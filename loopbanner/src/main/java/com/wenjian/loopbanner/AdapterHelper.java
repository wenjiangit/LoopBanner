package com.wenjian.loopbanner;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Description AdapterHelper
 * <p>
 * Date 2019-09-01
 *
 * @author wenjianes@163.com
 */
class AdapterHelper {

    private int pageMargin;
    private int lrMargin;

    AdapterHelper(int pageMargin, int lrMargin) {
        this.pageMargin = pageMargin;
        this.lrMargin = lrMargin;
    }

    void onCreateViewHolder(@NonNull ViewGroup parent, View view) {
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = parent.getWidth() - lrMargin * 2;
        view.setLayoutParams(layoutParams);
    }


    void onBindViewHolder(@NonNull LoopAdapter.ViewHolder holder) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        lp.leftMargin = pageMargin / 2;
        lp.rightMargin = pageMargin / 2;
        holder.itemView.setLayoutParams(lp);
    }

}
