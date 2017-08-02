package cn.xiaojs.xma.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.recordedlesson.RLCollectionPageData;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;

/**
 * Created by Paul Z on 2017/7/23.
 */
public class PersonHomeClassAdapter extends AbsSwipeAdapter<RLesson, PersonHomeClassAdapter.Holder> {

    private String mAccount;

    public PersonHomeClassAdapter(Context context, PullToRefreshSwipeListView list, String account) {
        super(context, list);
        mAccount = account;
        setDesc(context.getString(R.string.teacher_nerver_create_class));
    }

    public PersonHomeClassAdapter(Context context, PullToRefreshSwipeListView list, List<RLesson> data) {
        super(context, list, data);
    }

    @Override
    protected void setViewContent(Holder holder, RLesson bean, int position) {
        holder.titleView.setText(bean.title);
        holder.flagView.setText(bean.title.charAt(0)+"");
        setJoinState(holder.btnJoin,false);
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_person_home_class_item, null);
        return view;
    }

    @Override
    protected void onDataItemClick(int position, RLesson bean) {
        String state = bean.state;
        if (TextUtils.isEmpty(state)
                || state.equalsIgnoreCase(LessonState.ACKNOWLEDGED)
                || state.equalsIgnoreCase(LessonState.PENDING_FOR_ACK)
                || state.equalsIgnoreCase(LessonState.DRAFT)
                || state.equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)
                || state.equalsIgnoreCase(LessonState.CANCELLED)
                || state.equalsIgnoreCase(LessonState.REJECTED)) {
            return;
        }

        Intent i = new Intent(mContext, LessonHomeActivity.class);
        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
        i.putExtra(CourseConstant.KEY_LESSON_ID, bean.id);
        mContext.startActivity(i);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        LessonDataManager.getClassesByUser(mContext, mAccount, mPagination, new APIServiceCallback<RLCollectionPageData<RLesson>>() {
            @Override
            public void onSuccess(RLCollectionPageData<RLesson> object) {
                if (object != null && object.recordedCourse != null) {
                    PersonHomeClassAdapter.this.onSuccess(object.recordedCourse);
                } else {
                    PersonHomeClassAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                PersonHomeClassAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    @Override
    protected void setEmptyLayoutParams(View view, RelativeLayout.LayoutParams params) {
        if (params != null) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.px300);
            view.setLayoutParams(params);

            View click = view.findViewById(R.id.empty_click);
            if (click != null) {
                click.setVisibility(View.GONE);
            }

            View descImg = view.findViewById(R.id.empty_image);
            if (descImg != null) {
                descImg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void setFailedLayoutParams(View view, RelativeLayout.LayoutParams params) {
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.px60);
        view.setLayoutParams(params);

        View click = view.findViewById(R.id.empty_click);
        if (click != null) {
            click.setVisibility(View.GONE);
        }
    }

    private void setJoinState(TextView v, boolean joined) {
        if (!joined) {
            v.setText("加入");
            v.setTextColor(mContext.getResources().getColor(R.color.main_orange));
            v.setBackgroundResource(R.drawable.orange_stoke_bg);
            v.setEnabled(true);
        } else {
            v.setText("已加入");
            v.setTextColor(mContext.getResources().getColor(R.color.common_text));
            v.setBackgroundResource(0);
            v.setEnabled(false);
        }
    }

    class Holder extends BaseHolder {
        @BindView(R.id.flag_view)
        TextView flagView;
        @BindView(R.id.title_view)
        TextView titleView;
        @BindView(R.id.member_view)
        TextView memberView;
        @BindView(R.id.teachers_view)
        TextView teachersView;
        @BindView(R.id.btn_join)
        TextView btnJoin;

        public Holder(View view) {
            super(view);
        }
    }
}
