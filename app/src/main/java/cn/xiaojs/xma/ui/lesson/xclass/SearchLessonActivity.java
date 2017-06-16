package cn.xiaojs.xma.ui.lesson.xclass;

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
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.CollectionCalendar;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.ScheduleOptions;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/**
 * Created by Paul Z on 2017/5/31.
 * 搜索公开课
 */

public class SearchLessonActivity extends BaseActivity {


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
    DataPageLoader<ClassSchedule,CollectionCalendar<ClassSchedule>> dataPageLoader;
    Pagination mPagination;
    String key;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_search_lesson);
        needHeader(false);
        mAdapter=new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerview.setAdapter(mAdapter);
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
        ScheduleOptions options=new ScheduleOptions.Builder().setStart(ScheduleUtil.getUTCDate(new Date(0).getTime()))
                .setEnd(ScheduleUtil.getUTCDate(ScheduleUtil.ymdToTimeMill(2100,12,30)))
                .setState("All").setType(Account.TypeName.STAND_ALONE_LESSON).setQ(key)
                .build();
        LessonDataManager.getClassesSchedule4Lesson(this, options,mPagination,dataPageLoader);
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
        dataPageLoader=new DataPageLoader<ClassSchedule, CollectionCalendar<ClassSchedule>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;
            @Override
            public void onRequst(int page) {
                mPagination.setPage(page);
                request();
            }

            @Override
            public List<ClassSchedule> adaptData(CollectionCalendar<ClassSchedule> object) {
                if(object==null)return new ArrayList<>();
                return object.calendar;
            }

            @Override
            public void onSuccess(List<ClassSchedule> curPage, List<ClassSchedule> all) {
                bindData(all);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                bindData(new ArrayList<ClassSchedule>());
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView=new PageChangeInRecyclerView(recyclerview,this);

            }
        };
    }

    private void bindData(List<ClassSchedule> list){
        ArrayList monthLists=new ArrayList();
        LessonLabelModel tempLabel=null;
        for(int j=0;j<list.size();j++){
            ClassSchedule cs=list.get(j);
            tempLabel=new LessonLabelModel(ScheduleUtil.getDateYMDW(cs.date),0,false);
            monthLists.add(tempLabel);
            monthLists.addAll(cs.lessons);
            tempLabel.lessonCount=cs.lessons.size();
            if(cs.lessons.size()>0){
                tempLabel.hasData=true;
            }
        }
        monthLists.add(new LastEmptyModel());
        mAdapter.setList(monthLists);
        mAdapter.notifyDataSetChanged();
    }




}
