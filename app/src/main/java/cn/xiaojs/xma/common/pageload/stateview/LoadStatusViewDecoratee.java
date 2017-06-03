package cn.xiaojs.xma.common.pageload.stateview;

import android.view.View;

/**
 * Created by Paul Z on 2017/5/15.
 */

public class LoadStatusViewDecoratee implements LoadStateListener{
    LoadStateListener stateView;


    public LoadStatusViewDecoratee(LoadStateListener loadStateListener){
        stateView=loadStateListener;
    }


    @Override
    public void onSuccess(String msg) {
        if(stateView!=null)
        stateView.onSuccess(msg);
    }

    @Override
    public void onFailed(String msg) {
        if(stateView!=null)
            stateView.onFailed(msg);
    }

    @Override
    public void onNoData(String msg) {
        if(stateView!=null)

            stateView.onNoData(msg);

    }

    @Override
    public void onNoNetwork(String msg) {
        if(stateView!=null)
        stateView.onNoNetwork(msg);

    }

    @Override
    public void onLoading(String msg) {
        if(stateView!=null)
        stateView.onLoading(msg);

    }

    @Override
    public View getView() {
        return stateView.getView();
    }


    public void change(int state,String msg){
        switch (state) {
            case STATE_NORMAL:
                onSuccess(msg);
                break;
            case STATE_LOADING:
                onLoading(msg);
                break;
            case STATE_LOADING_ERROR:
                onFailed(msg);
                break;
            case STATE_UP_REFRESH:

                break;
            case STATE_DOWN_REFRESH:

                break;
            case STATE_ALL_EMPTY:
                onNoData(msg);
                break;
            case STATE_SINGLE_EMPTY:
                onNoData(msg);
                break;
            default:
                break;
        }

    }
}
