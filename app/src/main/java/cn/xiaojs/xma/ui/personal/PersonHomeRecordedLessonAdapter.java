package cn.xiaojs.xma.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

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
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.PersonHomeUserLesson;
import cn.xiaojs.xma.model.recordedlesson.RLCollectionPageData;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonEnrollActivity;
import cn.xiaojs.xma.ui.widget.LiveingImageView;
import cn.xiaojs.xma.util.TimeUtil;
import okhttp3.ResponseBody;

/**
 * Created by Paul Z on 2017/7/23.
 */
public class PersonHomeRecordedLessonAdapter extends AbsSwipeAdapter<RLesson, PersonHomeRecordedLessonAdapter.Holder> {
    private String mAccount;

    public PersonHomeRecordedLessonAdapter(Context context, PullToRefreshSwipeListView list, String account) {
        super(context, list);
        mAccount = account;
        setDesc(context.getString(R.string.teacher_resetting));
    }

    public PersonHomeRecordedLessonAdapter(Context context, PullToRefreshSwipeListView list, List<RLesson> data) {
        super(context, list, data);
    }

    @Override
    protected void setViewContent(Holder holder, RLesson bean, int position) {
        holder.title.setText(bean.title);
        Dimension dimension = new Dimension();
        dimension.width = CourseConstant.COURSE_COVER_WIDTH;
        dimension.height = CourseConstant.COURSE_COVER_HEIGHT;
        Glide.with(mContext)
                .load(Ctl.getCover(bean.cover, dimension))
                .placeholder(R.drawable.default_lesson_cover)
                .error(R.drawable.default_lesson_cover)
                .into(holder.image);
        if (bean.expire != null&&bean.expire.effective>0) {
            holder.time.setText("有效期"+bean.expire.effective+"天");
        }else {
            holder.time.setText("永久");
        }

        holder.enroll.setText((bean.enroll != null ? bean.enroll.current : 0) + "人报名");
        /*if (bean.enroll !=null && bean.enroll.mandatory) {
            holder.enroll.setText((bean.enroll != null ? bean.enroll.current : 0) + "人报名");
        }else{
            holder.enroll.setText(mContext.getString(R.string.want_to_learn_num,
                    bean.enroll ==null? 0: bean.enroll.current));
        }*/

    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_person_home_recorded_lesson_item, null);
        return view;
    }

    @Override
    protected void onDataItemClick(int position, RLesson bean) {
        RecordedLessonEnrollActivity.invoke(mContext,bean.id);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        LessonDataManager.getRecordedCourseByUser(mContext, mAccount, mPagination, new APIServiceCallback<RLCollectionPageData<RLesson>>() {
            @Override
            public void onSuccess(RLCollectionPageData<RLesson> object) {
                if (object != null && object.recordedCourse != null) {
                    PersonHomeRecordedLessonAdapter.this.onSuccess(object.recordedCourse);
                } else {
                    PersonHomeRecordedLessonAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                PersonHomeRecordedLessonAdapter.this.onFailure(errorCode, errorMessage);
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

    class Holder extends BaseHolder {

        @BindView(R.id.lesson_adapter_name)
        TextView title;
        @BindView(R.id.lesson_adapter_image)
        ImageView image;
        @BindView(R.id.lesson_adapter_time)
        TextView time;
        @BindView(R.id.lesson_adapter_enroll)
        TextView enroll;

        public Holder(View view) {
            super(view);
        }
    }
}
