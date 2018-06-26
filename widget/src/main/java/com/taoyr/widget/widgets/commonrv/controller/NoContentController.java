package com.taoyr.widget.widgets.commonrv.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taoyr.widget.R;
import com.taoyr.widget.widgets.commonrv.base.BaseRvController;

/**
 * Created by taoyr on 2017/9/22.
 */

public class NoContentController extends BaseRvController {
    @Override
    public RecyclerView.ViewHolder create(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_no_content, parent, false);
        return new RecyclerView.ViewHolder(itemView) {
        };
    }
}
