package com.wenjian.loopbanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.*;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: LoopAdapter
 * Date: 2018/12/4
 *
 * @author jian.wen@ubtrobot.com
 */
public abstract class LoopAdapter<T> extends PagerAdapter {

    private static final String TAG = "LoopAdapter";
    private final SparseArray<ViewHolder> mHolderMap = new SparseArray<>();
    private List<T> mData;
    private int mLayoutId;
    private boolean mCanLoop = true;
    LoopBanner.OnPageClickListener mClickListener;
    private boolean mAlwaysRebind = false;

    public LoopAdapter(List<T> data, int layoutId) {
        mData = data == null ? new ArrayList<T>() : data;
        mLayoutId = layoutId;
    }

    public LoopAdapter(List<T> data) {
        this(data, -1);
    }

    public LoopAdapter(int layoutId) {
        this(new ArrayList<T>(), layoutId);
    }

    public LoopAdapter() {
        this(new ArrayList<T>(), -1);
    }

    @Override
    public final int getCount() {
        final int size = mData.size();
        if (size != 0) {
            return mCanLoop ? Integer.MAX_VALUE : size;
        }
        return 0;
    }

    @NonNull
    @Override
    public final Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int dataPosition = computePosition(position);
        ViewHolder holder = mHolderMap.get(dataPosition);
        if (holder == null) {
            View convertView = onCreateView(container);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.key_holder, holder);
            onBindView(holder, mData.get(dataPosition), dataPosition);
        } else {
            rebindIfNeed(holder, dataPosition);
        }
        return addViewSafely(container, holder.itemView);
    }

    /**
     * 在某些极端情况下重新执行onBindView，避免图片加载失败后一直显示白屏
     *
     * @param holder   ViewHolder
     * @param position 真实数据位置
     */
    private void rebindIfNeed(ViewHolder holder, int position) {
        if (mAlwaysRebind || holder.itemView instanceof ImageView) {
            onBindView(holder, mData.get(position), position);
            return;
        }
        SparseArray<View> viewList = holder.mViewList;
        for (int i = 0; i < viewList.size(); i++) {
            View view = viewList.valueAt(i);
            if (view instanceof ImageView) {
                Drawable drawable = ((ImageView) view).getDrawable();
                if (drawable == null) {
                    Tools.logI(TAG, "call onBindView again at " + position);
                    onBindView(holder, mData.get(position), position);
                    break;
                }
            }
        }
    }

    @Override
    public final void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        container.removeView((View) object);
        mHolderMap.put(computePosition(position), (ViewHolder) ((View) object).getTag(R.id.key_holder));
    }

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    private View addViewSafely(ViewGroup container, View itemView) {
        ViewParent parent = itemView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(itemView);
        }
        container.addView(itemView);
        return itemView;
    }

    /**
     * 获取真实的数据个数
     */
    public final int getDataSize() {
        return mData.size();
    }

    private int computePosition(int position) {
        final int size = mData.size();
        return size == 0 ? 0 : position % size;
    }

    /**
     * 子类可以复写此方法,添加自己的自定义View
     *
     * @param container ViewGroup
     * @return itemView
     */
    @NonNull
    protected View onCreateView(@NonNull ViewGroup container) {
        Tools.logI(TAG, "onCreateView");
        View view;
        if (mLayoutId != -1) {
            view = LayoutInflater.from(container.getContext()).inflate(mLayoutId, container, false);
        } else {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view = imageView;
        }
        return view;
    }

    /**
     * 为每个page绑定数据
     *
     * @param holder   ViewHolder
     * @param data     数据
     * @param position 数据真实位置
     */
    protected abstract void onBindView(ViewHolder holder, T data, int position);

    /**
     * 设置数据集
     *
     * @param data List<T> data
     */
    public final void setNewData(List<T> data) {
        mData = data != null ? data : new ArrayList<T>();
        mHolderMap.clear();
        notifyDataSetChanged();
    }

    /**
     * 只缓存view，每次都重新bind数据
     */
    public final void alwaysRebind() {
        this.mAlwaysRebind = true;
    }


    int getDataPosition(int position) {
        return computePosition(position);
    }

    void setOnPageClickListener(LoopBanner.OnPageClickListener listener) {
        this.mClickListener = listener;
    }

    void setCanLoop(boolean canLoop) {
        mCanLoop = canLoop;
    }

    public static final class ViewHolder {
        public final View itemView;

        private SparseArray<View> mViewList = new SparseArray<>();

        ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(@IdRes int viewId) {
            View view = mViewList.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViewList.put(viewId, view);
            }
            return (T) view;
        }

        public Context getContext() {
            return itemView.getContext();
        }

        /**
         * Will set the text of a TextView.
         *
         * @param viewId The view id.
         * @param value  The text to put in the text view.
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setText(@IdRes int viewId, CharSequence value) {
            TextView view = getView(viewId);
            view.setText(value);
            return this;
        }

        public ViewHolder setText(@IdRes int viewId, @StringRes int strId) {
            TextView view = getView(viewId);
            view.setText(strId);
            return this;
        }

        /**
         * Will set the image of an ImageView from a resource id.
         *
         * @param viewId     The view id.
         * @param imageResId The image resource id.
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
            ImageView view = getView(viewId);
            view.setImageResource(imageResId);
            return this;
        }

        /**
         * Will set background color of a view.
         *
         * @param viewId The view id.
         * @param color  A color, not a resource id.
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
            View view = getView(viewId);
            view.setBackgroundColor(color);
            return this;
        }

        /**
         * Will set background of a view.
         *
         * @param viewId        The view id.
         * @param backgroundRes A resource to use as a background.
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setBackgroundRes(@IdRes int viewId, @DrawableRes int backgroundRes) {
            View view = getView(viewId);
            view.setBackgroundResource(backgroundRes);
            return this;
        }

        /**
         * Will set text color of a TextView.
         *
         * @param viewId    The view id.
         * @param textColor The text color (not a resource id).
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setTextColor(@IdRes int viewId, @ColorInt int textColor) {
            TextView view = getView(viewId);
            view.setTextColor(textColor);
            return this;
        }


        /**
         * Will set the image of an ImageView from a drawable.
         *
         * @param viewId   The view id.
         * @param drawable The image drawable.
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setImageDrawable(@IdRes int viewId, Drawable drawable) {
            ImageView view = getView(viewId);
            view.setImageDrawable(drawable);
            return this;
        }

        /**
         * Add an action to set the image of an image view. Can be called multiple times.
         */
        public ViewHolder setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bitmap);
            return this;
        }

        /**
         * Set a view visibility to VISIBLE (true) or GONE (false).
         *
         * @param viewId  The view id.
         * @param visible True for VISIBLE, false for GONE.
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setGone(@IdRes int viewId, boolean visible) {
            View view = getView(viewId);
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            return this;
        }

        /**
         * Set a view visibility to VISIBLE (true) or INVISIBLE (false).
         *
         * @param viewId  The view id.
         * @param visible True for VISIBLE, false for INVISIBLE.
         * @return The BaseViewHolder for chaining.
         */
        public ViewHolder setVisible(@IdRes int viewId, boolean visible) {
            View view = getView(viewId);
            view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            return this;
        }
    }
}
