package com.crossbow.wear;

import android.content.Context;

import com.crossbow.volley.toolbox.Crossbow;
import com.crossbow.volley.toolbox.CrossbowBuilder;
import com.crossbow.wear.toolbox.WearCrossbowBuilder;


/**
 * Created by Patrick on 11/01/14.
 */
public class CrossbowWear {

    private static CrossbowBuilder crossbowBuilder;

    private CrossbowWear(Context context) {
    }

    public static com.crossbow.volley.toolbox.Crossbow get(Context context) {
        if(crossbowBuilder == null) {
            crossbowBuilder = new WearCrossbowBuilder(context);
        }
        return Crossbow.get(context, crossbowBuilder);
    }
}
