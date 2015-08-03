package com.crossbow.wear.toolbox;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.toolbox.HttpStack;
import com.crossbow.volley.toolbox.DefaultCrossbowBuilder;
import com.crossbow.wear.PlayNetwork;

/**
 * Used to get the correct version of WearCrossbowBuilder that uses the {@link PlayNetwork} to transmit
 * data to the handheld. If you are writing a custom stack please use the {@link PlayNetwork} as
 * the network or request will not be sent to wearable.
 */
public class WearCrossbowBuilder extends DefaultCrossbowBuilder {

    public WearCrossbowBuilder(Context context) {
        super(context);
    }

    @Override
    public Network onCreateNetwork(HttpStack httpStack) {
        return new PlayNetwork(getContext());
    }
}
