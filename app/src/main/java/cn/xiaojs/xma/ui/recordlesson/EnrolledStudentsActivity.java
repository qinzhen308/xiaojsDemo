package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/7/24.
 */

public class EnrolledStudentsActivity extends BaseActivity {

    @BindView(R.id.student_list)
    PullToRefreshSwipeListView listView;

    @BindView(R.id.lay_veri)
    RelativeLayout verLayout;
    @BindView(R.id.veri_count)
    TextView veriCount;


    private boolean teaching;

    private StudentsAdapter adapter;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_enrolled_students);
        setMiddleTitle(R.string.enroll_register_stu);

        init();
    }

    @OnClick({R.id.left_image, R.id.right_image2, R.id.lay_veri})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:      //返回
                finish();
                break;
            case R.id.right_image2:    //报名注册
                // TODO
                break;
            case R.id.lay_veri:        //报名确认
                // TODO
                break;

        }
    }

    private void init() {


        adapter = new StudentsAdapter(this, listView);
        if (teaching) {
            listView.enableLeftSwipe(true);
            setRightText(R.string.lesson_op_signup);
        } else {
            listView.enableLeftSwipe(false);
        }


        listView.setAdapter(adapter);
    }


    class StudentsAdapter extends AbsSwipeAdapter<StudentEnroll, StudentsAdapter.Holder> {


        public StudentsAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context, listView);
        }

        @Override
        protected void setViewContent(Holder holder, StudentEnroll bean, int position) {

            //TODO
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_rl_enrolled_student_item, null);
        }

        @Override
        protected Holder initHolder(View view) {
            return new Holder(view);
        }

        @Override
        protected void doRequest() {
            //TODO
        }

        class Holder extends BaseHolder {

            @BindView(R.id.ic_nav)
            ImageView name;
            @BindView(R.id.name)
            TextView nameView;
            @BindView(R.id.phone)
            TextView phoneView;
            @BindView(R.id.time_env)
            TextView timeEnvView;


            public Holder(View view) {
                super(view);
            }
        }
    }


}
