package cn.xiaojs.xma.ui.recordlesson;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.flow.ColorTextFlexboxLayout;

/**
 * Created by maxiaobao on 2017/7/23.
 */

public class RecordedLessonEnrollActivity extends BaseActivity {


    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.enroll_btn)
    Button enrollButton;

    @Nullable
    @BindView(R.id.lesson_img)
    ImageView lessonCoverView;

    @Nullable
    @BindView(R.id.lesson_title)
    TextView lessonTitleView;

    @Nullable
    @BindView(R.id.valid)
    TextView lessonValidView;

    @Nullable
    @BindView(R.id.enroll_count)
    TextView enrollCountView;

    @Nullable
    @BindView(R.id.label_container)
    ColorTextFlexboxLayout labelLayout;

    @Nullable
    @BindView(R.id.tea_avatar)
    ImageView avatarView;

    @Nullable
    @BindView(R.id.tea_name)
    TextView nameView;

    @Nullable
    @BindView(R.id.tea_title)
    TextView sinView;

    @Nullable
    @BindView(R.id.tab_group)
    RadioGroup tabGroup;

    private RecordedLessonListAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recordlesson_enroll);
        needHeader(false);
        init();
    }



    @Optional
    @OnClick({R.id.back_btn, R.id.share_btn, R.id.enroll_btn, R.id.lay_teacher})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:       //返回
                finish();
                break;
            case R.id.share_btn:      //分享
                break;
            case R.id.enroll_btn:     //立即报名
                break;
            case R.id.lay_teacher:    //进入老师个人主页
                break;
        }
    }

    @Optional
    @OnCheckedChanged({R.id.tab_info, R.id.tab_dir})
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch (v.getId()) {
            case R.id.tab_info:      //课程简介
                break;
            case R.id.tab_dir:       //目录
                break;
        }
    }

    private void init() {

        View header = LayoutInflater.from(this).inflate(R.layout.layout_recordlesson_enroll_header, null);

        listView.addHeaderView(header);
        ButterKnife.bind(this);

        adapter = new RecordedLessonListAdapter(this);
        listView.setAdapter(adapter);

        tabGroup.check(R.id.tab_dir);


    }

}
