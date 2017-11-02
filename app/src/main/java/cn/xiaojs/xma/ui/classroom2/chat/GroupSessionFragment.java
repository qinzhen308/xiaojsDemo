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

public class GroupSessionFragment extends ChatSessionFragment {

    private String group;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = getArguments().getString(CTLConstant.EXTRA_GROUP_ID);
        titleStr = getArguments().getString(CTLConstant.EXTRA_SESSION_NAME);

    }

    @Override
    public LiveCriteria createLiveCriteria() {
        LiveCriteria liveCriteria = new LiveCriteria();
        liveCriteria.to = group;
        liveCriteria.type = Communications.TalkType.OPEN;
        return liveCriteria;
    }

    public static void invoke(FragmentManager fragmentManager, String groupId, String title) {
        GroupSessionFragment sessionFragment = new GroupSessionFragment();
        Bundle b = new Bundle();
        b.putString(CTLConstant.EXTRA_GROUP_ID, groupId);
        b.putString(CTLConstant.EXTRA_SESSION_NAME, title);
        sessionFragment.setArguments(b);
        sessionFragment.show(fragmentManager,"gsession");
    }
}
