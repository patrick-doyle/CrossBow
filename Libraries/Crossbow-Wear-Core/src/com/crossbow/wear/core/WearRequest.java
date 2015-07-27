package com.crossbow.wear.core;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Patrick on 10/07/2015.
 */
public interface WearRequest {

    public @Nullable Bundle getTransformerParams();

    public @Nullable String getTransFormerKey();
}
