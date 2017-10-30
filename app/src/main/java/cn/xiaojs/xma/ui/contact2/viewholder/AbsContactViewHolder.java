package cn.xiaojs.xma.ui.contact2.viewholder;

import android.content.Context;
import android.view.View;

import butterknife.ButterKnife;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public abstract class AbsContactViewHolder <T extends AbsContactItem>{

    private Context context;

    public AbsContactViewHolder(Context context, View view) {
        ButterKnife.bind(this, view);
    }

    public Context getContext() {
        return context;
    }


}
