package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.content.Intent;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.LessonScheduleActivity;
import cn.xiaojs.xma.util.ShareUtil;

/**
 * Created by Paul Z on 2017/5/25.
 */

public class LOpModel {
    //查看申请
    public static final int OP_APPLY =0;
    public static final int OP_CANCEL_LESSON=1;
    public static final int OP_CANCEL_SUBMIT=2;
    public static final int OP_CLASS_INFO=3;
    public static final int OP_DATABASE=4;
    public static final int OP_DELETE=5;
    public static final int OP_EDIT=6;
    public static final int OP_ENTER=7;
    public static final int OP_ENTER_2=8;
    public static final int OP_LOOK=9;
    public static final int OP_MODIFY_TIME=10;
    public static final int OP_PRIVATE=11;
    public static final int OP_PUBLIC=12;
    public static final int OP_PUBLISH=13;
    public static final int OP_RECREATE_LESSON=14;
    public static final int OP_SCHEDULE=15;
    public static final int OP_SHARE=16;
    public static final int OP_SIGNUP=17;
    public static final int OP_SUBMIT=18;
    
    
    
    private int id;

    public LOpModel(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public void onClick(Activity context,Object data){
        switch (id){
            case OP_APPLY:

                break;
            case OP_CANCEL_LESSON:

                break;
            case OP_CANCEL_SUBMIT:

                break;
            case OP_CLASS_INFO:

                break;
            case OP_DATABASE:
                enterDatabase(context);
                break;
            case OP_DELETE:

                break;
            case OP_EDIT:
                editLesson(context);
                break;
            case OP_ENTER:
                enterSchedule(context);
                break;
            case OP_ENTER_2:
                enterSchedule(context);
                break;
            case OP_LOOK:
                break;
            case OP_MODIFY_TIME:
                break;
            case OP_PRIVATE:
                break;
            case OP_PUBLIC:
                break;
            case OP_PUBLISH:
                break;
            case OP_RECREATE_LESSON:
                break;
            case OP_SCHEDULE:
                break;
            case OP_SHARE:
                share(context);
                break;
            case OP_SIGNUP:
                break;
            case OP_SUBMIT:
                break;

        }
    }


    public static void editLesson(Activity context){
        Intent intent=new Intent(context,LessonCreationActivity.class);
        context.startActivity(intent);
    }

    public static void share(Activity context){
        ShareUtil.show(context,"标题test","test内容","https//baidu.com");
    }

    public static void enterSchedule(Activity context){
        Intent intent=new Intent(context,ClassScheduleActivity.class);
        context.startActivity(intent);
    }

    public void enterDatabase(Activity context){
        Intent intent=new Intent(context,MaterialActivity.class);
        intent.putExtra(MaterialActivity.KEY_IS_MINE,false);
        context.startActivity(intent);
    }
}
