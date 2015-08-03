package com.crossbow.wear.receiver;


import android.support.annotation.CallSuper;

import com.android.volley.RequestQueue;
import com.crossbow.volley.toolbox.Crossbow;
import com.crossbow.wear.core.ResponseTransformer;
import com.crossbow.wear.core.WearConstants;
import com.crossbow.wear.core.WearRequest;
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
 * Base service for receiving requests form the wearable.
 * Your WearableListenerService should extend this class instead.
 * Any request transformers should be registered in the {@link #onGetTransformerMap()} and if you are using a
 * custom CrossbowBuilder you need to set it with the {@link #onGetRequestQueue()} method
 */
public class CrossbowListenerService extends WearableListenerService {

    //Queue of buffered events to hold events while the repeater service is connected

    private WearRequestHandler dataItemHandler;
    private Map<String, ResponseTransformer> transformerMap = new HashMap<>();

    public CrossbowListenerService() {
        transformerMap.put(WearConstants.IMAGE_TRANSFORMER_KEY, new ImageRequestTransformer());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, ResponseTransformer> extraTransFormers = onGetTransformerMap();
        if(extraTransFormers != null) {
            transformerMap.putAll(extraTransFormers);
        }

        RequestQueue requestQueue = onGetRequestQueue();
        dataItemHandler = new WearRequestHandler(this, requestQueue, transformerMap);
    }

    /**
     * Used to register {@link ResponseTransformer}s needed to shrink network responses to a
     * manageable size for the wearable
     *
     * @return a map of transformer keys to ResponseTransformer that match the key returned
     * from the {@link WearRequest#getTransFormerKey()} method
     */
    public Map<String, ResponseTransformer> onGetTransformerMap() {
        return Collections.emptyMap();
    }

    /**
     * If you using a custom set of crossbow/volley components you need to return the correct request
     * queue here or a second request queue will be creating ignoring your custom set up. If you are using the default
     * setup via
     * <code>
     *     <pre>
     *         Crossbow.get(this).getRequestQueue()
     *     </pre>
     * </code>
     * the you do not need to override this method.
     * @return the request queue you are using.
     */
    public RequestQueue onGetRequestQueue() {
        return Crossbow.get(this).getRequestQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataItemHandler.disconnect();
    }

    /**
     * Used to tell if the message is from the watch. If this returns true you can ignore the message as it will be
     * handled by the library
     * @param messageEvent the message event received from {@link #onMessageReceived(MessageEvent)}
     * @return true if the message was sent by crossbow wear from the wearable
     */
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
