package com.crossbow.wear.receiver;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.crossbow.wear.core.RequestSerialUtil;
import com.crossbow.wear.core.ResponseTransformer;
import com.crossbow.wear.core.WearDataRequest;
import com.crossbow.wear.core.WearNetworkResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Handles the creation and registering of the google play services client as well as
 * sending the request from the wearable and send the response from the wearable
 */
public class WearRequestHandler {

    private final Map<String, ResponseTransformer> transformerMap = new HashMap<>();
    private RequestQueue requestQueue;
    private GoogleApiClient googleApiClient;
    private Queue<MessageEvent> messages = new LinkedList<>();
    boolean googlePlayServicesConnected = false;

    public WearRequestHandler(Context context, RequestQueue requestQueue, Map<String, ResponseTransformer> transformerMap) {
        this.requestQueue = requestQueue;
        this.transformerMap.putAll(transformerMap);
        googleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        googleApiClient.connect();
    }

    public static boolean isWearMessage(MessageEvent messageEvent) {
        return messageEvent.getPath().contains("crossbow_wear");
    }

    public void sendRequest(MessageEvent messageEvent) {
        if(googlePlayServicesConnected) {
            setWearRequest(messageEvent);
        }
        else {
            messages.add(messageEvent);
        }
    }

    public void disconnect() {
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void setWearRequest(MessageEvent messageEvent) {
        byte[] data = messageEvent.getData();
        final String sourceNodeID = messageEvent.getSourceNodeId();
        try {
            WearDataRequest wearDataRequest = RequestSerialUtil.deSerializeRequest(data);
            wearDataRequest.setTransformerMap(transformerMap);
            wearDataRequest.setListener(new WearDataRequest.Listener() {
                @Override
                public void onResponse(String uuid, NetworkResponse response) {
                    sendResultToWear(sourceNodeID, true, uuid, response);
                }
            });
            wearDataRequest.setErrorListener(new WearDataRequest.ErrorListener() {
                @Override
                public void onErrorResponse(String uuid, VolleyError error) {
                    sendResultToWear(sourceNodeID, true, uuid, null);
                }
            });
            requestQueue.add(wearDataRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPendingRequests() {
        while (!messages.isEmpty()) {
            setWearRequest(messages.poll());
        }
    }

    private void sendResultToWear(String sourceNodeID, boolean success, String uuid, NetworkResponse networkResponse) {
        WearNetworkResponse wearNetworkResponse = new WearNetworkResponse(success, uuid, networkResponse);
        byte[] data = wearNetworkResponse.toByteArray();
        PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(googleApiClient, sourceNodeID, "/crossbow_wear/" + uuid, data);
        result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                Log.i("TAG", "Request sent back result = " + sendMessageResult.getStatus().isSuccess());
            }
        });
    }

    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            googlePlayServicesConnected = true;
            sendPendingRequests();
        }

        @Override
        public void onConnectionSuspended(int i) {
            googlePlayServicesConnected = false;
        }
    };

    private final GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            googlePlayServicesConnected = false;
        }
    };

}
