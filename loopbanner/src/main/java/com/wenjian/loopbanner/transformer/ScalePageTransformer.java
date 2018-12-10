package com.wenjian.loopbanner.transformer;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Description: ScalePageTransformer
 * Date: 2018/12/10
 *
 * @author jian.wen@ubtrobot.com
 */
public class ScalePageTransformer implements ViewPager.PageTransformer {

    private static final float DEFAULT_SCALE = 0.85f;
    private float mScale;

    public ScalePageTransformer(float mScale) {
        this.mScale = mScale;
    }

    public ScalePageTransformer() {
        this(DEFAULT_SCALE);
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        int width = view.getWidth();
        int height = view.getHeight();
        view.setPivotX(width / 2);
        view.setPivotY(height / 2);
        if (position < -1) {
            view.setScaleX(mScale);
            view.setScaleY(mScale);
            view.setPivotX(width);
        } else if (position <= 1) {
            if (position < 0) {
                float scaleFactor = (1 + position) * (1 - mScale) + mScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(width * (1 + (1 * -position)));
            } else {
                float scaleFactor = (1 - position) * (1 - mScale) + mScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(width * ((1 - position) * 1));
            }
        } else {
            view.setPivotX(0);
            view.setScaleX(mScale);
            view.setScaleY(mScale);
        }
    }
}
