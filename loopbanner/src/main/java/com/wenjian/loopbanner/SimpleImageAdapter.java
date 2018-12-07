package com.wenjian.loopbanner;

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
    public void onBindView(ViewHolder holder, String data,int position) {
        ImageView itemView = (ImageView) holder.itemView;
        Glide.with(itemView.getContext())
                .load(data)
                .into(itemView);
    }


}
