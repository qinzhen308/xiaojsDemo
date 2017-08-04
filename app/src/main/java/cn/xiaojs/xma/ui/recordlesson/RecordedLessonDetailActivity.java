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
import cn.xiaojs.xma.ui.widget.MaxLineTextView;
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
    MaxLineTextView lessonInfoView;
    @Nullable
    @BindView(R.id.overlay)
    TextView overlay;

    private String lessonId;
    private RecordedLessonListAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recordlesson_detail);
        setMiddleTitle(R.string.detail);
        init();
    }



    @Optional
    @OnClick({R.id.left_image,R.id.overlay,R.id.lesson_info})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:       //返回
                finish();
                break;
            case R.id.overlay:   //展开
                lessonInfoView.displayAll();

                break;
            case R.id.lesson_info:   //展开
                if(lessonInfoView.isOverSize()){
                    lessonInfoView.displayAll();
                }else {
                    lessonInfoView.hide(3);
                }
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

        lessonInfoView.setOnOverLineChangedListener(new MaxLineTextView.OnOverSizeChangedListener() {
            @Override
            public void onChanged(boolean isOverSize) {
                if(isOverSize){
                    overlay.setVisibility(View.VISIBLE);
                }else {
                    overlay.setVisibility(View.GONE);
                }
            }
        });

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
            lessonCategoryView.setText(detail.subject.getName());
        }

        enrollView.setText(detail.enroll!=null&&detail.enroll.mode==Ctl.JoinMode.VERIFICATION?R.string.need_confirm:R.string.no_verification_required);

        if(detail.expire !=null&&detail.expire.effective>0) {
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

        lessonInfoView.hide(3);
        if (detail.overview != null  && !TextUtils.isEmpty(detail.overview.getText())) {
            lessonInfoView.setText(detail.overview.getText());
        }else {
            lessonInfoView.setText(R.string.lesson_no_introduction);
        }

      /*  lessonInfoView.setText("超级长的简介我泪崩阿萨德那等了两个周，昨天海贼王874话的文字情报已经出了，和情报一起传过来的还有SBS。这期海迷问了个关于白胡子的问题：\n" +
                "Q：尾田老师你好，白胡子头上总是包着印花巾，其实是个秃头吗？到底是怎样的发型呢？请您画出来。\n" +
                "A：传闻是这样的。" +
                "\n还有一次，白胡子和罗杰在某地一起喝酒，罗杰还趁机调侃白胡子，想不想知道最终之岛的位置。老爹果断拒绝，一是因为这种事情要自己来的才痛快，二是老爹想要的那时候基本已经达成，大秘宝并不是他追求的。这时候白胡子的年纪应该不比SBS上小吧，依然是波浪形金发。因此，七月君认为本期SBS上尾田老师回答读者的答案，是在临时的想法，纯属开玩笑，并不是老爹的真实设定。");
*/

        adapter.setList(detail.sections);

        adapter.notifyDataSetChanged();




    }

}
