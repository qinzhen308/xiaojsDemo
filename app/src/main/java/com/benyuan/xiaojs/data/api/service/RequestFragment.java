package com.benyuan.xiaojs.data.api.service;

import android.app.Fragment;
import android.content.Context;

import com.benyuan.xiaojs.XiaojsConfig;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/12/1.
 */

public final class RequestFragment extends Fragment {

    protected static final String FRAGMENT_TAG = "com.yuanpei.api.req_frg";

    private ArrayList<ContextLifecycle> lifecycles;

    public RequestFragment() {
        lifecycles = new ArrayList<>();

    }


    public final void addLifecycle(ContextLifecycle lifecycle) {
        lifecycles.add(lifecycle);
    }

    public final void removeLifecycle(ContextLifecycle lifecycle) {
        boolean remove = lifecycles.remove(lifecycle);
//        if (XiaojsConfig.DEBUG) {
//            Logger.d("RequestFragment remove lifecycle is :%s",remove);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if(XiaojsConfig.DEBUG) {
//            Logger.d("RequestFragment onAttach......");
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//
//        if(XiaojsConfig.DEBUG) {
//            Logger.d("RequestFragment onDestroy......");
//        }

        lifeDead();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void lifeDead() {

        for (ContextLifecycle lifecycle : lifecycles) {
            lifecycle.onDestroy();
        }

        clearLife();

    }

    private void clearLife() {
        lifecycles.clear();
    }
}
