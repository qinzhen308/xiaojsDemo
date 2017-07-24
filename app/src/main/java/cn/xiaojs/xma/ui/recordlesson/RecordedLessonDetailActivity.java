package cn.xiaojs.xma.ui.recordlesson;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

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

public class RecordedLessonDetailActivity extends BaseActivity {


    @BindView(R.id.list)
    ListView listView;


    @Nullable
    @BindView(R.id.lesson_title)
    TextView lessonTitleView;

    @Nullable
    @BindView(R.id.lesson_category)
    TextView lessonCategoryView;

    @Nullable
    @BindView(R.id.lesson_enroll)
    TextView enrollView;

    @Nullable
    @BindView(R.id.lesson_valid)
    TextView validView;

    @Nullable
    @BindView(R.id.lcover)
    TextView lCoverView;

    @Nullable
    @BindView(R.id.lesson_cover)
    ImageView lessonCoverView;

    @Nullable
    @BindView(R.id.ltag)
    TextView lTagView;

    @Nullable
    @BindView(R.id.label_container)
    ColorTextFlexboxLayout labelLayout;

    @Nullable
    @BindView(R.id.lesson_info)
    TextView lessonInfoView;


    private RecordedLessonListAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recordlesson_detail);
        setMiddleTitle(R.string.detail);
        init();
    }



    @Optional
    @OnClick({R.id.back_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:       //返回
                finish();
                break;
        }
    }

    private void init() {

        View header = LayoutInflater.from(this).inflate(R.layout.layout_recordlesson_detail_header, null);

        listView.addHeaderView(header);
        ButterKnife.bind(this);

        adapter = new RecordedLessonListAdapter(this);
        listView.setAdapter(adapter);


    }

}
