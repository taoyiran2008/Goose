package com.goose.app.rxbus;

import com.taoyr.app.rxbus.RxEvent;

/**
 * Created by taoyr on 2018/7/18.
 */

public class RefreshProductEvent extends RxEvent {

    public String productType;
    public String categoryCode;

    public RefreshProductEvent(String productType, String categoryCode) {
        this.productType = productType;
        this.categoryCode = categoryCode;
    }
}
