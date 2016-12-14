package com.benyuan.xiaojs.ui.widget.flow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowBaseLayout extends ViewGroup {

	public static final String TAG = "FlowBaseLayout";
	
	/**
	 * 存储所有的View，按行记录
	 */
	 protected List<List<View>> mAllViews = new ArrayList<List<View>>();
	/**
	 * 记录每一行的最大高度
	 */
	 protected List<Integer> mLineHeight = new ArrayList<Integer>();

	private int mMaxLine = -1;
	private int mMaxChild = -1;
	private int mMaxInIine = -1;

	private boolean mIsOverlay;
	private int mOverlayWidth;//重叠区域宽度

	public FlowBaseLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(LayoutParams p) {
		return new MarginLayoutParams(p);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	/**
	 * 最多显示多少行
	 * 
	 * @param maxLines
	 */
	public void setMaxLines(int maxLines) {
		this.mMaxLine = maxLines;
	}

	/**
	 * 最多显示多少个
	 * 
	 * @param maxChild
	 */
	public void setMaxChild(int maxChild) {
		this.mMaxChild = maxChild;
	}

	/**
	 * 每行最多显示多少个
	 * 
	 * @param maxInLine
	 */
	public void setMaxInLine(int maxInLine) {
		this.mMaxInIine = maxInLine;
	}

	public void isOverlay(boolean b){
		mIsOverlay = b;
	}

	public void setOverlayWidth(int width){
		mOverlayWidth = width;
	}

	/**
	 * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获得它的父容器为它设置的测量模式和大小
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		//Log.e(TAG, sizeWidth + "," + sizeHeight);

		// 如果是warp_content情况下，记录宽和高
		int width = 0;
		int height = 0;
		/**
		 * 记录每一行的宽度，width不断取最大宽度
		 */
		int lineWidth = 0;
		/**
		 * 每一行的高度，累加至height
		 */
		int lineHeight = 0;

		int cCount = getChildCount();

		int row = 0;
		
		// 遍历每个子元素
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			// 测量每一个child的宽和高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到child的lp
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			// 当前子空间实际占据的宽度
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			// 当前子空间实际占据的高度
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			/**
			 * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
			 */
			if (lineWidth + childWidth > sizeWidth) {
				width = Math.max(lineWidth, childWidth);// 取最大的
				lineWidth = childWidth; // 重新开启新行，开始记录
				// 叠加当前高度，
				height += lineHeight;
				row++;
				if(isMaxLine(row)){
					break;
				}
				// 开启记录下一行的高度
				lineHeight = childHeight;
			} else
			// 否则累加值lineWidth,lineHeight取最大高度
			{
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			// 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
			if (i == cCount - 1) {
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}

			if((i == mMaxChild - 1) && mMaxChild < cCount){
				width = Math.max(width, lineWidth);
				height += lineHeight;
				break;
			}
		}
		setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);

	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mAllViews.clear();
		mLineHeight.clear();

		int width = getWidth();

		int lineWidth = 0;
		int lineHeight = 0;
		// 存储每一行所有的childView
		List<View> lineViews = new ArrayList<View>();
		int cCount = getChildCount();
		int row = 0;// 行数
		int quantity = 0;// 已显示的数量
		int lineCount = 0;
		// 遍历所有的孩子
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			// 如果已经需要换行
			if (	(mIsOverlay && childWidth + lp.leftMargin + lp.rightMargin + lineWidth - i * mOverlayWidth > width) ||
					(!mIsOverlay && childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) ||
					((mMaxInIine > 0) && lineCount >= mMaxInIine)) {
				lineCount = 0;
				row++;
				if (isMaxLine(row)) {
					break;
				}
				// 记录这一行所有的View以及最大高度
				mLineHeight.add(lineHeight);
				// 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
				mAllViews.add(lineViews);
				lineWidth = 0;// 重置行宽
				lineViews = new ArrayList<View>();
			}
			/**
			 * 如果不需要换行，则累加
			 */
			if (mIsOverlay){
				lineWidth += childWidth + lp.leftMargin + lp.rightMargin - i * mOverlayWidth;
			}else {
				lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			}

			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
			lineViews.add(child);
			quantity++;
			if (mMaxInIine > 0) {
				lineCount++;
			}
			if (isMaxQuantity(quantity)) {
				break;
			}
		}
		// 记录最后一行
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViews);

		if (showLast()){
			mAllViews.get(0).remove(mAllViews.get(0).size() - 1);
			mAllViews.get(0).add(lastView(getTotal() - mAllViews.get(0).size()));
		}
		int left = 0;
		int top = 0;
		// 得到总行数
		int lineNums = mAllViews.size();
		for (int i = 0; i < lineNums; i++) {
			// 每一行的所有的views
			lineViews = mAllViews.get(i);
			// 当前行的最大高度
			lineHeight = mLineHeight.get(i);

			//Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
			//Log.e(TAG, "第" + i + "行， ：" + lineHeight);

			// 遍历当前行所有的View
			if (mIsOverlay){//有覆盖形式
				left = width ;
				for (int j = 0,k =0; j <lineViews.size(); j++,k++) {
					View child = lineViews.get(j);
					if (child.getVisibility() == View.GONE) {
						continue;
					}
					MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

					// 计算childView的left,top,right,bottom
					int lc = left - lp.leftMargin - child.getMeasuredWidth() -lp.rightMargin + j * mOverlayWidth;
					int tc = top + lp.topMargin;
					int rc = lc + child.getMeasuredWidth();
					int bc = tc + child.getMeasuredHeight();

					//Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r =" + rc + " , b = " + bc);

					child.layout(lc, tc, rc, bc);

					left -= child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
				}
			}else {//普通形式
				for (int j = 0; j < lineViews.size(); j++) {
					View child = lineViews.get(j);
					if (child == null || child.getVisibility() == View.GONE) {
						continue;
					}
					MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

					// 计算childView的left,top,right,bottom
					int lc = 0;
//				if (mIsOverlay){
//					lc = left + lp.leftMargin - j * mOverlayWidth;
//				}else {
					lc = left + lp.leftMargin;
					//}

					int tc = top + lp.topMargin;
					int rc = lc + child.getMeasuredWidth();
					int bc = tc + child.getMeasuredHeight();

					//Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r =" + rc + " , b = " + bc);

					child.layout(lc, tc, rc, bc);

					left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
				}
			}

			left = 0;
			top += lineHeight;
		}

	}

	private boolean isMaxLine(int row) {
		if (mMaxLine > 0 && mMaxLine == row) {
			return true;
		}

		return false;
	}

	private boolean isMaxQuantity(int quantity) {
		if (mMaxChild > 0 && mMaxChild <= quantity) {
			return true;
		}

		return false;
	}

	protected boolean showLast(){
		return false;
	}

	protected View lastView(int num){
		return null;
	}

	protected int getTotal(){
		return 0;
	}
}
