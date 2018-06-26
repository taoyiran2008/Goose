package com.taoyr.widget.widgets.autosrollviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.taoyr.widget.widgets.progress.ProgressImageView;

import java.util.List;

/**
 * Created by taoyiran on 2018/2/8.
 */

public class BannerPagerAdapter extends PagerAdapter {

    Context mContext;
    //int mData[] = {R.drawable.default_pic_01, R.drawable.default_pic_02, R.drawable.default_pic_03};
    List<BannerInfo> mList;

    public BannerPagerAdapter(Context context, List<BannerInfo> list) {
        mContext = context;
        mList = list;
    }

    public void refresh(List<BannerInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // java.lang.UnsupportedOperationException: Required method destroyItem was not overridden
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView(mViewList.get(position));
        container.removeView((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        //ImageView view = new ImageView(mContext);
        //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //view.setImageResource(mData[position]);
        //view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //PictureLoader.loadImageViaGlide(view, UrlConfig.getServerFilePath(mList.get(position).imagePath),
        //       -1, -1, false, false, null);
        ProgressImageView view = new ProgressImageView(mContext);
        // 进度支持得考虑get请求的content-length是否是对的（如果后台服务端未做处理返回0，则进度显示失败，ProgressListener会报divide by zero Exception）
        view.loadImage(mList.get(position).imagePath);
        // 测试用高清图片（2.5M）
        //view.loadImage("http://img.tuku.cn/file_big/201502/89448ed96e524552a46abce14fab2eb8.jpg");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra(Configs.EXTRA_URL, mList.get(position).link);
                mContext.startActivity(intent);*/
            }
        });
        container.addView(view);

        return view;
    }
}
