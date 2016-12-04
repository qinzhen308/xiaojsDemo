package com.benyuan.xiaojs.data.api.service;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.benyuan.xiaojs.XiaojsConfig;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/12/2.
 */

public final class SupportRequestFragment extends Fragment {

    protected static final String FRAGMENT_TAG = "com.yuanpei.api.req_4_frg";

    private ArrayList<ContextLifecycle> lifecycles;

    public SupportRequestFragment() {
        lifecycles = new ArrayList<>();

    }


    public final void addLifecycle(ContextLifecycle lifecycle) {
        lifecycles.add(lifecycle);
    }

    public final void removeLifecycle(ContextLifecycle lifecycle) {
        boolean remove = lifecycles.remove(lifecycle);
//        if (XiaojsConfig.DEBUG) {
//            Logger.d("SupportRequestFragment remove lifecycle is :%s",remove);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if(XiaojsConfig.DEBUG) {
//            Logger.d("SupportRequestFragment onAttach......");
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

//        if(XiaojsConfig.DEBUG) {
//            Logger.d("SupportRequestFragment onDestroy......");
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
