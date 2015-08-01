package com.crossbow.wear;

import android.content.Context;

import com.crossbow.volley.toolbox.Crossbow;
import com.crossbow.volley.toolbox.CrossbowBuilder;
import com.crossbow.wear.toolbox.WearCrossbowBuilder;


/**
 * Created by Patrick on 11/01/14.
 */
public class CrossbowWear {

    private static Crossbow crossbow;

    public static Crossbow get(Context context) {
        WearCrossbowBuilder crossbowBuilder = new WearCrossbowBuilder(context);
        if(crossbow == null) {
            crossbow = new Crossbow(context, crossbowBuilder);
        }
        return crossbow;
    }
}
