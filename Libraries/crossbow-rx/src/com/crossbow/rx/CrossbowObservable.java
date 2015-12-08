package com.crossbow.rx;

import com.android.volley.Request;
import com.android.volley.SyncResponse;
import com.crossbow.volley.toolbox.Crossbow;

import rx.Observable;

public class CrossbowObservable {

    public static <T> Observable<T> create(Crossbow crossbow, final Request<T> request) {
        return Observable.create(new CrossbowSubscriber<>(crossbow, request)).subscribeOn(CrossbowSchedulers.network());
    }

    public static class CrossbowSubscriber<T> implements Observable.OnSubscribe<T> {

        final Crossbow crossbow;
        final Request<T> request;

        public CrossbowSubscriber(Crossbow crossbow, Request<T> request) {
            this.crossbow = crossbow;
            this.request = request;
        }

        @Override
        public void call(rx.Subscriber<? super T> subscriber) {
            SyncResponse<T> data = crossbow.sync(request);
            if (!subscriber.isUnsubscribed() && !request.isCanceled()) {
                if (data.isSuccess()) {
                    subscriber.onNext(data.data);
                } else {
                    subscriber.onError(data.volleyError);
                }
            } else {
                request.cancel();
            }
            subscriber.onCompleted();
        }
    }
}
