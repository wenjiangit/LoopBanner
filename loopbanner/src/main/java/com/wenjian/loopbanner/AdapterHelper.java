package com.wenjian.loopbanner;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

class AdapterHelper {

    private int pageMargin;
    private int lrMargin;

    AdapterHelper(int pageMargin, int lrMargin) {
        this.pageMargin = pageMargin;
        this.lrMargin = lrMargin;
    }

    void onCreateViewHolder(@NonNull ViewGroup parent, View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = parent.getWidth() - lrMargin * 2;
        view.setLayoutParams(lp);
    }

    void onBindViewHolder(@NonNull LoopAdapter.ViewHolder holder) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        lp.leftMargin = pageMargin / 2;
        lp.rightMargin = pageMargin / 2;
        holder.itemView.setLayoutParams(lp);
    }

}
