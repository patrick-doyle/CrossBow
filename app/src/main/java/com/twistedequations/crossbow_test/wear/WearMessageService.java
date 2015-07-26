package com.twistedequations.crossbow_test.wear;

import android.widget.Toast;

import com.crossbow.wear.core.ResponseTransformer;
import com.crossbow.wear.receiver.CrossbowRemoteService;
import com.google.android.gms.wearable.MessageEvent;

import java.util.Map;

/**
 * Created by Patrick on 25/07/2015.
 */
public class WearMessageService extends CrossbowRemoteService {

    @Override
    public Map<String, ResponseTransformer> getTransformerMap() {
        return super.getTransformerMap();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if(!isCrossBowMessage(messageEvent)) {
            String path = messageEvent.getPath();
            String data = new String(messageEvent.getData());

            Toast.makeText(this, "Message Received from Path - " + path + ", data = " + data, Toast.LENGTH_SHORT).show();
        }
    }
}
