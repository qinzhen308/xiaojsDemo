package cn.xiaojs.xma.ui.recordlesson;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.Competency;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.CChapter;
import cn.xiaojs.xma.model.ctl.CChapterSection;
import cn.xiaojs.xma.model.ctl.CRecordLesson;
import cn.xiaojs.xma.model.ctl.TeachLead;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.recordedlesson.EnrollMode;
import cn.xiaojs.xma.model.recordedlesson.RLessonDetail;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.XiaojsActions;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LiveLessonBriefActivity;
import cn.xiaojs.xma.ui.lesson.LiveLessonLabelActivity;
import cn.xiaojs.xma.ui.lesson.SubjectSelectorActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/7/20.
 */
public class CreateRecordedLessonActivity extends BaseActivity implements CourseConstant {
    private final static int LESSON_BRIEF = 0;
    private final static int TEACHER_INTRODUCTION = 1;
    private final static int LESSON_LABEL = 2;
    private final static int SIT_IN_ON = 3;
    private final static int REQUEST_SELECT_SUBJECT = 4;
    private final static int ADD_COVER = 5;

    private final static int DEFAULT_SHOW_CHAR_LEN = 8;

    private final int MAX_LESSON_CHAR = 50;

    public final static String KEY_COMPETENCY = "key_competency";
    public static final String EXTRA_KEY_DIRS="extra_key_dirs";
    public static final String EXTRA_KEY_OLD_DATA="extra_key_old_data";



    @BindView(R.id.label1)
    TextView label1;
    @BindView(R.id.live_lesson_name)
    EditTextDel liveLessonName;
    @BindView(R.id.label2)
    TextView label2;
    @BindView(R.id.lesson_subject)
    TextView lessonSubject;
    @BindView(R.id.label3)
    TextView label3;
    @BindView(R.id.btn_teacher)
    TextView btnTeacher;
    @BindView(R.id.enroll_switcher)
    ToggleButton enrollSwitcher;
    @BindView(R.id.expiry_date_switcher)
    ToggleButton expiryDateSwitcher;
    @BindView(R.id.et_expired_date)
    EditTextDel etExpiredDate;
    @BindView(R.id.layout_expired_date)
    LinearLayout layoutExpiredDate;
    @BindView(R.id.add_cover)
    TextView addCover;
    @BindView(R.id.add_cover_tips)
    TextView addCoverTips;
    @BindView(R.id.cover_add_layout)
    LinearLayout coverAddLayout;
    @BindView(R.id.cover_view)
    ImageView coverView;
    @BindView(R.id.live_lesson_brief)
    TextView liveLessonBrief;
    @BindView(R.id.live_lesson_brief_layout)
    LinearLayout liveLessonBriefLayout;
    @BindView(R.id.live_lesson_label)
    TextView liveLessonLabel;
    @BindView(R.id.live_lesson_label_layout)
    LinearLayout liveLessonLabelLayout;



    private int mDarkGrayFont;
    private int mLightGrayFont;
    private Drawable mErrorDrawable;

    private String mCoverFileName;
    private String mCoverUrl;

    private Competency mCompetency;
    private String mCompetencyId;
    private LiveLesson mLesson=new LiveLesson();

    private RLessonDetail oldDatal;


    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.basic_infomation);
        addView(R.layout.activity_create_recorded_lesson);
        initView();
        setListener();
        initCoverLayout();
        initOldData();
    }

    private void initView() {
        mustInputSymbol();
        expiryDateSwitcher.setChecked(false);
        enrollSwitcher.setChecked(false);

        Account account = AccountDataManager.getAccont(this);
        if (account != null && account.getBasic() != null) {
            String name = account.getBasic().getName();
            btnTeacher.setText(name);
        }

        liveLessonName.setHint(getString(R.string.live_lesson_name_hint, MAX_LESSON_CHAR));
        liveLessonName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LESSON_CHAR)});

        //get color
        mDarkGrayFont = getResources().getColor(R.color.font_create_lesson_content);
        mLightGrayFont = getResources().getColor(R.color.font_gray);
        mErrorDrawable = new ColorDrawable(Color.WHITE);
    }

    private void initOldData(){
        if(!getIntent().hasExtra(EXTRA_KEY_OLD_DATA)){
            return;
        }
        oldDatal=(RLessonDetail) getIntent().getSerializableExtra(EXTRA_KEY_OLD_DATA);
        mLesson.setTags(oldDatal.tags);
        mLesson.setOverview(oldDatal.overview);
        mLesson.setCover(oldDatal.cover);
        mCompetency=new Competency();
        mCompetency.setChecked(true);
        mCompetency.setSubject(oldDatal.subject);
        mCompetencyId=oldDatal.subject.getId();

        if(!TextUtils.isEmpty(mLesson.getCover())){
            mCoverFileName = mLesson.getCover();
            coverView.setVisibility(View.VISIBLE);
            Dimension dimension = new Dimension();
            dimension.width = coverView.getMeasuredWidth();
            dimension.height = coverView.getMeasuredHeight();
            String url = Ctl.getCover(mLesson.getCover(), dimension);
            Glide.with(this)
                    .load(url)
                    .placeholder(mErrorDrawable)
                    .error(mErrorDrawable)
                    .into(coverView);
        }

        enrollSwitcher.setChecked((oldDatal.enroll!=null&&oldDatal.enroll.mode==Ctl.JoinMode.VERIFICATION));
        if(oldDatal.expire!=null&&oldDatal.expire.effective>0){
            expiryDateSwitcher.setChecked(true);
            etExpiredDate.setText(""+oldDatal.expire.effective);
        }else {
            expiryDateSwitcher.setChecked(false);
        }
        lessonSubject.setTextColor(mDarkGrayFont);
        lessonSubject.setText(mCompetency.getSubject().getName());
        initLessonBrief(mLesson);
        initLessonLabel(mLesson);
        liveLessonName.setText(oldDatal.title);
    }

    private void mustInputSymbol() {
        label1.setText(StringUtil.getSpecialString(label1.getText().toString() + " *", " *", getResources().getColor(R.color.main_orange)));
        label2.setText(StringUtil.getSpecialString(label2.getText().toString() + " *", " *", getResources().getColor(R.color.main_orange)));
        label3.setText(StringUtil.getSpecialString(label3.getText().toString() + " *", " *", getResources().getColor(R.color.main_orange)));

    }

    private void setListener(){
        expiryDateSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    layoutExpiredDate.setVisibility(View.VISIBLE);
                }else {
                    layoutExpiredDate.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.left_image, R.id.add_cover, R.id.live_lesson_brief,R.id.lesson_subject,
            R.id.live_lesson_label,R.id.cover_view,R.id.sub_btn})
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.left_image:
                handleBackPressed();
                finish();
                break;
            case R.id.add_cover:
                i = new Intent(this, CropImageMainActivity.class);
                i.putExtra(CropImagePath.CROP_IMAGE_WIDTH, COURSE_COVER_WIDTH);
                i.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, COURSE_COVER_HEIGHT);
                startActivityForResult(i, ADD_COVER);
                break;
            case R.id.cover_view:
                i = new Intent(this, CropImageMainActivity.class);
                i.putExtra(CropImageMainActivity.NEED_DELETE, true);
                i.putExtra(CropImagePath.CROP_IMAGE_WIDTH, COURSE_COVER_WIDTH);
                i.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, COURSE_COVER_HEIGHT);
                startActivityForResult(i, ADD_COVER);
                break;
            case R.id.live_lesson_brief:
                i = new Intent(this, LiveLessonBriefActivity.class);
                i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                startActivityForResult(i, LESSON_BRIEF);
                break;
            case R.id.live_lesson_label:
                i = new Intent(this, LiveLessonLabelActivity.class);
                i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                startActivityForResult(i, LESSON_LABEL);
                break;
            case R.id.lesson_subject:
                selectSubject();
                break;
            case R.id.sub_btn:
                submit();
                break;

            default:
                break;
        }
    }


    private void initCoverLayout() {
        coverView.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) coverAddLayout.getLayoutParams();
        FrameLayout.LayoutParams imgParams = (FrameLayout.LayoutParams) coverView.getLayoutParams();
        int w = getResources().getDisplayMetrics().widthPixels;
        int h = (int) ((COURSE_COVER_HEIGHT / (float) COURSE_COVER_WIDTH) * w);
        params.height = h;
        params.width = w;
        imgParams.width = w;
        imgParams.height = h;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_COVER:
                if (resultCode == CropImageMainActivity.RESULT_DELETE) {
                    coverView.setImageBitmap(null);
                    coverView.setVisibility(View.GONE);
                } else {
                    //upload cover to server and set lesson cover url
                    //mLesson.setCover(coverUrl);
                    if (resultCode != RESULT_OK) return;
                    if (data != null) {
                        final String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                        final Context c = CreateRecordedLessonActivity.this;
                        showProgress(true);
                        LessonDataManager.requestUploadCover(c, cropImgPath, new QiniuService() {
                            @Override
                            public void uploadSuccess(String fileName, UploadReponse reponse) {
                                cancelProgress();
                                mCoverFileName = fileName;
                                coverView.setVisibility(View.VISIBLE);
                                Glide.with(c)
                                        .load(cropImgPath)
                                        .error(mErrorDrawable)
                                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                        .into(coverView);

                                if (mLesson != null) {
                                    mLesson.setCover(fileName);
                                }
                            }

                            @Override
                            public void uploadProgress(String key, double percent) {

                            }

                            @Override
                            public void uploadFailure(boolean cancel) {
                                cancelProgress();
                                Toast.makeText(c, R.string.upload_cover_fail, Toast.LENGTH_SHORT).show();
                                coverView.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        coverView.setVisibility(View.GONE);
                    }
                }
                break;
            case LESSON_BRIEF:
                updateLesson(data);
                initLessonBrief(mLesson);
                break;
            case LESSON_LABEL:
                updateLesson(data);
                initLessonLabel(mLesson);
                break;
            case REQUEST_SELECT_SUBJECT:
                if (data != null) {
                    mCompetency = (Competency) data.getSerializableExtra(KEY_COMPETENCY);
                    CSubject subject = null;
                    if (mCompetency != null && (subject = mCompetency.getSubject()) != null) {
                        lessonSubject.setTextColor(mDarkGrayFont);
                        mCompetencyId = subject.getId();
                        lessonSubject.setText(subject.getName());
                    }
                }
                break;
        }
    }


    private void updateLesson(Intent data) {
        if (data != null) {
            Object object = data.getSerializableExtra(KEY_LESSON_OPTIONAL_INFO);
            if (object instanceof LiveLesson) {
                String coverUrl = mLesson.getCover();
                mLesson = (LiveLesson) object;
                mLesson.setCover(coverUrl);
            }
        }
    }

    private void initLessonBrief(LiveLesson data) {
        if (data != null && data.getOverview() != null) {
            String txt = formatResult(data.getOverview().getText());
            if (!TextUtils.isEmpty(txt) && data.getOverview() != null) {
                liveLessonBrief.setText(txt);
                liveLessonBrief.setTextColor(mDarkGrayFont);
            } else {
                liveLessonBrief.setTextColor(mLightGrayFont);
                liveLessonBrief.setText(R.string.please_input);
            }
        } else {
            liveLessonBrief.setTextColor(mLightGrayFont);
            liveLessonBrief.setText(R.string.please_input);
        }
    }

    private void initLessonLabel(LiveLesson data) {
        if (data != null) {
            String[] labels = data.getTags();
            if (labels != null) {
                StringBuilder sb = new StringBuilder();

                for (String label : labels) {
                    sb.append(label + ",");
                }
                String txt = sb.toString();
                if (!TextUtils.isEmpty(txt)) {
                    txt = txt.substring(0, txt.length() - 1);
                }
                if (!TextUtils.isEmpty(txt)) {
                    liveLessonLabel.setText(formatResult(txt));
                    liveLessonLabel.setTextColor(mDarkGrayFont);
                }
            } else {
                liveLessonLabel.setText(R.string.live_lesson_label_hint);
                liveLessonLabel.setTextColor(mLightGrayFont);
            }
        }
    }

    private void selectSubject() {
        //mLessonSubjectTv.setTextColor(mContentFont);
        Intent intent = new Intent();
        intent.setClass(this, SubjectSelectorActivity.class);
        intent.putExtra(CourseConstant.KEY_SUBJECT, mCompetency);
        startActivityForResult(intent, REQUEST_SELECT_SUBJECT);
    }




    private String formatResult(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (s.length() > DEFAULT_SHOW_CHAR_LEN) {
                return s.substring(0, DEFAULT_SHOW_CHAR_LEN) + "...";
            }
        }

        return s;
    }

    private void handleBackPressed() {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
        super.onBackPressed();
    }


    private void submit() {
        String name=liveLessonName.getText().toString();
        if(TextUtils.isEmpty(name)){
            ToastUtil.showToast(getApplicationContext(),R.string.live_lesson_name_empty);
            return;
        }

        if (name.length() > MAX_LESSON_CHAR) {
            String nameEr = getString(R.string.live_lesson_name_error, MAX_LESSON_CHAR);
            ToastUtil.showToast(getApplicationContext(),nameEr);
            return;
        }

        if(mCompetency==null||mCompetency.getSubject()==null){
            ToastUtil.showToast(getApplicationContext(),R.string.subject_empty);
            return;
        }


        CRecordLesson lesson=new CRecordLesson();
        lesson.title=name;
        lesson.subject=mCompetency.getSubject().getId();
        lesson.mode=2;

        if(expiryDateSwitcher.isChecked()){
            String daysStr=etExpiredDate.getText().toString().trim();
            if(TextUtils.isEmpty(daysStr)){
                ToastUtil.showToast(getApplicationContext(),"请填写有效天数");
                return;
            }
            lesson.effective=Long.valueOf(daysStr);
        }
        showProgress(false);
        lesson.enroll=new EnrollMode();
        lesson.enroll.mode=enrollSwitcher.isChecked()?Ctl.JoinMode.VERIFICATION:Ctl.JoinMode.OPEN;

        TeachLead lead=new TeachLead();
        lead.leads=new String[]{AccountDataManager.getAccountID(this)};
        lesson.teaching=lead;

        lesson.overview=mLesson.getOverview();
        lesson.cover=mLesson.getCover();
        lesson.tags=mLesson.getTags();
        setChaptersWithDirs(lesson);

        LessonDataManager.createRecordedCourse(this, lesson, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_RECORDED_LESSON_CHANGED);
                showSkipTip();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setChaptersWithDirs( CRecordLesson lesson){
        ArrayList<RLDirectory> dirs=(ArrayList<RLDirectory>)getIntent().getSerializableExtra(EXTRA_KEY_DIRS);
        CChapter[] chapters=new CChapter[dirs.size()];
        CChapter parent=null;
        CChapterSection child=null;
        CChapterSection[] childChapters=null;
        for(int i=0,size=dirs.size();i<size;i++){
            RLDirectory dir=dirs.get(i);
            parent=new CChapter();
            parent.index=i+1;
            parent.title=dir.name;
            childChapters=new CChapterSection[dir.getChildrenCount()];
            for(int j=0,size2=dir.children.size();j<size2;j++){
                RLLesson l=dir.getChild(j);
                child=new CChapterSection();
                child.index=j+1;
                child.title=l.name;
                child.resource=l.videoId;
                childChapters[j]=child;
            }
            parent.sections=childChapters;
            chapters[i]=parent;
        }
        lesson.chapters=chapters;
    }


    private void showSkipTip() {
        CommonDialog dialog = new CommonDialog(this);
        dialog.setDesc(getString(R.string.create_recorded_lesson_skip_tip));
        dialog.setLefBtnText(R.string.create_lesson_skip_btn_back);
        dialog.setRightBtnText(R.string.create_lesson_skip_btn_look);
        dialog.setCancelable(false);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                MainActivity.invokeWithAction(CreateRecordedLessonActivity.this, XiaojsActions.ACTION_TO_MY_RECORDED_LESSONS);
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                MainActivity.invokeWithAction(CreateRecordedLessonActivity.this, null);
            }
        });
        dialog.show();
    }




    public static void invoke(Activity context, ArrayList<RLDirectory> dirs){
        Intent intent=new Intent(context,CreateRecordedLessonActivity.class);
        intent.putExtra(EXTRA_KEY_DIRS,dirs);
        context.startActivity(intent);
    }

    public static void invoke(Activity context, ArrayList<RLDirectory> dirs, RLessonDetail oldData){
        Intent intent=new Intent(context,CreateRecordedLessonActivity.class);
        intent.putExtra(EXTRA_KEY_DIRS,dirs);
        if(oldData!=null){
            intent.putExtra(EXTRA_KEY_OLD_DATA,oldData);
        }
        context.startActivity(intent);
    }

}
