package com.crossbow.rx;

import rx.Scheduler;

public class CrossbowSchedulers {

    private static final int DEFAULT_POOL_SIZE = 4;

    private static final CrossbowSchedulers INSTANCE = new CrossbowSchedulers();
    private final Scheduler networkScheduler;

    public CrossbowSchedulers() {
        networkScheduler = CrossbowScheduler.fixedPool(DEFAULT_POOL_SIZE);
    }

    public static Scheduler network() {
        return INSTANCE.networkScheduler;
    }
}
