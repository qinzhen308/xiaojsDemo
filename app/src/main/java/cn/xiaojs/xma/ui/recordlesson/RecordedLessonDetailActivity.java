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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.recordedlesson.RLessonDetail;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.flow.ColorTextFlexboxLayout;

/**
 * Created by maxiaobao on 2017/7/23.
 */

public class RecordedLessonDetailActivity extends BaseActivity {

    public static final String EXTRA_RLESSON_ID = "rl_id";


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
    @BindView(R.id.c_line)
    View clineView;

    @Nullable
    @BindView(R.id.lesson_cover)
    ImageView lessonCoverView;

    @Nullable
    @BindView(R.id.ltag)
    TextView lTagView;

    @Nullable
    @BindView(R.id.lesson_tags)
    ColorTextFlexboxLayout labelLayout;

    @Nullable
    @BindView(R.id.t_line)
    View tlineView;

    @Nullable
    @BindView(R.id.lesson_info)
    TextView lessonInfoView;

    private String lessonId;
    private RecordedLessonListAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recordlesson_detail);
        setMiddleTitle(R.string.detail);
        init();
    }



    @Optional
    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:       //返回
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

        lessonId = getIntent().getStringExtra(EXTRA_RLESSON_ID);

        requestDetail();

    }

    private void requestDetail() {
        showProgress(true);
        LessonDataManager.getRecordedCourse(this, lessonId, new APIServiceCallback<RLessonDetail>() {
            @Override
            public void onSuccess(RLessonDetail object) {
                cancelProgress();

                if (object == null) {
                    showEmptyView("空");
                }else{
                    bindView(object);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(RecordedLessonDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestDetail();
                    }
                });
            }
        });
    }

    private void bindView(RLessonDetail detail) {
        lessonTitleView.setText(detail.title);
        if (detail.subject != null) {
            lessonCategoryView.setText(detail.subject.name);
        }

        //TODO 报名确认

        if(detail.expire !=null) {
            validView.setText(detail.expire.effective + "天");
        }else {
            validView.setText(R.string.valid_forever);
        }

        String cover = detail.cover;
        if (TextUtils.isEmpty(cover)) {
            lCoverView.setVisibility(View.GONE);
            lessonCoverView.setVisibility(View.GONE);
            clineView.setVisibility(View.GONE);
        }else {

            Dimension dimension = new Dimension();
            dimension.width = lessonCoverView.getMeasuredWidth();
            dimension.height = lessonCoverView.getMeasuredHeight();
            String url = Ctl.getCover(cover, dimension);

            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.default_lesson_cover)
                    .error(R.drawable.default_lesson_cover)
                    .into(lessonCoverView);
        }


        if (detail.tags !=null && detail.tags.length > 0) {

            for (String tag : detail.tags) {
                labelLayout.addText(tag);
            }

        }else {
            lTagView.setVisibility(View.GONE);
            labelLayout.setVisibility(View.GONE);
            tlineView.setVisibility(View.GONE);
        }


        if (detail.overview != null  && !TextUtils.isEmpty(detail.overview.getText())) {
            lessonInfoView.setText(detail.overview.getText());
        }else {
            lessonInfoView.setText(R.string.lesson_no_introduction);
        }














    }

}
