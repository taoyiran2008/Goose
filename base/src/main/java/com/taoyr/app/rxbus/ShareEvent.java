package com.taoyr.app.rxbus;

/**
 * Created by taoyiran on 2018/1/16.
 */

public class ShareEvent extends RxEvent {

    public String shareCode;

    public ShareEvent(String shareCode) {
        this.shareCode = shareCode;
    }
}
