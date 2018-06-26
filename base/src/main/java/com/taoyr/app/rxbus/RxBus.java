package com.taoyr.app.rxbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

/**
 * Created by taoyr on 2018/1/9.
 */

public class RxBus {

    private final Relay<Object> mBus = PublishRelay.create().toSerialized();

    public void send(Object o) {
        mBus.accept(o);
    }

    public Flowable<Object> asFlowable() {
        return mBus.toFlowable(BackpressureStrategy.LATEST);
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }
}
