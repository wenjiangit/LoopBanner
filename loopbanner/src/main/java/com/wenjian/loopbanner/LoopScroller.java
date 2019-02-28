package com.wenjian.loopbanner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Description LoopScroller
 * <p>
 * Date 2019/2/28
 *
 * @author wenjianes@163.com
 */
public class LoopScroller extends Scroller {

    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private int mScrollerDuration;

    void setScrollerDuration(int mScrollerDuration) {
        this.mScrollerDuration = mScrollerDuration;
    }

    LoopScroller(Context context) {
        super(context, sInterpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        this.startScroll(startX, startY, dx, dy, mScrollerDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollerDuration);
    }

    void linkViewPager(ViewPager viewPager) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(viewPager, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
