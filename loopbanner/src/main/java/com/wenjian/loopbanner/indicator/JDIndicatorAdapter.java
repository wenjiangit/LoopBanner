package com.wenjian.loopbanner.indicator;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wenjian.loopbanner.R;

/**
 * Description: JDIndicatorAdapter
 * Date: 2018/12/6
 *
 * @author jian.wen@ubtrobot.com
 */
public class JDIndicatorAdapter implements IndicatorAdapter {

    private final int drawableId;

    private boolean initialed = false;
    private float mScale;

    public JDIndicatorAdapter(int drawableId) {
        this.drawableId = drawableId;
    }

    public JDIndicatorAdapter() {
        this(R.drawable.indicator_jd);
    }

    @Override
    public void addIndicator(LinearLayout container, Drawable drawable, int size, int margin) {
        drawable = ContextCompat.getDrawable(container.getContext(), drawableId);
        if (drawable == null) {
            throw new IllegalArgumentException("please provide valid drawableId");
        }
        ImageView image = new ImageView(container.getContext());
        ViewCompat.setBackground(image, drawable);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = margin;
        container.addView(image, params);

        computeScale(drawable.getMinimumWidth(), margin);

    }

    @Override
    public void applySelectState(View prev, View current, boolean reverse) {
        prev.setPivotX(0);
        prev.setPivotY(prev.getHeight() >> 1);
        if (reverse) {
            current.animate().scaleX(1).setDuration(200).start();
        } else {
            prev.animate().scaleX(mScale).setDuration(200).start();
        }
    }

    @Override
    public void applyUnSelectState(View indicator) {

    }

    @Override
    public boolean handleSpecial(LinearLayout container, int position) {
        int childCount = container.getChildCount();
        //对第一个和最后一个做特殊处理
        if (position == 0 || position == childCount - 1) {
            for (int i = 0; i < childCount; i++) {
                View childAt = container.getChildAt(i);
                childAt.setPivotX(0);
                childAt.setPivotY(childAt.getHeight() >> 1);
                //第一个
                if (position == 0) {
                    childAt.animate().scaleX(1).setDuration(200).start();
                }
                //最后一个
                else {
                    if (i != childCount - 1) {
                        childAt.animate().scaleX(mScale).setDuration(200).start();
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void computeScale(int width, int margin) {
        if (!initialed) {
            mScale = width == 0 ? 2 : ((width + margin + width / 2) * 1f) / width;
            initialed = true;
        }
    }

}
