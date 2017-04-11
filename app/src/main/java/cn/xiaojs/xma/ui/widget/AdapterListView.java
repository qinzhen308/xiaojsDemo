package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 可自适应高度的listview
 */
public class AdapterListView extends ListView {

	public AdapterListView(Context context) {
		super(context);
	}

	public AdapterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdapterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}