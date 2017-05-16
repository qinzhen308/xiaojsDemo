package cn.xiaojs.xma.ui.mine;

import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by Paul Z on 2017/5/11.
 */

public class MySignatureDetailActivity extends BaseActivity {
    public final static String KEY_CONTENT="key_content";

    @Override
    protected void addViewContent() {
        TextView tvSignature=new TextView(this);
        tvSignature.setText(getIntent().getStringExtra(KEY_CONTENT));
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        tvSignature.setLayoutParams(lp);
        int padding=getResources().getDimensionPixelSize(R.dimen.px18);
        tvSignature.setPadding(padding,padding,padding,padding);
        tvSignature.setBackgroundColor(getResources().getColor(R.color.white));
        tvSignature.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_28px));
        addView(tvSignature);
        setMiddleTitle("简介");
    }
}
