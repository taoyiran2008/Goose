package com.taoyr.widget.widgets.progress.glide;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.taoyr.widget.widgets.progress.ProgressListener;

import java.io.InputStream;

/**
 * Created by taoyr on 2018/3/26.
 */

public class ProgressModelLoader implements StreamModelLoader<String> {

    private ProgressListener mProgressListener;
    private ProgressDataFetcher mProgressDataFetcher;

    public ProgressModelLoader(ProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
        // 这里不能直接return new ProgressDataFetcher()
        // 这样会造成ProgressDataFetcher#loadData请求数据时抛出thread interrupted的错误（资源不足导致？）
        if (mProgressDataFetcher == null) {
            mProgressDataFetcher = new ProgressDataFetcher(model, mProgressListener);
        }
        return mProgressDataFetcher;
    }
}
