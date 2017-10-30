package cn.xiaojs.xma.common.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.LoadingView;

public class LoadingMoreFooter extends LinearLayout {


    @BindView(R.id.loading_progress)
    LoadingView loadingView;

    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;

    public LoadingMoreFooter(Context context) {
        super(context);
        initView();
    }

    /**
     * @param context
     * @param attrs
     */
    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {

        LayoutInflater.from(getContext()).
                inflate(R.layout.layout_base2_recyclerview_loadmore, this, true);
        ButterKnife.bind(this);

    }

    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                loadingView.setVisibility(View.VISIBLE);
                loadingView.resume();
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                loadingView.pause();
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                loadingView.pause();
                this.setVisibility(View.VISIBLE);
                break;
        }
    }
}
