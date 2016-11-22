/*
 * 功能描述：能显示在ScrollView下的适应内容的listview
 */
package com.benyuan.xiaojs.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;


public class CanInScrollviewListView extends LinearLayout implements
		CanInScrollviewListViewDataNotify {

	private BaseAdapter mAdapter;
	private boolean mNeedDivider;
	private int mDividerColor;
	private int mDividerHeight;
	private int mDividerWidth;
	private int mDividerLeftMargin;
	private int mDividerRightMargin;
	private OnItemClickListener mListener;
	private OnItemLongClickListener mLongListener;

	public static interface DataSetObserverAdapter {
		public void setDataSetObserver(
				CanInScrollviewListViewDataNotify dataSetObserver);
	}

	public static abstract class Adapter extends BaseAdapter implements
			DataSetObserverAdapter {

		private CanInScrollviewListViewDataNotify mDataSetObserver;

		@Override
		public void setDataSetObserver(
				CanInScrollviewListViewDataNotify dataSetObserver) {
			mDataSetObserver = dataSetObserver;

		}

		@Override
		public void notifyDataSetChanged() {
			if (mDataSetObserver != null) {
				mDataSetObserver.dataChange();
			}
		}
	}

	public CanInScrollviewListView(Context context) {
		super(context);
		if (getChildCount() > 0) {
			return;
		}
		mDividerColor = getResources().getColor(R.color.hor_divide_line);
		mDividerHeight = getContext().getResources().getDimensionPixelSize(
				R.dimen.px1);
		mDividerWidth = ViewGroup.LayoutParams.MATCH_PARENT;
		mDividerLeftMargin = 0;
		mDividerRightMargin = 0;
		setOrientation(LinearLayout.VERTICAL);
	}

	public CanInScrollviewListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (getChildCount() > 0) {
			return;
		}
		mDividerColor = getResources().getColor(R.color.hor_divide_line);
		mDividerHeight = getContext().getResources().getDimensionPixelSize(
				R.dimen.px1);
		mDividerWidth = ViewGroup.LayoutParams.MATCH_PARENT;
		mDividerLeftMargin = 0;
		mDividerRightMargin = 0;
		setOrientation(LinearLayout.VERTICAL);
	}
	
	@TargetApi(11)
	public CanInScrollviewListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		if (getChildCount() > 0) {
			return;
		}
		mDividerColor = getResources().getColor(R.color.hor_divide_line);
		mDividerHeight = getContext().getResources().getDimensionPixelSize(
				R.dimen.px1);
		mDividerWidth = ViewGroup.LayoutParams.MATCH_PARENT;
		mDividerLeftMargin = 0;
		mDividerRightMargin = 0;
		setOrientation(LinearLayout.VERTICAL);
	}

	/**
	 * 设置数据adapter
	 */
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		((DataSetObserverAdapter) mAdapter).setDataSetObserver(this);
		initView();
	}

	/**
	 * 获取adapter
	 */
	public Adapter getAdapter() {
		return (Adapter) mAdapter;
	}

	/**
	 * 设置是否要显示分割线
	 */
	public CanInScrollviewListView setNeedDivider(boolean needDivider) {
		mNeedDivider = needDivider;
		return this;
	}

	/**
	 * 设置分割线的颜色
	 */
	public CanInScrollviewListView setDividerColor(int resId) {
		mDividerColor = getResources().getColor(resId);
		return this;
	}

	/**
	 * 设置分割线的高度
	 */
	public CanInScrollviewListView setDividerHeight(int height) {
		mDividerHeight = height;
		return this;
	}

	/**
	 * 设置分割线的左右边距
	 */
	public CanInScrollviewListView setDividerLeftRightMargin(int left, int right) {
		mDividerLeftMargin = left;
		mDividerRightMargin = right;
		return this;
	}

	private View buildDivider() {
		ImageView divider = new ImageView(getContext());
		divider.setBackgroundColor(mDividerColor);
		LayoutParams parmas = new LayoutParams(
				mDividerWidth, mDividerHeight);
		parmas.leftMargin = mDividerLeftMargin;
		parmas.rightMargin = mDividerRightMargin;
		divider.setLayoutParams(parmas);
		return divider;
	}

	private void initView() {
		if (mAdapter != null) {
			for (int i = 0; i < mAdapter.getCount(); i++) {
				final View view = mAdapter.getView(i, null, this);
				if (view != null) {
					addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					final int index = i;
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mListener != null){
								mListener.onItemClick(view,index);
							}
						}
					});
					view.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							if (mLongListener != null){
								return mLongListener.onItemLongClick(view,index);
							}
							return false;
						}
					});
					if (mNeedDivider && (i != (mAdapter.getCount() - 1))) {
						addView(buildDivider());
					}
				}
			}
		}
	}


	public void setOnItemClickListener(OnItemClickListener listener){
		mListener = listener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener listener){
		mLongListener = listener;
	}

	@Override
	public void dataChange() {
		removeAllViews();
		initView();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	public interface OnItemClickListener{
		void onItemClick(View view,int position);
	}

	public interface OnItemLongClickListener{
		boolean onItemLongClick(View view,int position);
	}
}
