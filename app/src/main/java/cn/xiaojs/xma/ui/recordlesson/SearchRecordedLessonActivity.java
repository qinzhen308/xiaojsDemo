package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.CollectionCalendar;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.ScheduleOptions;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.recordedlesson.RecordedLessonCriteria;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.xclass.HomeClassAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.recordlesson.util.RLessonFilterHelper;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/31.
 * 搜索录播课
 */

public class SearchRecordedLessonActivity extends BaseActivity implements IUpdateMethod{


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.search_input)
    EditTextDel searchInput;
    @BindView(R.id.search_ok)
    TextView searchOk;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.search_empty)
    FrameLayout searchEmpty;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindString(R.string.cancel)
    String btn_text_cancel;

    @BindString(R.string.search)
    String btn_text_search;

    HomeClassAdapter mAdapter;

    private final static int BEGIN_SEARCH=0xff;
    DataPageLoader<RLesson,CollectionPage<RLesson>> dataPageLoader;
    Pagination mPagination;
    String key;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_search_lesson);
        needHeader(false);
        mAdapter=new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerview.setAdapter(mAdapter);
        searchInput.setHint(R.string.hint_input_recorded_lesson_name);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = searchInput.getText().toString();
                if(query.length()>0){
                    searchOk.setText(btn_text_search);
                }else {
                    searchOk.setText(btn_text_cancel);
                }
            }
        });
        initPageLoad();
    }



    @OnClick({R.id.back, R.id.search_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                break;
            case R.id.search_ok:
                searchOrCancel(searchOk.getText().toString());
                break;
        }
    }

    private void searchOrCancel(String text){
        if(btn_text_search.equals(text)){
            search();
        }else {
            finish();
        }
    }

    private void search(){
        key=searchInput.getText().toString();
        dataPageLoader.refresh();
    }

    private void request(){
        RecordedLessonCriteria criteria=new RecordedLessonCriteria();
        criteria.title=key;
        LessonDataManager.getRecordedCourses(this,criteria,mPagination , dataPageLoader);
    }

    private boolean verifyInput(String content){
        if(content.length()<2){
            Toast.makeText(this,"不能少于2个字符",Toast.LENGTH_LONG).show();
            return false;
        }

        if(content.length()>8){
            Toast.makeText(this,"不能多余8个字符",Toast.LENGTH_LONG).show();
            return false;
        }
        String regEx="[a-zA-Z0-9_\u4e00-\u9fa5]+";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(content);
        if(!matcher.matches()){
            Toast.makeText(this,R.string.subject_input_tip,Toast.LENGTH_LONG).show();
            return false;
        }
        regEx="[(0-9)]+";
        pattern=Pattern.compile(regEx);
        matcher=pattern.matcher(content);
        if(matcher.matches()){
            Toast.makeText(this,R.string.subject_input_tip2,Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void initPageLoad(){
        mPagination=new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(10);
        dataPageLoader=new DataPageLoader<RLesson,CollectionPage<RLesson>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;
            @Override
            public void onRequst(int page) {
                searchEmpty.setVisibility(View.GONE);
                mPagination.setPage(page);
                request();
            }

            @Override
            public List<RLesson> adaptData(CollectionPage<RLesson> object) {
                if(object==null)return new ArrayList<>();
                return object.objectsOfPage;
            }

            @Override
            public void onSuccess(List<RLesson> curPage, List<RLesson> all) {
                pageChangeInRecyclerView.completeLoading();
                if(ArrayUtil.isEmpty(all)){
                    searchEmpty.setVisibility(View.VISIBLE);
                }else {
                    searchEmpty.setVisibility(View.GONE);
                }
                bindData(all);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
                searchEmpty.setVisibility(View.VISIBLE);
                bindData(new ArrayList<RLesson>());
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView=new PageChangeInRecyclerView(recyclerview,this);

            }
        };
    }


    private void bindData(List<RLesson> list){
//        ArrayList datas=new ArrayList();
//        datas.addAll(list);
//        datas.add(new LastEmptyModel());
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
    }


    public static void invoke(Context context){
        Intent intent=new Intent(context,SearchRecordedLessonActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CourseConstant.CODE_EDIT_LESSON:
                case CourseConstant.CODE_LESSON_AGAIN:
                    updateData(false);
                    break;
            }
        }
    }

    @Override
    public void updateData(boolean justNative,Object... others) {
        if (justNative) {
            mAdapter.notifyDataSetChanged();
        } else {
            dataPageLoader.refresh();
        }
    }

    @Override
    public void updateItem(int position, Object obj,Object... others) {
        if(position<mAdapter.getItemCount()){
            Object item=mAdapter.getList().get(position);
            //由于有些操作是异步的，为了防止在本方法调用前，列表已经刷新过，作如下判断
            if(item instanceof RLesson&&((RLesson)item).id.equals(((RLesson)obj).id)){
                if(others.length>0&&others[0].equals("remove")){
                    mAdapter.getList().remove(position);

                }else {
                    mAdapter.getList().set(position,obj);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
