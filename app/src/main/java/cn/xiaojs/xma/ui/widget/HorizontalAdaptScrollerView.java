package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cn.xiaojs.xma.util.DeviceUtil;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/6/8
 * Desc:
 *
 * ======================================================================================== */
public class HorizontalAdaptScrollerView extends RecyclerView {
    /**
     * item可以显示个数
     */
    public enum ItemVisibleTypeCount {
        TYPE_FREE,
        TYPE_ONE,
        TYPE_ONE_HALF,
        TYPE_TWO,
        TYPE_TWO_HALF,
        TYPE_THREE,
        TYPE_THREE_HALF,
        TYPE_FOUR,
        TYPE_FOUR_HALF
    }

    /**
     * 默认显示3个半
     */
    private ItemVisibleTypeCount mItemVisibleTypeCount = ItemVisibleTypeCount.TYPE_THREE_HALF;
    private float mItemVisibleCount = 1;
    private static int mItemWidth;
    private int mViewWidth;
    private ItemSpace mItemSpace;
    private RecyclerView.Adapter mOriginalAdapter;


    public HorizontalAdaptScrollerView(Context context) {
        super(context);
        init(context);
    }

    public HorizontalAdaptScrollerView(Context context, ItemVisibleTypeCount type) {
        super(context);
        init(context);
        mItemVisibleTypeCount = type;
    }

    public HorizontalAdaptScrollerView(Context context, float visibleCount) {
        super(context);
        init(context);
        mItemVisibleTypeCount = ItemVisibleTypeCount.TYPE_FREE;
        mItemVisibleCount = visibleCount;
    }

    public HorizontalAdaptScrollerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizontalAdaptScrollerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(linearLayoutManager);

        mViewWidth = DeviceUtil.getScreenWidth(getContext());
    }

    public void setItemVisibleCountType(ItemVisibleTypeCount type) {
        mItemVisibleTypeCount = type;
    }

    public void setItemVisibleCount(float visibleCount) {
        mItemVisibleCount = visibleCount;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        if (mItemSpace == null) {
            mItemSpace = new ItemSpace();
            mItemSpace.setHorizontalSpace(horizontalSpacing);
            addItemDecoration(mItemSpace);
        } else {
            mItemSpace.setHorizontalSpace(horizontalSpacing);
            computeItemWith(mViewWidth, mItemSpace.getHorizontalSpace());
        }
    }

    /**
     * 设置百分百
     * @param horSpacingPercent
     */
    public void setHorizontalSpacing(float horSpacingPercent) {
        if (mItemSpace == null) {
            mItemSpace = new ItemSpace();
            mItemSpace.setHorSpacePercent(horSpacingPercent);
            addItemDecoration(mItemSpace);
        } else {
            mItemSpace.setHorSpacePercent(horSpacingPercent);
            computeItemWith(mViewWidth, mItemSpace.getHorizontalSpace());
        }
    }

    public void setTopSpacing(int topSpacing) {
        if (mItemSpace == null) {
            mItemSpace = new ItemSpace();
            mItemSpace.setTsp(topSpacing);
            addItemDecoration(mItemSpace);
        } else {
            mItemSpace.setTsp(topSpacing);
        }
    }

    public void setBottomSpacing(int bottomSpacing) {
        if (mItemSpace == null) {
            mItemSpace = new ItemSpace();
            mItemSpace.setBsp(bottomSpacing);
            addItemDecoration(mItemSpace);
        } else {
            mItemSpace.setBsp(bottomSpacing);
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        computeItemWith(mViewWidth, mItemSpace != null ? mItemSpace.getHorizontalSpace() : 0);
    }

    @Override
    public void addItemDecoration(ItemDecoration decor, int index) {
        super.addItemDecoration(decor, index);
        onDecorationAddedChangeLayoutParams(decor);
    }

    @Override
    public void addItemDecoration(ItemDecoration decor) {
        super.addItemDecoration(decor);
        onDecorationAddedChangeLayoutParams(decor);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mOriginalAdapter = adapter;
        if (adapter != null) {
            AdaptScrollerViewAdapter adaptScrollerViewAdapter = new AdaptScrollerViewAdapter(adapter, this);
            super.setAdapter(adaptScrollerViewAdapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    /**
     * item之间的间距，以及topPadding和bottomPadding
     */
    public static class ItemSpace extends RecyclerView.ItemDecoration {
        private float mHorSpacePercent;
        private int mHorizontalSpace;
        private int mTsp;
        private int mBsp;

        public ItemSpace() {

        }

        public ItemSpace(int space, int tsp, int bsp) {
            mHorizontalSpace = space;
            mTsp = tsp;
            mBsp = bsp;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int pos = -1;
            if ((pos = parent.getChildAdapterPosition(view)) > -1) {
                if (mHorizontalSpace <= 0) {
                    mHorizontalSpace = (int)(view.getMeasuredWidth() * mHorSpacePercent);
                }

                if (pos == 0) {
                    outRect.left = mHorizontalSpace;
                }
                outRect.right = mHorizontalSpace;

                outRect.top = mTsp;
                outRect.bottom = mBsp;
            }
        }

        public void setHorizontalSpace(int space) {
            mHorizontalSpace = space;
        }

        public void setHorSpacePercent(float percent) {
            mHorSpacePercent = percent;
        }

        public void setTsp(int tsp) {
            mTsp = tsp;
        }

        public void setBsp(int bsp) {
            mBsp = bsp;
        }

        public int getHorizontalSpace() {
            return mHorizontalSpace;
        }
    }

    private void computeItemWith(int viewWidth, int itemSpace) {
        if (viewWidth <= 0) {
            return;
        }

        float f = 3.5f;
        switch (mItemVisibleTypeCount) {
            case TYPE_FREE:
                f = mItemVisibleCount;
                break;
            case TYPE_ONE:
                f = 1.0f;
                break;
            case TYPE_ONE_HALF:
                f = 1.5f;
                break;
            case TYPE_TWO:
                f = 2.0f;
                break;
            case TYPE_TWO_HALF:
                f = 2.5f;
                break;
            case TYPE_THREE:
                f = 3.0f;
                break;
            case TYPE_THREE_HALF:
                f = 3.5f;
                break;
            case TYPE_FOUR:
                f = 4.0f;
                break;
            case TYPE_FOUR_HALF:
                f = 4.5f;
                break;
            default:
                f = mItemVisibleCount;
                break;
        }

        mItemWidth = (int)((viewWidth - Math.round(f) * itemSpace) / f);
    }

    private void onDecorationAddedChangeLayoutParams(ItemDecoration decor) {
        if (decor instanceof ItemSpace) {
            ItemSpace mltDecor = (ItemSpace)decor;
            computeItemWith(mViewWidth, mltDecor.getHorizontalSpace());
        }
    }

    /**
     * 包装子类adapter，在onCreateViewHolder()的方法里面修改布局参数
     */
    public static class AdaptScrollerViewAdapter extends RecyclerView.Adapter<AdaptScrollerViewAdapter.ViewHolder> {
        private RecyclerView.Adapter mAdapter;
        private HorizontalAdaptScrollerView mHorizontalScrollerView;

        public AdaptScrollerViewAdapter(RecyclerView.Adapter adapter, HorizontalAdaptScrollerView view) {
            mAdapter = adapter;
            mHorizontalScrollerView = view;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            RecyclerView.ViewHolder holder = mAdapter.onCreateViewHolder(viewGroup, i);
            changeItemParams(mItemWidth, holder.itemView);

            return new ViewHolder(holder.itemView,holder);
        }

        @Override
        public void onBindViewHolder(ViewHolder t, int i) {
            mAdapter.onBindViewHolder(t.mItemHolder, i);
        }

        @Override
        public int getItemCount() {
            return mAdapter.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            return mAdapter.getItemViewType(position);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public RecyclerView.ViewHolder mItemHolder;

            public ViewHolder(View itemView, RecyclerView.ViewHolder itemHolder) {
                super(itemView);
                mItemHolder = itemHolder;
            }
        }

        private void changeItemParams(int itemWidth, View itemView) {
            if (itemWidth <= 0) {
                return;
            }

            ViewGroup.LayoutParams vgParams = itemView.getLayoutParams();
            RecyclerView.LayoutParams params = null;
            if (vgParams instanceof RecyclerView.LayoutParams) {
                params = (RecyclerView.LayoutParams) vgParams;
                params.width = itemWidth;
            }

            if (params == null) {
                params = new RecyclerView.LayoutParams(itemWidth, LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(params);
            }

            mHorizontalScrollerView.changeChildrenParams(itemView, params);
        }

    }

    protected void changeChildrenParams(View parent, RecyclerView.LayoutParams parentParams) {
        //sub class to overwrite
    }

}
