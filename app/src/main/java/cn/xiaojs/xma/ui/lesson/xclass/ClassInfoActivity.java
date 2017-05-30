package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/5/21.
 */

public class ClassInfoActivity extends BaseActivity {

    public static final String EXTRA_CLASSID = "classid";
    public final int REQUEST_NAME_CODE = 0x1;

    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.teacher_name)
    TextView teacherNameView;
    @BindView(R.id.student_num)
    TextView studentNumView;
    @BindView(R.id.open_time)
    TextView opentimeView;
    @BindView(R.id.creator)
    TextView creatorView;
    @BindView(R.id.join_veri)
    TextView verifyStatusView;
    @BindView(R.id.num_lesson)
    TextView numLessonView;

    private String classId;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_class_info);
        setMiddleTitle(getString(R.string.class_info));

        classId = getIntent().getStringExtra(EXTRA_CLASSID);
        loadClassInfo();
    }

    @OnClick({R.id.left_image, R.id.enter_btn,
            R.id.lay_time_table, R.id.lay_material,
            R.id.lay_student, R.id.lay_qrcode, R.id.name_lay})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.name_lay:
                modifyName();
                break;
            case R.id.enter_btn:
                //TODO 进入教室
                break;
            case R.id.lay_time_table:
                //TODO 课表
                break;
            case R.id.lay_material:
                //TODO 资料库
                //databank();
                break;
            case R.id.lay_student:
                //学生
                Intent i = new Intent(this,StudentsListActivity.class);
                i.putExtra(StudentsListActivity.EXTRA_CLASS,classId);
                startActivity(i);
                break;
            case R.id.lay_qrcode:
                //TODO 二维码
                break;
        }


    }

    //资料库
    private void databank() {
//        Intent intent = new Intent(this, ClassMaterialActivity.class);
//        intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
//        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_ID, bean.getId());
//        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_NAME, bean.getTitle());
//        mContext.startActivity(intent);
    }

    private void bingView(ClassInfo info) {
        nameView.setText(info.title);
        teacherNameView.setText(info.adviserName);

        numLessonView.setText(getString(R.string.number_lesson,info.lessons));
        studentNumView.setText(getString(R.string.number_student,info.students));
        opentimeView.setText(TimeUtil.format(info.createdOn,TimeUtil.TIME_YYYY_MM_DD));
        creatorView.setText(info.ownerName);


        //TODO 如果是学生，此处该隐藏

        int vid = (info.join != null && info.join.mode == Ctl.JoinMode.VERIFICATION) ?
                R.string.need_confirm : R.string.no_verification_required;

        verifyStatusView.setText(vid);

    }

    private void modifyName() {
        Intent i = new Intent(this,AddLessonNameActivity.class);
        i.putExtra(AddLessonNameActivity.EXTRA_CLASSID,classId);
        i.putExtra(AddLessonNameActivity.EXTRA_NAME,nameView.getText().toString());
        i.putExtra(AddLessonNameActivity.EXTRA_ROLE,AddLessonNameActivity.ROLE_CLASS);
        startActivityForResult(i,REQUEST_NAME_CODE);
    }

    private void loadClassInfo() {
        showProgress(true);
        LessonDataManager.getClassInfo(this, classId, new APIServiceCallback<ClassInfoData>() {
            @Override
            public void onSuccess(ClassInfoData object) {

                cancelProgress();

                if (object != null && object.adviserclass != null) {
                    bingView(object.adviserclass);
                } else {
                    showEmptyView(getString(R.string.empty_tips));
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(ClassInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadClassInfo();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NAME_CODE:
                    if (data!=null) {
                        String newName = data.getStringExtra(AddLessonNameActivity.EXTRA_NAME);
                        if (!TextUtils.isEmpty(newName)) {
                            nameView.setText(newName);
                        }
                    }

                    break;
            }
        }
    }
}
