package com.crossbow.wear.receiver;


import android.support.annotation.CallSuper;

import com.android.volley.RequestQueue;
import com.crossbow.volley.toolbox.Crossbow;
import com.crossbow.wear.core.ResponseTransformer;
import com.crossbow.wear.core.WearFlags;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Patrick on 11/07/2015.
 */
public abstract class CrossbowRemoteService extends WearableListenerService {

    //Queue of buffered events to hold events while the repeater service is connected

    private WearRequestHandler dataItemHandler;
    private Map<String, ResponseTransformer> transformerMap = new HashMap<>();

    public CrossbowRemoteService() {
        transformerMap.put(WearFlags.IMAGE_TRANSFORMER_KEY, new ImageRequestTransformer());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, ResponseTransformer> extraTransFormers = getTransformerMap();
        if(extraTransFormers != null) {
            transformerMap.putAll(extraTransFormers);
        }

        RequestQueue requestQueue = getRequestQueue();
        dataItemHandler = new WearRequestHandler(this, requestQueue, transformerMap);
    }

    public Map<String, ResponseTransformer> getTransformerMap() {
        return Collections.emptyMap();
    }

    public RequestQueue getRequestQueue() {
        return Crossbow.get(this).getRequestQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataItemHandler.disconnect();
    }

    protected boolean isCrossBowMessage(MessageEvent messageEvent) {
        return WearRequestHandler.isWearMessage(messageEvent);
    }

    @Override
    @CallSuper
    public void onDataChanged(DataEventBuffer dataEvents) {
    }

    @Override
    @CallSuper
    public void onMessageReceived(MessageEvent messageEvent) {
        if(isCrossBowMessage(messageEvent)) {
            dataItemHandler.sendRequest(messageEvent);
        }
    }

    @Override
    @CallSuper
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
    }

    @Override
    @CallSuper
    public void onPeerConnected(Node peer) {
    }

    @Override
    @CallSuper
    public void onPeerDisconnected(Node peer) {
    }

    @Override
    @CallSuper
    public void onConnectedNodes(List<Node> connectedNodes) {
    }

    @Override
    @CallSuper
    public void onChannelOpened(Channel channel) {
    }

    @Override
    @CallSuper
    public void onChannelClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
    }

    @Override
    @CallSuper
    public void onInputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
    }

    @Override
    @CallSuper
    public void onOutputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
    }


}
