package com.wenjian.loopbanner.indicator;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Description: IndicatorAdapter
 * Date: 2018/12/6
 *
 * @author jian.wen@ubtrobot.com
 */
public interface IndicatorAdapter {

    /**
     * 添加子indicator
     *
     * @param container 父布局
     * @param drawable  配置的Drawable
     * @param size      配置的指示器大小
     * @param margin    配置的指示器margin值
     */
    void addIndicator(LinearLayout container, Drawable drawable, int size, int margin);

    /**
     * 应用选中效果
     *
     * @param prev    上一个
     * @param current 当前
     * @param reverse 是否逆向滑动
     */
    void applySelectState(View prev, View current, boolean reverse);

    /**
     * 应用为选中效果
     *
     * @param indicator 指示器
     */
    void applyUnSelectState(View indicator);


    /**
     * 是否需要对某个位置进行特殊处理
     *
     * @param container 指示器容器
     * @param position  第一个或最后一个
     * @return 返回true代表处理好了
     */
    boolean handleSpecial(LinearLayout container, int position);


}
