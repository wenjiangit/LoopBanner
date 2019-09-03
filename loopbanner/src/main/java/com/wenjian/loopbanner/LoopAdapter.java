package com.wenjian.loopbanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
public abstract class LoopAdapter<T> extends RecyclerView.Adapter<LoopAdapter.ViewHolder> {

    private static final String TAG = "LoopAdapter";
    private List<T> mData;
    private int mLayoutId;
    private boolean mCanLoop = true;
    LoopBanner.OnPageClickListener mClickListener;

    private AdapterHelper mHelper;

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

    void setHelper(AdapterHelper helper) {
        this.mHelper = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = onCreateView(parent);
        mHelper.onCreateViewHolder(parent, view);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mHelper.onBindViewHolder(holder);
        final int dataPosition = computePosition(position);
        onBindView(holder, mData.get(dataPosition), dataPosition);
    }


    @Override
    public int getItemCount() {
        final int size = mData.size();
        if (size != 0) {
            return mCanLoop ? Integer.MAX_VALUE : size;
        }
        return size;
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
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.lay_internal_img,container,false);
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
        notifyDataSetChanged();
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

    public static final class ViewHolder extends RecyclerView.ViewHolder {

        private final SparseArray<View> mViewList = new SparseArray<>();

        ViewHolder(View itemView) {
            super(itemView);
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

        public View getItemView() {
            return itemView;
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
