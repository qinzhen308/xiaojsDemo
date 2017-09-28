package cn.xiaojs.xma.ui.classroom2.chat;

import java.util.Comparator;

import cn.xiaojs.xma.model.live.TalkItem;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class MessageComparator implements Comparator<TalkItem> {
    @Override
    public int compare(TalkItem o1, TalkItem o2) {
        if (o1 == null || o2 == null) {
            return 0;
        }

        return o1.time > o2.time ? 1 : o1.time == o2.time ? 0 : -1;
    }
}
