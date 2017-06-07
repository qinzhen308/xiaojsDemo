package cn.xiaojs.xma.ui.lesson.xclass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by Paul Z on 2017/5/19.
 */

public class ClassFragment extends BaseFragment implements View.OnClickListener{
    @BindView(R.id.home_root)
    FrameLayout homeRoot;
    @BindView(R.id.layout_content_empty)
    ViewStub emptyStub;
    @BindView(R.id.layout_content_normal)
    ViewStub normalStub;

    View layoutEmpty;
    View layoutNormal;

    public boolean isEmpty = false;
    ImageView btnScan;
    TextView tabWannaLean;
    TextView tabWannaTeach;
    LinearLayout layoutWanna;
    RelativeLayout layoutCreativeLesson;
    RelativeLayout layoutCreativeClass;
    LinearLayout layoutSearch;

    @BindColor(R.color.white) int textColorWhite;
    @BindColor(R.color.font_light_gray) int textColorGray;

    private int curTabIndex=TAB_WANNA_LEARN;
    private final static int TAB_WANNA_LEARN=0;
    private final static int TAB_WANNA_TEACH=1;

    HomeClassContentBuz contentBuz;

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_home_class, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void init() {
        if (!AccountPref.getUser(getActivity()).hasClass) {
            emptyStub.setLayoutResource(R.layout.fragment_home_class_empty);
            layoutEmpty = emptyStub.inflate();
            btnScan=(ImageView) layoutEmpty.findViewById(R.id.btn_scan);
            tabWannaLean=(TextView) layoutEmpty.findViewById(R.id.tab_wanna_lean);
            tabWannaTeach=(TextView) layoutEmpty.findViewById(R.id.tab_wanna_teach);
            layoutWanna=(LinearLayout) layoutEmpty.findViewById(R.id.layout_wanna);
            layoutCreativeLesson=(RelativeLayout) layoutEmpty.findViewById(R.id.layout_creative_lesson);
            layoutCreativeClass=(RelativeLayout) layoutEmpty.findViewById(R.id.layout_creative_class);
            layoutSearch=(LinearLayout) layoutEmpty.findViewById(R.id.layout_search);
            layoutEmpty.findViewById(R.id.tv_search).setOnClickListener(this);
            tabWannaLean.setOnClickListener(this);
            tabWannaTeach.setOnClickListener(this);
            layoutCreativeLesson.setOnClickListener(this);
            layoutCreativeClass.setOnClickListener(this);
            btnScan.setOnClickListener(this);
            drawable=(TransitionDrawable) getResources().getDrawable(R.drawable.bg_home_class_trans);
            layoutEmpty.setBackground(drawable);
        } else {
            normalStub.setLayoutResource(R.layout.fragment_home_class_normal);
            layoutNormal = normalStub.inflate();
            contentBuz=new HomeClassContentBuz();
            contentBuz.init(getActivity(),layoutNormal);
        }
    }

    private void changeToNormal() {
        normalStub.setLayoutResource(R.layout.fragment_home_class_normal);
        layoutNormal = normalStub.inflate();
        homeRoot.removeView(layoutEmpty);
        layoutEmpty = null;
        contentBuz=new HomeClassContentBuz();
        contentBuz.init(getActivity(),layoutNormal);
    }

//    @Optional @OnClick ({R.id.layout_search,R.id.tab_wanna_lean,R.id.tab_wanna_teach})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_scan:
                if (PermissionUtil.isOverMarshmallow()
                        && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.PERMISSION_CODE);

                } else {
                    startActivity(new Intent(mContext, ScanQrcodeActivity.class));
                }
                break;
            case R.id.tab_wanna_lean:
                changeTab(TAB_WANNA_LEARN);
                break;
            case R.id.tab_wanna_teach:
                changeTab(TAB_WANNA_TEACH);
                break;
            case R.id.tv_search:
                startActivityForResult(new Intent(getActivity(),SearchActivity.class),100);
                break;
            case R.id.layout_creative_lesson:
                if(JudgementUtil.checkTeachingAbility(mContext)){
                    Intent open = new Intent(mContext, LessonCreationActivity.class);
                    mContext.startActivity(open);
                }
                break;
            case R.id.layout_creative_class:
                if(JudgementUtil.checkTeachingAbility(mContext)){
                    mContext.startActivity(new Intent(mContext,CreateClassActivity.class));
                }
                break;
            default:
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void changeTab(int tab){
        if(tab==curTabIndex)return;
        curTabIndex=tab;
        if(tab==TAB_WANNA_LEARN){
            tabWannaLean.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_50px));
            tabWannaLean.setTextColor(textColorWhite);
            tabWannaLean.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.shape_line);
            tabWannaTeach.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_35px));
            tabWannaTeach.setTextColor(textColorGray);
            tabWannaTeach.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            changeToLearnAnim();
        }else {
            tabWannaLean.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_35px));
            tabWannaLean.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            tabWannaLean.setTextColor(textColorGray);
            tabWannaTeach.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_50px));
            tabWannaTeach.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.shape_line);
            tabWannaTeach.setTextColor(textColorWhite);
            changeToTeachAnim();
        }
    }

    public void updateData(){
        if(layoutNormal!=null){
            contentBuz.update();
        }
    }


    //-------------------------动画----------------

    private final int ANIM_DURATION=300;
    private void changeToTeachAnim(){
        changeBgAnim(false);
//        AnimationSet anim=new AnimationSet(false);
        TranslateAnimation ta=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,1,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
        ta.setInterpolator(new FastOutSlowInInterpolator());
        ta.setDuration(ANIM_DURATION);
        layoutCreativeClass.startAnimation(ta);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutCreativeClass.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        TranslateAnimation ta1=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,1,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
        ta1.setInterpolator(new FastOutSlowInInterpolator());
        ta1.setDuration(ANIM_DURATION);
        ta1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutCreativeLesson.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        layoutCreativeLesson.startAnimation(ta1);

        TranslateAnimation ta2=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,1);
        ta2.setInterpolator(new FastOutSlowInInterpolator());
        ta2.setDuration(ANIM_DURATION);
        ta2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutSearch.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layoutSearch.startAnimation(ta2);
    }

    private void changeToLearnAnim(){
        changeBgAnim(true);
//        AnimationSet anim=new AnimationSet(false);
        TranslateAnimation ta=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,1,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
        ta.setInterpolator(new FastOutSlowInInterpolator());
        ta.setDuration(ANIM_DURATION);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutCreativeClass.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        layoutCreativeClass.startAnimation(ta);

        TranslateAnimation ta1=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,1,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
        ta1.setInterpolator(new FastOutSlowInInterpolator());
        ta1.setDuration(ANIM_DURATION);
        ta1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutCreativeLesson.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        layoutCreativeLesson.startAnimation(ta1);


        TranslateAnimation ta2=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,1,Animation.RELATIVE_TO_PARENT,0);
        ta2.setInterpolator(new FastOutSlowInInterpolator());
        ta2.setDuration(ANIM_DURATION);
        ta2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layoutSearch.startAnimation(ta2);
    }
    TransitionDrawable drawable;

    private void changeBgAnim(boolean toLearn){
        if(toLearn){
            drawable.reverseTransition(ANIM_DURATION);
        }else {
            drawable.startTransition(ANIM_DURATION);
        }
    }

    @Override
    protected int registerDataChangeListenerType() {
        return SimpleDataChangeListener.CREATE_CLASS_CHANGED;
    }


    @Override
    protected void onDataChanged() {
        if(layoutEmpty!=null){
            changeToNormal();
        }
    }
}
