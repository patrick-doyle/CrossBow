package com.crossbow.wear;

import android.content.Context;

import com.crossbow.volley.toolbox.Crossbow;
import com.crossbow.wear.toolbox.WearCrossbowComponents;


/**
 * Used to get the correct version of crossbow that uses the {@link PlayNetwork} to transmit data to the
 * handheld.
 */
public class CrossbowWear {

    private static Crossbow crossbow;

    public static Crossbow get(Context context) {
        if(crossbow == null) {
            //only create the WearCrossbowComponents once.
            WearCrossbowComponents crossbowBuilder = new WearCrossbowComponents(context);
            crossbow = new Crossbow(context, crossbowBuilder);
        }
        return crossbow;
    }
}
