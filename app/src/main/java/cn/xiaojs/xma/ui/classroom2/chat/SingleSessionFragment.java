package cn.xiaojs.xma.ui.classroom2.chat;

import android.support.v4.app.FragmentManager;

import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.model.live.LiveCriteria;

/**
 * Created by maxiaobao on 2017/10/31.
 */

public class SingleSessionFragment extends ChatSessionFragment {

    @Override
    public LiveCriteria createLiveCriteria() {
        LiveCriteria liveCriteria = new LiveCriteria();
        liveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
        liveCriteria.type = Communications.TalkType.OPEN;
        return liveCriteria;
    }

    public static void invoke(FragmentManager fragmentManager) {
        SingleSessionFragment sessionFragment = new SingleSessionFragment();
//        Bundle b = new Bundle();
//        b.putString(CTLConstant.EXTRA_GROUP_ID, groupId);
//        sessionFragment.setArguments(b);
        sessionFragment.show(fragmentManager,"ssession");
    }
}
