package com.goose.app.ui.search;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.model.PictureInfo;
import com.goose.app.widgets.controller.PictureListController;
import com.goose.app.widgets.controller.ProductListController;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.utility.CommonUtils;
import com.taoyr.app.utility.PictureUtils;
import com.taoyr.pulltorefresh.PullToRefreshListener;
import com.taoyr.pulltorefresh.PullToRefreshViewGroup;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lankton.flowlayout.FlowLayout;

/**
 * Created by taoyr on 2018/1/6.
 */

public class SearchActivity extends BaseActivity<SearchContract.Presenter>
        implements SearchContract.View, TextWatcher {

    @BindView(R.id.ll_left_area)
    LinearLayout ll_left_area;

    @BindView(R.id.edt_content_filter)
    EditText edt_content_filter;

    @BindView(R.id.txt_search)
    TextView txt_search;

    @BindView(R.id.txt_status)
    TextView txt_status;

    @BindView(R.id.flowlayout)
    FlowLayout flowlayout;

    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.pull_to_refresh)
    PullToRefreshViewGroup pull_to_refresh;

    public static final int PAGE_COUNT = 15;

    private List<PictureInfo> mList = new ArrayList<>();
    private int mCurrentPage = 1;
    private String mKeywords;
    private String mCategory;
    private String mType;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_search;
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mKeywords = intent.getStringExtra(Configs.EXTRA_TAG);
        mType = intent.getStringExtra(Configs.EXTRA_TYPE);
        mCategory = intent.getStringExtra(Configs.EXTRA_CATEGORY);

    }


    @OnClick({R.id.ll_left_area, R.id.txt_search})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left_area:
                finish();
                break;
            case R.id.txt_search:
                mKeywords = edt_content_filter.getText().toString();
                mCurrentPage = 1;
                mPresenter.search(mCurrentPage, PAGE_COUNT, mKeywords, mCategory, mType, false);
                break;
        }
    }

    @Override
    protected void initView() {
        edt_content_filter.addTextChangedListener(this);
        CommonUtils.disableView(txt_search, true);

        Set<String> set = mSp.getStringSet("searchList", new HashSet<String>());
        for (String key : set) {
            addToFlowlayout(key);
        }

        // 需要一个延时，不然页面没加载完全，软键盘（也没加载完全）弹不出来
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtils.requestFocusAndShowKeyboard(edt_content_filter);
            }
        }, 300);
        if(mKeywords!=null&&!"".equals(mKeywords)){
            edt_content_filter.setText(mKeywords);
            mPresenter.search(mCurrentPage, PAGE_COUNT, mKeywords, mCategory, mType, false);
        }
        base_recycler_view.initialize(new ProductListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 1,0);

        showSearchResult(false);

        initPullToRefreshWidget();

        edt_content_filter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (TextUtils.isEmpty(edt_content_filter.getText())) {
                        showToast("内容不能为空");
                    } else {
                        mKeywords = edt_content_filter.getText().toString();
                        mCurrentPage = 1;
                        mPresenter.search(mCurrentPage, PAGE_COUNT, mKeywords,mCategory,mKeywords ,false);
                    }
                }
                return false;
            }
        });
    }

    private void initPullToRefreshWidget() {
        pull_to_refresh.setTheme(PullToRefreshViewGroup.THEME_BLUE_TONE);
        // 禁止下拉刷新
        pull_to_refresh.setHeaderEnable(false);
        pull_to_refresh.setFooterEnable(true);
        //pull_to_refresh.setAlwaysEnablePullUp(true);
        pull_to_refresh.setOnRefreshListener(new PullToRefreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                mPresenter.search(mCurrentPage, PAGE_COUNT, mKeywords,mCategory,mType, true);
            }
        }, hashCode());
    }

    private void addToFlowlayout(final String keywords) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final TextView tv = new TextView(mContext);
                flowlayout.addView(tv);
                tv.setText(keywords);
                tv.setPadding(PictureUtils.dip2px(mContext, 20), PictureUtils.dip2px(mContext, 10),
                        PictureUtils.dip2px(mContext, 20), PictureUtils.dip2px(mContext, 10));
                tv.setTextColor(getResources().getColor(R.color.black));
                tv.setBackgroundResource(R.drawable.shape_search_item_bg);
                // After addView()
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
                lp.setMargins(0, PictureUtils.dip2px(mContext, 10), PictureUtils.dip2px(mContext, 10), 0);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mKeywords = tv.getText().toString();
                        mCurrentPage = 1;
                        mPresenter.search(mCurrentPage, PAGE_COUNT, mKeywords,mCategory,mType, false);
                    }
                });
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
            CommonUtils.disableView(txt_search, false);
        } else {
            CommonUtils.disableView(txt_search, true);
        }
    }

    @Override
    public void addHistoryTag(String keywords) {
        if (!TextUtils.isEmpty(keywords)) {
            // 保存搜索历史
            if (keywords.length() > 10) {
                keywords = keywords.substring(0, 10);
            }

            // hash set是按照元素的hash code进行排序和内存映射的，因此是无序的（跟添加顺序无关）
            // ，不能使用类似下标操作集合（访问，删除）。
            Set<String> set = mSp.getStringSet("searchList", new HashSet<String>());
            if (!set.contains(keywords)) {
                int i = 0;
                Set<String> newSet = new HashSet<String>();
                for (String key : set) {
                    newSet.add(key);
                    if (i >= 5) break; // 最多只保留6个历史记录
                }
                newSet.add(keywords);
                mSp.edit().putStringSet("searchList", newSet).commit();
            }

            //addToFlowlayout(keywords);
            showSearchResult(true);
        }
    }

    @Override
    public void refreshList(List<PictureInfo> list, boolean loadMore) {
        pull_to_refresh.finishLoading();
        pull_to_refresh.finishRefreshing(true);

        if (list == null || list.isEmpty()) {
            if (!loadMore) {
                base_recycler_view.refresh(list);
            }
            return; // No more data
        }

        mCurrentPage++;

        if (loadMore) {
            mList.addAll(list);
            base_recycler_view.refresh(mList);
        } else {
            mList = list;
            base_recycler_view.refresh(mList);
        }
    }

    private void showSearchResult(final boolean show) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    pull_to_refresh.setVisibility(View.VISIBLE);
                    flowlayout.setVisibility(View.GONE);
                    txt_status.setText("搜索结果");
                    txt_status.setVisibility(View.GONE);
                } else {
                    pull_to_refresh.setVisibility(View.GONE);
                    flowlayout.setVisibility(View.VISIBLE);
                    txt_status.setText("历史搜索");
                    txt_status.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
