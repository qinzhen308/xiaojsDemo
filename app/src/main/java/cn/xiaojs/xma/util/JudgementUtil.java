package cn.xiaojs.xma.util;

import android.content.Context;
import android.content.Intent;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;

/**
 * Created by Paul Z on 2017/5/31.
 */

public class JudgementUtil {

    public static boolean checkTeachingAbility(final Context context){
        if (AccountDataManager.isTeacher(context)) {
            return true;
        } else {
            //提示申明教学能力
            final CommonDialog dialog = new CommonDialog(context);
            dialog.setTitle(R.string.declare_teaching_ability);
            dialog.setDesc(R.string.declare_teaching_ability_tip);
            dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    dialog.dismiss();
                    Intent intent = new Intent(context, TeachingSubjectActivity.class);
                    context.startActivity(intent);
                }
            });
            dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return false;
        }
    }
}
