package com.benyuan.xiaojs.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class CommonPopupMenuList extends ListView {

	public CommonPopupMenuList(Context context) {
		super(context);
	}

	public CommonPopupMenuList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommonPopupMenuList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int maxWidth = meathureWidthByChilds() + getPaddingLeft() + getPaddingRight();
	    super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), heightMeasureSpec);     
	}

	public int meathureWidthByChilds() {
	    int maxWidth = 0;
	    for (int i = 0; i < getAdapter().getCount(); i++) {
			View view = getAdapter().getView(i,null,this);
			if(view == null)
				continue;
	        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	        if (view.getMeasuredWidth() > maxWidth){
	            maxWidth = view.getMeasuredWidth();
	        }
	    }
	    return maxWidth;
	}

}
