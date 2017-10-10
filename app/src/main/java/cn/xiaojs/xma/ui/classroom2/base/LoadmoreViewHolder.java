package cn.xiaojs.xma.ui.classroom2.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.LoadingView;

/**
 * Created by maxiaobao on 2017/10/10.
 */

public class LoadmoreViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.loading_lay)
    FrameLayout loadingLay;
    @BindView(R.id.loading)
    LoadingView loadingView;

    public LoadmoreViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setLoadingVisibility(int visibility) {
        loadingLay.setVisibility(visibility);
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            loadingView.pause();
        }else {
            loadingView.resume();
        }
    }


    public static LoadmoreViewHolder createHolder(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new LoadmoreViewHolder(
                inflater.inflate(R.layout.layout_classroom2_loadmore,parent,false));
    }

}
