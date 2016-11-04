package com.benyuan.xiaojs.ui.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.benyuan.xiaojs.R;

/*
 * 功能描述：带有下部阴影的popupWindow的基类
 */
public abstract class BaseFullWindow extends PopupWindow{
	
	protected Context mContext;
	private View mRootView;
	private View mEmptyContent;
	private LinearLayout mContent;
	
	public BaseFullWindow(Context context) {
		super(context);
		mContext = context;
		init(mContext);
	}
	
	private void init(Context context) {
		mContext = context;
		mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_base_full_view, null);
		mContent = (LinearLayout) mRootView.findViewById(R.id.content);
		View customerView = setCustomerContentView();
		mContent.addView(customerView, 0);
		setContentView(mRootView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		setBackgroundDrawable(dw);
		mRootView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (((ScrollView)mRootView).getChildAt(0).getMeasuredHeight() < mRootView.getMeasuredHeight()){
					dismiss();
				}
				return false;
			}
		});
	}
	
	protected abstract View setCustomerContentView();
}
