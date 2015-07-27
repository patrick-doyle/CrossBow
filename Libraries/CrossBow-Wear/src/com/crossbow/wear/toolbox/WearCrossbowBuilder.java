package com.crossbow.wear.toolbox;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.toolbox.HttpStack;
import com.crossbow.volley.toolbox.CrossbowBuilder;
import com.crossbow.wear.PlayNetwork;

/**
 * Created by Patrick on 27/07/2015.
 */
public class WearCrossbowBuilder extends CrossbowBuilder {

    public WearCrossbowBuilder(Context context) {
        super(context);
    }

    @Override
    public Network createNetwork(HttpStack httpStack) {
        return new PlayNetwork(getContext());
    }
}
