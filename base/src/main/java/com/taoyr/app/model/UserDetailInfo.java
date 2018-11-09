package com.taoyr.app.model;

import java.io.Serializable;

/**
 * Created by taoyiran on 2018/1/24.
 */

public class UserDetailInfo implements Serializable {

    public String uid;
    public String username;
    public String displayName;
    public String sex; // MALE, FEMALE, NONE（保密）
    public String avatar; // 头像地址
    public String lastSignTime; // 2018-05-30 11:14:19
    public String token;
    public int coin;
    public String shareCode;
}
