package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.JoinClassParams;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.recordedlesson.RLessonDetail;
import cn.xiaojs.xma.model.recordedlesson.Section;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.base.AbsOpModel;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.EnrollSuccessActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.search.SearchBusiness;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.flow.ColorTextFlexboxLayout;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.ToastUtil;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/7/23.
 */

public class RecordedLessonEnrollActivity extends BaseActivity {

    public final static String EXTRA_LESSON_ID="extra_lesson_id";

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
    @BindView(R.id.tea_count)
    TextView teaCount;
    @Nullable
    @BindView(R.id.tv_description)
    TextView tvDescription;

    @Nullable
    @BindView(R.id.tab_group)
    RadioGroup tabGroup;

    @Nullable
    @BindView(R.id.rv_teachers)
    RecyclerView rvTeachers;

    private String lessonId;

    private RecordedLessonListAdapter adapter;
    private ItemAdapter teacherAdapter;

    RLessonDetail mDetail;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recordlesson_enroll);
        needHeader(false);
        init();
        loadData();
    }



    @Optional
    @OnClick({R.id.back_btn, R.id.share_btn, R.id.enroll_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:       //返回
                finish();
                break;
            case R.id.share_btn:      //分享
                share();
                break;
            case R.id.enroll_btn:     //立即报名
                doEnrollBuz();
                break;

        }
    }


    private void doEnrollBuz(){
        if(mDetail==null)return;
        if(mDetail.createdBy!=null&& AccountDataManager.getAccountID(this).equals(mDetail.createdBy.getId())){//是这个课的创建者
            //老师按钮失效
        }else {//学生

            if(Ctl.CourseEnrollmentState.PENDING_FOR_ACCEPTANCE.equals(mDetail.enrollState)){//等待确认

            }else if(Ctl.CourseEnrollmentState.ENROLLED.equals(mDetail.enrollState)){//已经加入
                RLDirListActivity.invoke(this,mDetail.id,mDetail.enrollOfCurrentAccount!=null&&mDetail.enrollOfCurrentAccount.isExpired);
            }else {//未加入
                checkJoinNeedVerify();
            }
        }
    }

    //分享
    private void share() {
        if (mDetail == null) return;
        String date = null;
        if(mDetail.expire!=null){
            date=ScheduleUtil.getDateYMD(new Date(mDetail.createdOn.getTime()+ ScheduleUtil.DAY*mDetail.expire.effective));
        }else {
            date="永久";
        }
        String name = "";
        if (!ArrayUtil.isEmpty(mDetail.teachers)) {
            name = mDetail.teachers[0].name;
        }
        String shareUrl = ApiManager.getShareLessonUrl(mDetail.id, Social.SearchType.COURSE);
        ShareUtil.shareUrlByUmeng( this, mDetail.title, new StringBuilder(date).append("\r\n").append("主讲：").append(name).toString(), shareUrl);
    }



    private void init() {
        lessonId=getIntent().getStringExtra(EXTRA_LESSON_ID);
        View header = LayoutInflater.from(this).inflate(R.layout.layout_recordlesson_enroll_header, null);

        listView.addHeaderView(header);
        ButterKnife.bind(this);

        initCoverLayout();

        adapter = new RecordedLessonListAdapter(this);
        listView.setAdapter(adapter);

        tabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.tab_info:      //课程简介
                        tvDescription.setVisibility(View.VISIBLE);
                        adapter.setList(new ArrayList<Section>());
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.tab_dir:       //目录
                        tvDescription.setVisibility(View.GONE);
                        adapter.setList(mDetail.sections);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
//        tabGroup.check(R.id.tab_dir);
        initItemWidth();
        teacherAdapter=new ItemAdapter();
        rvTeachers.setLayoutManager(new GridLayoutManager(this,1,RecyclerView.HORIZONTAL,false));
        rvTeachers.setAdapter(teacherAdapter);
    }

    private void initCoverLayout() {
        ViewGroup.LayoutParams imgParams = lessonCoverView.getLayoutParams();
        int w = getResources().getDisplayMetrics().widthPixels;
        int h = (int) ((CourseConstant.COURSE_COVER_HEIGHT / (float) CourseConstant.COURSE_COVER_WIDTH) * w);
        imgParams.width = w;
        imgParams.height = h;
    }

    private void loadData(){
        showProgress(true);
        LessonDataManager.getRecordedCoursePublic(this, lessonId, new APIServiceCallback<RLessonDetail>() {
            @Override
            public void onSuccess(RLessonDetail object) {
                cancelProgress();
                mDetail=object;
                handleData();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                ToastUtil.showToast(getApplicationContext(),errorMessage);
            }
        });
    }

    private void handleData(){
        if(mDetail==null){
            showFailedView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData();
                }
            });
            return;
        }
        lessonTitleView.setText(mDetail.title);
        if(!TextUtils.isEmpty(mDetail.cover)){
            Dimension dimension = new Dimension();
            dimension.width = lessonCoverView.getMeasuredWidth();
            dimension.height = lessonCoverView.getMeasuredHeight();
            String url = Ctl.getCover(mDetail.cover, dimension);
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.default_lesson_cover)
                    .error(R.drawable.default_lesson_cover)
                    .into(lessonCoverView);
        }

        enrollCountView.setText((mDetail.enroll==null?0:mDetail.enroll.current)+"人报名");
        teacherAdapter.setList(mDetail.teachers);
        teacherAdapter.notifyDataSetChanged();
        tvDescription.setText(mDetail.overview==null?"暂无简介~":mDetail.overview.getText());
        teaCount.setText("（"+(mDetail.teachers==null?0:mDetail.teachers.length)+"）");
        if (mDetail.tags !=null && mDetail.tags.length > 0) {
            labelLayout.setVisibility(View.VISIBLE);

            for (String tag : mDetail.tags) {
                labelLayout.addText(tag);
            }

        }else {
            labelLayout.setVisibility(View.GONE);
        }


        if(mDetail.createdBy!=null&& AccountDataManager.getAccountID(this).equals(mDetail.createdBy.getId())){//是这个课的创建者

            if(mDetail.expire!=null){
                lessonValidView.setText("有效期："+mDetail.expire.effective+"天");
            }else {
                lessonValidView.setText("永久");
            }
            enrollButton.setEnabled(false);
        }else {//学生

            if(Ctl.CourseEnrollmentState.PENDING_FOR_ACCEPTANCE.equals(mDetail.enrollState)){//等待确认
                if(mDetail.expire!=null){
                    lessonValidView.setText("有效期："+mDetail.expire.effective+"天");
                }else {
                    lessonValidView.setText("永久");
                }
            }else if(Ctl.CourseEnrollmentState.ENROLLED.equals(mDetail.enrollState)){//已经加入
                if(mDetail.expire!=null&&mDetail.enrollOfCurrentAccount!=null){
                    lessonValidView.setText("有效期至"+ ScheduleUtil.getDateYMD(mDetail.enrollOfCurrentAccount.deadline));
                }else {
                    lessonValidView.setText("永久");
                }
                enrollButton.setText("立即学习");
            }else {//未加入
                if(mDetail.expire!=null){
                    lessonValidView.setText("有效期："+mDetail.expire.effective+"天");
                }else {
                    lessonValidView.setText("永久");
                }
            }
        }
        lessonCoverView.requestLayout();

    }


    private void doJoinRecordedLessonRequest(String msg){
        showProgress(false);
        JoinClassParams params=new JoinClassParams();
        params.remarks=msg;
        LessonDataManager.enrollRecordedCourse(this, mDetail.id,params, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelProgress();
                JoinResponse joinResponse = getJoinResponse(object);
                DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
                if (joinResponse == null) {
                    startActivity(new Intent(RecordedLessonEnrollActivity.this,EnrollSuccessActivity.class));
                }else if(!TextUtils.isEmpty(joinResponse.id)){
                    //此班是需要申请验证才能加入的班
                    ToastUtil.showToast(getApplicationContext(),"您已提交申请，请等待确认");
                    MainActivity.invokeWithAction(RecordedLessonEnrollActivity.this,null);
                }else {
                    startActivity(new Intent(RecordedLessonEnrollActivity.this,EnrollSuccessActivity.class));
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                ToastUtil.showToast(getApplicationContext(),errorMessage);
            }
        });
    }

    private JoinResponse getJoinResponse(ResponseBody body) {
        JoinResponse response = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = body.string();
            response = mapper.readValue(json, JoinResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public void checkJoinNeedVerify(){
        boolean needVerify=mDetail.enroll!=null&&mDetail.enroll.mode==Ctl.JoinMode.VERIFICATION;
        if(needVerify){
            showJoinRecordedLessonVerifyMsgDialog();
        }else {
            doJoinRecordedLessonRequest(null);
        }

    }

    private void showJoinRecordedLessonVerifyMsgDialog(){
        final CommonDialog dialog=new CommonDialog(this);
        dialog.setTitle(R.string.add_recorded_lesson_verification_msg);
        final EditText editText=new EditText(this);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setHint(R.string.add_recorded_lesson_verify_msg_tip);
        editText.setLines(4);
        editText.setTextColor(getResources().getColor(R.color.font_black));
        editText.setBackgroundResource(R.drawable.common_search_bg);
        editText.setGravity(Gravity.LEFT|Gravity.TOP);
        int padding=getResources().getDimensionPixelSize(R.dimen.px10);
        editText.setPadding(padding,padding,padding,padding);
        editText.setHintTextColor(getResources().getColor(R.color.font_gray));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_28px));
        dialog.setCustomView(editText);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                doJoinRecordedLessonRequest(editText.getText().toString().trim());
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        private Account[] list;

        private void setList(Account[] list){
            this.list=list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemHolder holder=new ItemHolder(View.inflate(parent.getContext(), R.layout.item_lesson_operate, null));
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            return holder;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            final Account account=list[position];
            holder.name.setText(account.getBasic().getName());
            Glide.with(RecordedLessonEnrollActivity.this)
                    .load(cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(account.getId(), 300))
                    .bitmapTransform(new CircleTransform(RecordedLessonEnrollActivity.this))
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(holder.icon);
            holder.btnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountInfo info=new AccountInfo();
                    info.id=account.getId();
                    SearchBusiness.goPersonal(RecordedLessonEnrollActivity.this,info);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list ==null?0: list.length;
        }
    }

    int itemWidth=0;
    private void initItemWidth(){
        itemWidth=(ScreenUtils.getScreenWidth(this)-getResources().getDimensionPixelSize(R.dimen.px80))/4;
        Logger.d("----qz----initItemWidth="+itemWidth);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.btn_item)
        View btnItem;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static void invoke(Context context,String lessonId){
        Intent intent=new Intent(context,RecordedLessonEnrollActivity.class);
        intent.putExtra(EXTRA_LESSON_ID,lessonId);
        context.startActivity(intent);
    }

}
