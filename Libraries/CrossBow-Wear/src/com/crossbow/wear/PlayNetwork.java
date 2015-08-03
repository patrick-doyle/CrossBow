package com.crossbow.wear;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.crossbow.wear.core.RequestSerialUtil;
import com.crossbow.wear.core.WearNetworkResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Custom network that uses play services to send the requests to the handheld instead of the
 * http stack
 */
public class PlayNetwork implements Network, MessageApi.MessageListener {

    private GoogleApiClient googleApiClient;
    private final boolean serviceAvaiable;
    private final int playServicesAvaibleCode;

    private final Map<String, CountDownLatch> latchMap = Collections.synchronizedMap(new HashMap<String, CountDownLatch>());
    private final Map<String, WearNetworkResponse> responsesMap = Collections.synchronizedMap(new HashMap<String, WearNetworkResponse>());
    private final Set<String> inflightUuids = Collections.synchronizedSet(new HashSet<String>());
    private Context context;

    public PlayNetwork(Context context) {
        this.context = context.getApplicationContext();
        this.googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        playServicesAvaibleCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        serviceAvaiable = playServicesAvaibleCode == ConnectionResult.SUCCESS;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {

        if(!serviceAvaiable) {
            throwErrorForCode(playServicesAvaibleCode);
        }

        Log.d("Play Network", "performRequest start");
        final long timeOut = request.getTimeoutMs();

        ConnectionResult connectionResult = googleApiClient.blockingConnect(timeOut, TimeUnit.MILLISECONDS);

        if (!connectionResult.isSuccess()) {
            int code = connectionResult.getErrorCode();
            throwErrorForCode(code);
        }
        //add the listeners for the data response
        Wearable.MessageApi.addListener(googleApiClient, this);

        //Request the nodes that can handle the request
        String nodeID = getBestNode(timeOut);

        //We are good to go
        //Serialize the request an send it to the phone
        String uuid = UUID.randomUUID().toString();

        String wearMessageUrl = getDataRequestKey(uuid);
        Log.d("Play Network", "performRequest serialization");

        byte[] data;
        try {
            data = RequestSerialUtil.serializeRequest(uuid, request);
        } catch (IOException e) {
            throw new VolleyError("cant serialize the request", e);
        }
        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, nodeID, wearMessageUrl, data).await(timeOut, TimeUnit.MILLISECONDS);

        if(!result.getStatus().isSuccess()) {
            throw new NoConnectionError(new VolleyError("Failed to send message to device"));
        }

        inflightUuids.add(uuid);

        //block the thread until the data is received or times out
        CountDownLatch latch = new CountDownLatch(1);
        latchMap.put(uuid, latch);
        try {
            Log.d("Play Network", "performRequest latching");
            latch.await(timeOut * 2, TimeUnit.MILLISECONDS);
            Log.d("Play Network", "latch released");
            latchMap.remove(uuid);
        }
        catch (InterruptedException e) {
            Log.d("Play Network", "latch error");
            throw new VolleyError("Request Timeout talking to handheld", e);
        }

        WearNetworkResponse networkResponse = responsesMap.get(uuid);
        responsesMap.remove(uuid);

        if(!networkResponse.success) {
            throw new VolleyError("No response in map");
        }
        return networkResponse.getNetworkResponse();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().contains("crossbow_wear")) {
            byte[] data = messageEvent.getData();
            WearNetworkResponse wearNetworkResponse = WearNetworkResponse.fromByteArray(data);
            Log.d("Play Network", "request response returned");

            String uuid = wearNetworkResponse.uuid;
            responsesMap.put(uuid, wearNetworkResponse);
            if(latchMap.containsKey(uuid)) {
                //unlatch the correct thread
                Log.d("Play Network", "unlatching thread");
                latchMap.get(uuid).countDown();
            }
        }
    }

    private String getBestNode(long timeOut) throws VolleyError {
        final String nodeCompatibilty = context.getString(R.string.crossbow_compatibility);
        CapabilityApi.GetCapabilityResult nodes = Wearable.CapabilityApi.getCapability(googleApiClient, nodeCompatibilty, CapabilityApi.FILTER_REACHABLE).await(timeOut, TimeUnit.MILLISECONDS);
        String nodeID = null;
        Set<Node> nodeList = nodes.getCapability().getNodes();
        if(nodeList.isEmpty()) {
            throw new NoConnectionError(new VolleyError("No nodes found to handle the request"));
        }

        //get the nearest node
        for(Node node : nodeList) {
            if(node.isNearby()) {
                return node.getId();
            }
            nodeID = node.getId();
        }
        return nodeID;
    }

    private static String getDataRequestKey(String uuid) throws AuthFailureError{
        return "/crossbow_wear/" + uuid;
    }

    private static void throwErrorForCode(int code) throws VolleyError {

        final String message;

        switch (code) {
            case ConnectionResult.SERVICE_MISSING:
                message = "Play Services is missing";
                break;
            case ConnectionResult.SERVICE_UPDATING:
                message = "Play Services is updating";
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                message = "Play Services is running the wrong version";
                break;
            case ConnectionResult.SERVICE_DISABLED:
                message = "Play Services is disabled";
                break;
            case ConnectionResult.SERVICE_INVALID:
                message = "Play Services is invalid";
                break;
            default:
                message = "Connection to play services failed";
        }

        throw new VolleyError(message);
    }
}
