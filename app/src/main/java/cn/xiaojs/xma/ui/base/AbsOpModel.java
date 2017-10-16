package cn.xiaojs.xma.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.grade.GradeHomeActivity;
import cn.xiaojs.xma.ui.lesson.CancelLessonActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonBusiness;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.ui.lesson.LiveLessonDetailActivity;
import cn.xiaojs.xma.ui.lesson.ModifyLessonActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassInfoActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.EditTimetableActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.widget.Common2Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/5/25.
 */

public abstract class AbsOpModel<T>{
    //查看报名页
    public static final int OP_APPLY =0;
    //取消开课
    public static final int OP_CANCEL_LESSON=1;
    //撤销确认（机构端对老师）
    public static final int OP_CANCEL_SUBMIT=2;
    //班级信息
    public static final int OP_CLASS_INFO=3;
    //资料库（黄icon）
    public static final int OP_DATABASE2 =4;
    //删除
    public static final int OP_DELETE=5;
    //编辑课
    public static final int OP_EDIT=6;
    //进入教室
    public static final int OP_ENTER=7;
    //进入教室（黄icon）
    public static final int OP_ENTER_2=8;
    //查看详情
    public static final int OP_LOOK=9;
    //修改上课时间
    public static final int OP_MODIFY_TIME=10;
    //取消公开
    public static final int OP_PRIVATE=11;
    //设为公开
    public static final int OP_PUBLIC=12;
    //上架
    public static final int OP_PUBLISH=13;
    //再次开课
    public static final int OP_CREATE_LESSON_AGAIN =14;
    //课表
    public static final int OP_SCHEDULE=15;
    //分享(蓝图标按钮)
    public static final int OP_SHARE=16;
    //报名注册
    public static final int OP_SIGNUP=17;
    //提交确认（机构端对老师）
    public static final int OP_SUBMIT=18;
    //撤销审核
    public static final int OP_CANCEL_CHECK=19;
    //橘黄色的分享按钮
    public static final int OP_SHARE2=20;
    //蓝色图标的资料库按钮
    public static final int OP_DATABASE1=21;
    //同意邀请 （对于老师）
    public static final int OP_AGREE_INVITE=22;
    //拒绝邀请 （对于老师）
    public static final int OP_DISAGREE_INVITE=23;
    //查看原因（审核被拒绝后）
    public static final int OP_REJECT_REASON=24;
    //重新开课
    public static final int OP_RECREATE_LESSON=25;
    //报名学生名单
    public static final int OP_APPLY_STUDENTS_LIST=26;
    //学生退出录播课
    public static final int OP_ABORT_RECORDED_LESSON=27;

    private int id;

    public AbsOpModel(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public abstract void onClick(Activity context,T data,int position);


}
