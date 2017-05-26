package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/5/21.
 */

public class ClassInfoActivity extends BaseActivity {

    public static final String EXTRA_CLASSID = "classid";

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
            R.id.lay_student, R.id.lay_qrcode})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.enter_btn:
                //TODO 进入教室
                break;
            case R.id.lay_time_table:
                //TODO 课表
                break;
            case R.id.lay_material:
                //TODO 资料库
                break;
            case R.id.lay_student:
                //TODO 学生
                break;
            case R.id.lay_qrcode:
                //TODO 二维码
                break;
        }


    }


    private void loadClassInfo() {
        showProgress(true);
        LessonDataManager.getClassInfo(this, classId, new APIServiceCallback<ClassInfoData>() {
            @Override
            public void onSuccess(ClassInfoData object) {

                cancelProgress();

                if (object!=null && object.adviserclass !=null) {
                    bingView(object.adviserclass);
                }else{
                    showEmptyView("");
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(ClassInfoActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadClassInfo();
                    }
                });
            }
        });
    }

    private void bingView(ClassInfo info) {
        nameView.setText(info.title);
        teacherNameView.setText(info.adviserName);

        numLessonView.setText(info.lessons);
        studentNumView.setText(info.students);
        opentimeView.setText(info.createdOn.toString());
        creatorView.setText(info.ownerName);


         int vid = (info.join != null && info.join.mode == Ctl.JoinMode.VERIFICATION)?
                 R.string.need_ver : R.string.no_verification_required;

        verifyStatusView.setText(vid);

    }
}
