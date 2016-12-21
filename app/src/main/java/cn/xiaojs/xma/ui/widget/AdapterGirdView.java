package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 可自适应高度的gridview
 */
public class AdapterGirdView extends GridView {

	public AdapterGirdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AdapterGirdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AdapterGirdView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(  
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
	}

}
