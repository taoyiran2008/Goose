package com.taoyr.app.utility;

/**
 * Created by taoyr on 2018/3/26.
 */

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
