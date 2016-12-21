package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements
		Checkable {

	private CheckedTextView mTextView = null;
	
	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	
	

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		int count = this.getChildCount();
		for(int i=0;i<count;i++){
			View v = getChildAt(i);
			
			if(v instanceof CheckedTextView){
				mTextView = (CheckedTextView)v;
			}
		}
		
	}





	@Override
	public boolean isChecked() {

		return mTextView.isChecked();
	}

	@Override
	public void setChecked(boolean checked) {
		mTextView.setChecked(checked);

	}

	@Override
	public void toggle() {
		mTextView.toggle();

	}

}
