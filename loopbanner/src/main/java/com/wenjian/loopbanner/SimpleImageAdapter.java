package com.wenjian.loopbanner;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Description: SimpleImageAdapter
 * Date: 2018/12/4
 *
 * @author jian.wen@ubtrobot.com
 */
class SimpleImageAdapter extends LoopAdapter<String> {

    SimpleImageAdapter(List<String> data) {
        super(data);
    }

    @Override
    public void onBindView(ViewHolder holder, String data, final int position) {
        ImageView itemView = (ImageView) holder.itemView;
        Glide.with(itemView.getContext())
                .load(data)
                .into(itemView);
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onPageClick(v, position);
                }
            });
        }

    }


}
