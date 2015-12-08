package com.crossbow.rx;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/** A {@link Scheduler} backed by a Fixed thread pool. */
public final class CrossbowScheduler extends Scheduler {

    public static CrossbowScheduler fixedPool(int poolSize) {
        return new CrossbowScheduler(poolSize);
    }

    private final int poolSize;

    CrossbowScheduler(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public Worker createWorker() {
        return new CrossbowWorker(poolSize);
    }

    private static class CrossbowWorker extends Worker {

        private final ScheduledExecutorService threadPoolExecutor;

        private final CompositeSubscription compositeSubscription = new CompositeSubscription();

        CrossbowWorker(int poolSize) {
            threadPoolExecutor = Executors.newScheduledThreadPool(poolSize, new CrossbowThreadFactory());
        }

        @Override
        public void unsubscribe() {
            compositeSubscription.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return compositeSubscription.isUnsubscribed();
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (compositeSubscription.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }

            final ScheduledAction scheduledAction = new ScheduledAction(action);
            scheduledAction.addParent(compositeSubscription);
            compositeSubscription.add(scheduledAction);

            threadPoolExecutor.schedule(scheduledAction, delayTime, unit);

            return scheduledAction;
        }

        @Override
        public Subscription schedule(final Action0 action) {
            return schedule(action, 0, TimeUnit.MILLISECONDS);
        }
    }

    static class CrossbowThreadFactory implements ThreadFactory {

        private static final AtomicLong COUNTER = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Crossbow Thread " + COUNTER.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}