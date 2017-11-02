package cn.xiaojs.xma.ui.classroom2.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;

/**
 * Created by maxiaobao on 2017/10/31.
 */

public class SingleSessionFragment extends ChatSessionFragment {


    private String accountId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountId = getArguments().getString(CTLConstant.EXTRA_ACCOUNTID);
        titleStr = getArguments().getString(CTLConstant.EXTRA_SESSION_NAME);

    }


    @Override
    public LiveCriteria createLiveCriteria() {
        LiveCriteria liveCriteria = new LiveCriteria();
        liveCriteria.to = accountId;
        liveCriteria.type = Communications.TalkType.PEER;
        return liveCriteria;
    }

    public static void invoke(FragmentManager fragmentManager, String accountId, String title) {
        SingleSessionFragment sessionFragment = new SingleSessionFragment();
        Bundle b = new Bundle();
        b.putString(CTLConstant.EXTRA_ACCOUNTID, accountId);
        b.putString(CTLConstant.EXTRA_SESSION_NAME, title);
        sessionFragment.setArguments(b);
        sessionFragment.show(fragmentManager,"ssession");
    }
}
