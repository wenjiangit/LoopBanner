package com.wenjian.loopbanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.wenjian.loopbanner.indicator.IndicatorAdapter;
import com.wenjian.loopbanner.indicator.JDIndicatorAdapter;
import com.wenjian.loopbanner.indicator.SelectDrawableAdapter;
import com.wenjian.loopbanner.transformer.ScalePageTransformer;

import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


/**
 * Description: LoopBanner
 * Date: 2018/12/4
 *
 * @author jian.wen@ubtrobot.com
 */
public class LoopBanner extends FrameLayout {

    private static final int GRAVITY_BOTTOM_LEFT = 1;
    private static final int GRAVITY_BOTTOM_RIGHT = 2;
    private static final int GRAVITY_BOTTOM_CENTER = 3;
    private static final int DEFAULT_GRAVITY = GRAVITY_BOTTOM_CENTER;
    private static final long DEFAULT_INTERVAL_TIME = 3000L;
    private static final int DEFAULT_PAGE_LIMIT = 2;
    private static final int DEFAULT_INDICATOR_SIZE = 6;
    private static final int DEFAULT_INDICATOR_MARGIN = 3;
    private static final int DEFAULT_INDICATOR_MARGIN_TO_PARENT = 16;
    private static final long TOUCH_DELAY = 4000;
    private static final String TAG = "LoopBanner";

    /**
     * page切换周期
     */
    private long mInterval;

    /**
     * page对于父布局的上边距
     */
    private int mTopMargin;

    /**
     * page对于父布局的下边距
     */
    private int mBottomMargin;

    /**
     * page相对于父布局的左右边距
     */
    private int mLrMargin;
    private RecyclerView mRecycler;
    /**
     * 离屏缓存page个数,和ViewPager的参数保持一致
     */
    private int mOffscreenPageLimit;
    /**
     * 每个page之间的间距
     */
    private int mPageMargin;
    /**
     * 当前选中位置
     */
    private int mCurrentIndex = -1;
    private final Handler mHandler = new Handler();
    /**
     * 循环滚动
     */
    private final Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAutoLoop) {
                mRecycler.smoothScrollToPosition(++mCurrentIndex);
                mHandler.postDelayed(this, mInterval);
            } else {
                mHandler.removeCallbacks(this);
            }
//            Tools.logI(TAG, "setCurrentItem" + mCurrentIndex);
        }
    };
    /**
     * 是否循环播放
     */
    private boolean mCanLoop = true;
    /**
     * 所有缩放
     */
    private float mLrScale;

    private FrameLayout.LayoutParams mParams;
    /**
     * 是否处于循环播放中
     */
    private boolean inLoop = false;
    /**
     * 指示器容器
     */
    private LinearLayout mIndicatorContainer;
    /**
     * 单个指示器的大小
     */
    private int mIndicatorSize;
    /**
     * 指示器间距
     */
    private int mIndicatorMargin;
    /**
     * 指示器适配
     */
    private IndicatorAdapter mIndicatorAdapter = new SelectDrawableAdapter();

    /**
     * 指示器的位置
     */
    private int mIndicatorGravity;
    /**
     * 是否显示指示器
     */
    private boolean mShowIndicator;
    /**
     * 选中监听
     */
    private OnPageSelectListener mSelectListener;
    /**
     * Indicator选中的资源
     */
    private Drawable mSelectDrawable;
    /**
     * Indicator未选中的资源
     */
    private Drawable mUnSelectDrawable;
    /**
     * 数据观察者
     */
    private final RecyclerView.AdapterDataObserver mDataSetObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            Tools.logI(TAG, "onChanged");
            restoreInitState();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            Log.d(TAG, "onItemRangeChanged: ");
        }
    };
    /**
     * mIndicatorContainer相对于父容器的垂直方向间距
     */
    private int mIndicatorParentMarginV;
    /**
     * mIndicatorContainer相对于父容器的水平方向间距
     */
    private int mIndicatorParentMarginH;
    private LinearLayoutManager mLayoutManager;
    private boolean mAutoLoop = true;

    public LoopBanner(@NonNull Context context) {
        this(context, null);
    }

    public LoopBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoopBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttr(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoopBanner, defStyleAttr, defStyleRes);
        mCanLoop = a.getBoolean(R.styleable.LoopBanner_lb_canLoop, true);
        mShowIndicator = a.getBoolean(R.styleable.LoopBanner_lb_showIndicator, true);
        mInterval = a.getInteger(R.styleable.LoopBanner_lb_interval, (int) DEFAULT_INTERVAL_TIME);
        mOffscreenPageLimit = a.getInteger(R.styleable.LoopBanner_lb_offsetPageLimit, DEFAULT_PAGE_LIMIT);
        mPageMargin = (int) a.getDimension(R.styleable.LoopBanner_lb_pageMargin, 0);
        final int margin = (int) a.getDimension(R.styleable.LoopBanner_lb_margin, 0);
        mLrMargin = (int) a.getDimension(R.styleable.LoopBanner_lb_lrMargin, margin);
        mTopMargin = (int) a.getDimension(R.styleable.LoopBanner_lb_topMargin, margin);
        mBottomMargin = (int) a.getDimension(R.styleable.LoopBanner_lb_bottomMargin, margin);
        mLrScale = a.getFloat(R.styleable.LoopBanner_lb_lrScale, 0f);
        //for indicator
        mIndicatorGravity = a.getInt(R.styleable.LoopBanner_lb_indicatorGravity, DEFAULT_GRAVITY);
        mIndicatorSize = (int) a.getDimension(R.styleable.LoopBanner_lb_indicatorSize, Tools.dp2px(context, DEFAULT_INDICATOR_SIZE));
        mIndicatorMargin = (int) a.getDimension(R.styleable.LoopBanner_lb_indicatorMargin, Tools.dp2px(context, DEFAULT_INDICATOR_MARGIN));
        mIndicatorParentMarginV = (int) a.getDimension(R.styleable.LoopBanner_lb_indicatorParentMarginV, Tools.dp2px(context, DEFAULT_INDICATOR_MARGIN_TO_PARENT));
        mIndicatorParentMarginH = (int) a.getDimension(R.styleable.LoopBanner_lb_indicatorParentMarginH, Tools.dp2px(context, DEFAULT_INDICATOR_MARGIN_TO_PARENT));
        mSelectDrawable = a.getDrawable(R.styleable.LoopBanner_lb_indicatorSelect);
        mUnSelectDrawable = a.getDrawable(R.styleable.LoopBanner_lb_indicatorUnSelect);
        int style = a.getInt(R.styleable.LoopBanner_lb_indicatorStyle, 0);
        handleIndicatorStyle(style);
        a.recycle();
        init();
    }

    private void handleIndicatorStyle(int style) {
        Style s;
        switch (style) {
            case 1:
                s = Style.JD;
                break;
            case 2:
                s = Style.PILL;
                break;
            default:
                s = Style.NORMAL;
        }
        setIndicatorStyle(s, false);
    }


    /**
     * 恢复初始状态
     */
    private void restoreInitState() {
        createIndicatorIfNeed();
        startInternal(true);
    }


    private void setProperIndex(int dataSize, int curIndex) {
        Tools.logI(TAG, "oldIndex: " + curIndex);
        int ret = Math.round(curIndex * 1.0f / dataSize + 0.5f) * dataSize;
        mCurrentIndex = ret >= 0 ? ret : 0;
        Tools.logI(TAG, "mCurrentIndex: " + mCurrentIndex);
    }

    private void init() {

        mRecycler = new RecyclerView(getContext());
        setupViewPager(mRecycler);

        mParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mRecycler, mParams);

        if (mShowIndicator) {
            initIndicatorContainer();
        }
    }

    private void initIndicatorContainer() {
        mIndicatorContainer = new LinearLayout(getContext());
        mIndicatorContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = createLayoutParams();
        this.addView(mIndicatorContainer, layoutParams);
    }

    public LayoutParams createLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = mIndicatorParentMarginV;
        switch (mIndicatorGravity) {
            case GRAVITY_BOTTOM_LEFT:
                layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
                layoutParams.leftMargin = mIndicatorParentMarginH + mLrMargin;
                break;
            case GRAVITY_BOTTOM_RIGHT:
                layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
                layoutParams.rightMargin = mIndicatorParentMarginH + mLrMargin;
                break;
            case GRAVITY_BOTTOM_CENTER:
                layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            default:
        }
        return layoutParams;
    }


    private int mLastPosition;

    private void setupViewPager(final RecyclerView viewPager) {

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(mLayoutManager);

        initFirstWidth();

        final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(viewPager);
        viewPager.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final View snapView = pagerSnapHelper.findSnapView(mLayoutManager);
                final int cur = viewPager.getChildAdapterPosition(snapView);
                if (cur != mLastPosition) {
                    Tools.logI(TAG, "onPageSelected: " + cur);
                    mCurrentIndex = cur;
                    notifySelectChange(cur);
                    updateIndicators(cur, mLastPosition);
                    mLastPosition = cur;
                }
                switch (newState) {
                    case SCROLL_STATE_DRAGGING:
                        stopInternal();
                        break;
                    case SCROLL_STATE_IDLE:
                        startInternal(false);
                        break;
                    default:
                }
            }
        });


//        if (mLrScale > 0 && mLrScale < 1) {
//            viewPager.setPageTransformer(false, new ScalePageTransformer(mLrScale));
//        }
    }

    private void initFirstWidth() {
        mRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mRecycler.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                seekToOriginPosition();
            }
        });
    }

    private void notifySelectChange(int position) {
        LoopAdapter adapter = getAdapter();
        if (adapter == null || adapter.getDataSize() == 0) {
            return;
        }

        if (mSelectListener != null) {
            mSelectListener.onPageSelected(adapter.getDataPosition(position));
        }
    }

    private void updateIndicators(int position, int lastPosition) {
        if (mIndicatorContainer == null) {
            return;
        }
        LoopAdapter adapter = getAdapter();
        if (adapter == null || adapter.getDataSize() <= 1) {
            return;
        }

        final int dataPosition = adapter.getDataPosition(position);
        if (mIndicatorAdapter.handleSpecial(mIndicatorContainer, dataPosition)) {
            return;
        }
        final int childCount = mIndicatorContainer.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                mIndicatorAdapter.applyUnSelectState(mIndicatorContainer.getChildAt(i));
            }
            boolean auto = lastPosition == position;
            int prev;
            if (auto) {
                prev = computePrevPosition(adapter, lastPosition - 1);
            } else {
                prev = computePrevPosition(adapter, lastPosition);
            }

            mIndicatorAdapter.applySelectState(mIndicatorContainer.getChildAt(prev),
                    mIndicatorContainer.getChildAt(dataPosition), lastPosition > position);
        }
    }

    private int computePrevPosition(LoopAdapter adapter, int lastPosition) {
        if (lastPosition >= 0) {
            return adapter.getDataPosition(lastPosition);
        }
        return adapter.getDataSize() - 1;
    }

    /**
     * 启动轮播
     *
     * @param force 强制启动
     */
    @SuppressWarnings("ConstantConditions")
    private void startInternal(boolean force) {
        if (mCanLoop && mCurrentIndex == -1) {
            seekToOriginPosition();
        }
        if (!mAutoLoop || !dataSizeValid()) {
            return;
        }
        if (force) {
            mHandler.removeCallbacks(mLoopRunnable);
            mHandler.postDelayed(mLoopRunnable, mInterval);
            inLoop = true;
        } else {
            if (!inLoop) {
                mHandler.removeCallbacks(mLoopRunnable);
                mHandler.postDelayed(mLoopRunnable, TOUCH_DELAY);
                inLoop = true;
            }
        }
    }

    private void seekToOriginPosition() {
        final LoopAdapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        //如果是刚开始自动轮播，先将页面定位到合适的位置
        setProperIndex(adapter.getDataSize(), 100);
        mLayoutManager.scrollToPositionWithOffset(mCurrentIndex, mLrMargin);
        Log.d(TAG, "seekToOriginPosition: " + mCurrentIndex);
    }


    /**
     * 保证数据的长度大于1才轮播
     */
    private boolean dataSizeValid() {
        LoopAdapter adapter = getAdapter();
        if (adapter == null) {
            return false;
        }
        return adapter.getDataSize() > 1;
    }

    private void stopInternal() {
        mHandler.removeCallbacks(mLoopRunnable);
        inLoop = false;
    }

    /**
     * 设置是否循环播放
     *
     * @param enable loop
     */
    public void setCanLoop(boolean enable) {
        this.mCanLoop = enable;
    }


    /**
     * 是否自动轮播
     *
     * @param autoLoop 自动轮播
     */
    public void setAutoLoop(boolean autoLoop) {
        this.mAutoLoop = autoLoop;
    }

    /**
     * 开启调试模式,可以输出日志信息
     */
    public void openDebug() {
        Tools.setDebug(true);
    }

    /**
     * 设置上下左右与父布局的间距
     *
     * @param margin 间距
     */
    public void setMargins(int margin) {
        checkAdapter("setMargins");
        int marginDp = Tools.dp2px(getContext(), margin);
        mParams.setMargins(marginDp, marginDp, marginDp, marginDp);
        mLrMargin = mTopMargin = mBottomMargin = marginDp;
        mRecycler.setLayoutParams(mParams);
        adjustIndicator();
    }

    /**
     * 设置page页与父布局的上边距
     *
     * @param topMargin 上边距
     */
    public void setTopMargin(int topMargin) {
        checkAdapter("setTopMargin");
        final int topMarginDp = Tools.dp2px(getContext(), topMargin);
        mParams.topMargin = topMarginDp;
        mTopMargin = topMarginDp;
        mRecycler.setLayoutParams(mParams);
        adjustIndicator();
    }

    /**
     * 设置page页与父布局的下边距
     *
     * @param bottomMargin 下边距
     */
    public void setBottomMargin(int bottomMargin) {
        checkAdapter("setBottomMargin");
        mBottomMargin = Tools.dp2px(getContext(), bottomMargin);
        mParams.bottomMargin = mBottomMargin;
        mRecycler.setLayoutParams(mParams);
        adjustIndicator();
    }

    /**
     * 设置中心page与父布局的左右边距
     *
     * @param lrMargin 左右边距
     */
    public void setLrMargin(int lrMargin) {
        checkAdapter("setLrMargin");
        mLrMargin = Tools.dp2px(getContext(), lrMargin);
        adjustIndicator();
    }

    private void adjustIndicator() {
        if (mIndicatorContainer != null) {
            mIndicatorContainer.setLayoutParams(createLayoutParams());
        }
    }

    public int getPageMargin() {
        return mPageMargin;
    }

    /**
     * 设置page与page之间的间距
     *
     * @param pageMargin page之间的间距
     */
    public void setPageMargin(int pageMargin) {
        checkAdapter("setPageMargin");
        mPageMargin = Tools.dp2px(getContext(), pageMargin);
        adjustIndicator();
    }

    public int getOffscreenPageLimit() {
        return mOffscreenPageLimit;
    }

    /**
     * 设置离屏缓存个数,默认为2,即内存中同时存在5个页面
     *
     * @param limit 离屏缓存个数
     */
    public void setOffscreenPageLimit(int limit) {
        mOffscreenPageLimit = limit;
//        mRecycler.setOffscreenPageLimit(limit);
    }

    public long getInterval() {
        return mInterval;
    }


    /**
     * 设置当前位置
     *
     * @param position 目标位置
     * @param smooth   是否缓慢移动
     */
    public void setCurrentItem(int position, boolean smooth) {
        final LoopAdapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        final int dataPosition = adapter.getDataPosition(mCurrentIndex);
        int del = position - dataPosition;
        if (smooth) {
            mRecycler.smoothScrollToPosition(mCurrentIndex + del);
        } else {
            mRecycler.scrollToPosition(mCurrentIndex + del);
        }
    }


    /**
     * 设置当前位置
     *
     * @param position 目标位置
     */
    public void setCurrentItem(int position) {
        setCurrentItem(position, false);
    }


    /**
     * 设置轮播间隔时间
     *
     * @param interval 间隔时间
     */
    public void setInterval(long interval) {
        mInterval = interval;
    }

    /**
     * 设置page切换动画
     *
     * @param pageTransformer 切换动画
     */
    public void setPageTransformer(ViewPager.PageTransformer pageTransformer) {
//        mRecycler.setPageTransformer(false, pageTransformer);
    }

    /**
     * 设置左右page的缩放比例
     *
     * @param scale 缩放比例(0-1)
     */
    public void setLrScale(@FloatRange(from = 0, to = 1.0f) float scale) {
        this.setPageTransformer(new ScalePageTransformer(scale));
    }

    /**
     * 开启缩放
     */
    public void enableScale() {
        this.setPageTransformer(new ScalePageTransformer());
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Tools.logI(TAG, "onWindowFocusChanged," + hasWindowFocus);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Tools.logI(TAG, "onWindowVisibilityChanged," + visibility);
        if (visibility == VISIBLE) {
            startInternal(true);
        } else {
            stopInternal();
        }
    }

    /**
     * 强制停止轮播
     */
    public void forceStop() {
        stopInternal();
        mAutoLoop = false;
    }

    /**
     * 强制开始轮播，适配某些机型自动轮播失效的时候需要手动调用该函数
     */
    public void forceStart() {
        startInternal(true);
    }

    private void createIndicatorIfNeed() {
        if (!mShowIndicator || !dataSizeValid()) {
            if (mIndicatorContainer != null) {
                this.removeView(mIndicatorContainer);
                mIndicatorContainer = null;
            }
            return;
        }
        //need show indicator
        if (mIndicatorContainer == null) {
            initIndicatorContainer();
        }

        mIndicatorContainer.removeAllViews();

        for (int i = 0; i < getAdapter().getDataSize(); i++) {
            mIndicatorAdapter.addIndicator(mIndicatorContainer, makeDrawable(), mIndicatorSize, mIndicatorMargin);
        }
        updateIndicators(0, -1);
    }

    private Drawable makeDrawable() {
        Drawable ret;
        if (mSelectDrawable != null && mUnSelectDrawable != null) {
            StateListDrawable listDrawable = new StateListDrawable();
            listDrawable.addState(new int[]{android.R.attr.state_selected}, mSelectDrawable);
            listDrawable.addState(new int[]{}, mUnSelectDrawable);
            ret = listDrawable;
        } else {
            ret = ContextCompat.getDrawable(getContext(), R.drawable.indicator_color_selector);
        }
        return ret;
    }

    private void checkAdapter(String methodName) {
        LoopAdapter adapter = getAdapter();
        if (adapter != null) {
            throw new IllegalStateException("please call " + methodName + " before setAdapter");
        }
    }

    @Nullable
    public LoopAdapter getAdapter() {
        final RecyclerView.Adapter adapter = mRecycler.getAdapter();
        return adapter == null ? null : (LoopAdapter) adapter;
    }

    /**
     * 设置Adapter加载数据
     *
     * @param adapter LoopAdapter
     */
    public void setAdapter(LoopAdapter<?> adapter) {
        Tools.checkNotNull(adapter);
        adapter.setCanLoop(mCanLoop);
        adapter.registerAdapterDataObserver(mDataSetObserver);
        adapter.setHelper(new AdapterHelper(mPageMargin, mLrMargin));
        mRecycler.setAdapter(adapter);
        restoreInitState();
    }

    /**
     * 直接设置图片地址和点击事件
     *
     * @param urls 图片地址
     */
    public void setImages(List<String> urls, OnPageClickListener listener) {
        SimpleImageAdapter imageAdapter = new SimpleImageAdapter(urls);
        imageAdapter.setOnPageClickListener(listener);
        this.setAdapter(imageAdapter);
    }

    public void setImages(List<String> urls) {
        this.setImages(urls, null);
    }

    /**
     * 是否使用指示器
     *
     * @param enable 是否可用
     */
    public void enableIndicator(boolean enable) {
        this.mShowIndicator = enable;
        if (enable) {
            if (mIndicatorContainer == null) {
                initIndicatorContainer();
            }
        } else {
            if (mIndicatorContainer != null) {
                this.removeView(mIndicatorContainer);
                mIndicatorContainer = null;
            }
        }
    }

    private void setIndicatorAdapter(IndicatorAdapter indicatorAdapter, boolean byeUser) {
        if (byeUser) {
            checkAdapter("setIndicatorAdapter");
        }
        mIndicatorAdapter = Tools.checkNotNull(indicatorAdapter, "indicatorAdapter is null");
    }


    /**
     * 设置指示适配器
     *
     * @param indicatorAdapter IndicatorAdapter
     */
    public void setIndicatorAdapter(IndicatorAdapter indicatorAdapter) {
        this.setIndicatorAdapter(indicatorAdapter, true);
    }

    /**
     * 设置页面选中监听
     *
     * @param listener OnPageSelectListener
     */
    public void setOnPageSelectListener(OnPageSelectListener listener) {
        this.mSelectListener = listener;
    }

    /**
     * 设置指示器资源
     *
     * @param selectRes   选中
     * @param unSelectRes 未选中
     */
    public void setIndicatorResource(@DrawableRes int selectRes, @DrawableRes int unSelectRes) {
        this.setIndicatorResource(selectRes, unSelectRes, true);
    }

    private void setIndicatorResource(@DrawableRes int selectRes, @DrawableRes int unSelectRes, boolean byUser) {
        if (byUser) {
            checkAdapter("setIndicatorResource");
        }
        Drawable selectDrawable = ContextCompat.getDrawable(getContext(), selectRes);
        Drawable unSelectDrawable = ContextCompat.getDrawable(getContext(), unSelectRes);
        this.mSelectDrawable = selectDrawable;
        this.mUnSelectDrawable = unSelectDrawable;
    }

    private void setIndicatorStyle(Style style, boolean byUser) {
        if (byUser) {
            checkAdapter("setIndicatorStyle");
        }
        switch (style) {
            case JD:
                setIndicatorAdapter(new JDIndicatorAdapter(), byUser);
                break;
            case PILL:
                setIndicatorResource(R.drawable.indicator_select, R.drawable.indicator_unselect, byUser);
                break;
            case NORMAL:
            default:
        }
    }

    /**
     * 设置指示器风格
     *
     * @param style Style
     */
    public void setIndicatorStyle(Style style) {
        this.setIndicatorStyle(style, true);
    }

    /**
     * 设置page切换时长
     *
     * @param duration 时长
     */
    public void setTransformDuration(int duration) {
        if (duration < 0) {
            duration = 0;
        }
        LoopScroller scroller = new LoopScroller(getContext());
        scroller.setScrollerDuration(duration);
//        scroller.linkViewPager(mRecycler);
    }

    public enum Style {
        /**
         * 京东
         */
        JD,
        /**
         * 药丸
         */
        PILL,
        /**
         * 普通
         */
        NORMAL

    }

    public interface OnPageClickListener {
        /**
         * page点击事件处理
         *
         * @param itemView 被点击的view
         * @param position 位置
         */
        void onPageClick(View itemView, int position);
    }

    /**
     * page选中监听
     */
    public interface OnPageSelectListener {

        /**
         * page选中事件处理
         *
         * @param position 被选中的位置
         */
        void onPageSelected(int position);
    }

}
