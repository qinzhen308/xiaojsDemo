package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.ctl.JoinCriteria;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.recordlesson.EnrolledStudentsActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2017/5/21.
 */

public class StudentsListActivity extends BaseActivity {

    public static final String EXTRA_CLASS = "class";
    public static final int REQUEST_ADD_STUDENT_CODE = 0x1;

    @BindView(R.id.student_list)
    PullToRefreshSwipeListView listView;
    @BindView(R.id.lay_veri)
    RelativeLayout verLayout;
    @BindView(R.id.veri_count)
    TextView veriCount;

    private String classId;
    private boolean teaching;

    private StudentsListAdapter adapter;

    @BindView(R.id.search_input)
    EditTextDel searchInput;
    @BindView(R.id.search_ok)
    TextView searchOk;

    String keyword;

    private final static int BEGIN_SEARCH=0xff;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_students_list);
        setMiddleTitle(getString(R.string.class_students));

        classId = getIntent().getStringExtra(EXTRA_CLASS);
        teaching = getIntent().getBooleanExtra(ClassInfoActivity.EXTRA_TEACHING, false);
        initView();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*String query = searchInput.getText().toString();
                if (query.length() > 0) {
                    searchOk.setVisibility(View.VISIBLE);
                } else {
                    searchOk.setVisibility(View.GONE);
                }*/
                toSearch();
            }
        });
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword=searchInput.getText().toString().trim();
                    if (adapter != null) {
                        adapter.resetAndrequest();
                    }
                    XjsUtils.hideIMM(StudentsListActivity.this);
                    return true;
                }
                return false;
            }
        });

    }

    @OnClick({R.id.left_image, R.id.right_image2, R.id.lay_veri, R.id.search_ok})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2:
                // 添加
                showAddDlg();
                break;
            case R.id.lay_veri:
                // 进入验证消息页面
                Intent i = new Intent(this, VerificationActivity.class);
                i.putExtra(EXTRA_CLASS, classId);
                startActivity(i);
                break;
            case R.id.search_ok:        //搜索
                keyword=searchInput.getText().toString().trim();
                if (adapter != null) {
                    adapter.resetAndrequest();
                }
                break;
        }
    }

    private void initView() {
        boolean verify = getIntent().getBooleanExtra(ClassInfoActivity.EXTRA_VERI,false);
        adapter = new StudentsListAdapter(this,
                listView);
        if (teaching ) {
            listView.enableLeftSwipe(true);
            setRightText(R.string.add);
            if (verify) {
                verLayout.setVisibility(View.VISIBLE);
            }
        }else {
            listView.enableLeftSwipe(false);
        }


        listView.setAdapter(adapter);
    }

    private void showAddDlg() {

        ListBottomDialog dialog = new ListBottomDialog(this);

        String[] items = getResources().getStringArray(R.array.add_student);
        dialog.setMiddleText(getString(R.string.add_student_tips));
        dialog.setItems(items);
        dialog.setTitleVisibility(View.VISIBLE);
        dialog.setTitleBackground(R.color.white);
        dialog.setRightBtnVisibility(View.GONE);
        dialog.setLeftBtnVisibility(View.GONE);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://手动添加
                        Intent addIntent = new Intent(StudentsListActivity.this,
                                AddStudentActivity.class);
                        addIntent.putExtra(AddStudentActivity.EXTRA_CLASS_ID, classId);
                        startActivityForResult(addIntent, REQUEST_ADD_STUDENT_CODE);
                        break;
                    case 1://从已有班级中添加

                        Intent i = new Intent(StudentsListActivity.this,
                                ImportStudentFormClassActivity.class);
                        i.putExtra(ClassInfoActivity.EXTRA_CLASSID, classId);
                        startActivityForResult(i,ImportStudentFormClassActivity.CODE_ADD_STUDENTS_BATCH);
                        break;
                }

            }

        });
        dialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImportStudentFormClassActivity.CODE_ADD_STUDENTS_BATCH:
                case REQUEST_ADD_STUDENT_CODE:
                    //通过EXTRA_STUDENTS，可以获取到新添加的学生
                    //data.getParcelableArrayListExtra(ManualAddStudentActivity.EXTRA_STUDENTS);
                    //直接通过API刷新界面
                    if (adapter != null) {
                        adapter.resetAndrequest();
                    }
                    break;
            }
        }
    }

    private void setMsgCount(int count){
        if(count<=0){
            veriCount.setVisibility(View.GONE);
        }else if(count<100){
            veriCount.setText(""+count);
            veriCount.setVisibility(View.VISIBLE);
        }else {
            veriCount.setText("99+");
            veriCount.setVisibility(View.VISIBLE);
        }
    }

    public class StudentsListAdapter extends AbsSwipeAdapter<StudentEnroll, StudentsListAdapter.Holder> {

        public StudentsListAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context, listView);
        }

        @Override
        protected void setViewContent(StudentsListAdapter.Holder holder, StudentEnroll bean, int position) {
            holder.nameView.setText(bean.name);
            if (teaching) {
                holder.phoneView.setText(String.valueOf(bean.mobile));
            }else {
                holder.phoneView.setText(StringUtil.protectCardNo(String.valueOf(bean.mobile)));
            }

        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_student_name_num_item, null);
        }

        @Override
        protected StudentsListAdapter.Holder initHolder(View view) {
            return new StudentsListAdapter.Holder(view);
        }

        @Override
        protected void doRequest() {
            JoinCriteria criteria=new JoinCriteria();
            criteria.joined=true;
            if (!TextUtils.isEmpty(keyword)) {
                criteria.title = keyword;
            } else {
                criteria.title = null;
            }
            LessonDataManager.getClassStudents(mContext, classId, criteria, mPagination,
                    new APIServiceCallback<CollectionPage<StudentEnroll>>() {
                        @Override
                        public void onSuccess(CollectionPage<StudentEnroll> object) {

                            if (object != null) {
                                StudentsListAdapter.this.onSuccess(object.objectsOfPage);
                                setMsgCount(object.countOfApplying);
                            }
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            StudentsListAdapter.this.onFailure(errorCode, errorMessage);
                        }
                    });

        }

        public void enableLeftSwipe(boolean enable){
            leftSwipe=enable;
        }

        private boolean leftSwipe=true;
        @Override
        protected boolean leftSwipe() {
            return leftSwipe;
        }

        @Override
        protected void onAttachSwipe(TextView mark, TextView del) {
            mark.setVisibility(View.GONE);
            setLeftOffset(mContext.getResources().getDimension(R.dimen.px140));
        }

        @Override
        protected void onSwipeDelete(final int position) {
            StudentEnroll studentEnroll = getItem(position);
            if (studentEnroll != null) {

                showProgress(true);
                LessonDataManager.removeClassStudent(mContext, classId, new String[]{studentEnroll.id},
                        new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress();
                        removeItem(position);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                        cancelProgress();
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public void resetAndrequest() {
            reset();
            request();
        }

        class Holder extends BaseHolder {
            @BindView(R.id.name)
            TextView nameView;
            @BindView(R.id.phone)
            TextView phoneView;

            public Holder(View view) {
                super(view);
            }
        }
    }

    private void toSearch() {
        handler.removeMessages(BEGIN_SEARCH);
        String query = searchInput.getText().toString();
        Message msg=new Message();
        msg.what=BEGIN_SEARCH;
        msg.obj=query;
        handler.sendMessageDelayed(msg,300);
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(XiaojsConfig.DEBUG){
                Logger.d("qz--handleMessage---what="+msg.what+"---key="+msg.obj);
            }
            if(msg.what==BEGIN_SEARCH){
                String key=msg.obj.toString();
                if(TextUtils.isEmpty(key)){
                    keyword=null;
                }else {
                    keyword=key;
                }
                adapter.resetAndrequest();
            }
        }
    };



}
