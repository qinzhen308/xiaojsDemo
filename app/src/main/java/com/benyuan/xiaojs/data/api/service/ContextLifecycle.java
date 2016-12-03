package com.benyuan.xiaojs.data.api.service;

/**
 * Created by maxiaobao on 2016/12/1.
 */

public interface ContextLifecycle{

    void onStart();

    void onStop();

    void onDestroy();
}
