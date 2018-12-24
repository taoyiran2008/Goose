package com.goose.app.model;

import java.io.Serializable;

/**
 * Created by taoyr on 2018/6/25.
 */

public class PictureInfo implements Serializable {

    public String id;
    public String type;
    public String title;
    public String tag;
    public String url;
    public String cover;
    public String category;
    public int view;
    public int support;
    public int collect;
    public String createTime;
    public float download_price;
    public int view_price;
    public int star;
}
