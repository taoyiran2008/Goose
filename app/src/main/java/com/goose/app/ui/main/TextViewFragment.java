package com.goose.app.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goose.app.R;

import javax.inject.Inject;

/**
 * Created by taoyr on 2017/10/11.
 */

public class TextViewFragment extends Fragment {

    private View mRoot;

    @Inject
    public TextViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_textview, null);
        initView();
        return mRoot;
    }

    private void initView() {
    }
}
