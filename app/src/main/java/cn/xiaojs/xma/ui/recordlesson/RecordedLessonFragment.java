package cn.xiaojs.xma.ui.recordlesson;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.stateview.AppLoadState2;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.stateview.LoadStatusViewDecoratee;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshRecyclerView;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.recordedlesson.RecordedLessonCriteria;
import cn.xiaojs.xma.ui.lesson.xclass.HomeClassAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.lesson.xclass.view.MyClassFilterDialog;
import cn.xiaojs.xma.ui.recordlesson.util.RLessonFilterHelper;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/7/21.
 */

public class RecordedLessonFragment extends Fragment implements IUpdateMethod{


    @BindView(R.id.my_course_search)
    TextView searchView;
    @BindView(R.id.filter_line)
    View mFilterLine;
    @BindView(R.id.course_filter)
    TextView mFilter;
    @BindView(R.id.over_layout)
    PullToRefreshRecyclerView mPullRecyclerView;
    RecyclerView mRecyclerView;


    private Context context;

    protected int group1Position;
    protected int group2Position;

//    private LessonAdapter lessonAdapter;
    HomeClassAdapter mAdapter;
    DataPageLoader<RLesson,CollectionPage<RLesson>> dataPageLoader;
    Pagination mPagination;
    LoadStatusViewDecoratee stateView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = getActivity();
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_public_lesson, null);
        ButterKnife.bind(this,v);
        searchView.setHint(R.string.hint_input_recorded_lesson_name);

        mPullRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullRecyclerView.setFirstLoading(false);
        mPullRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                dataPageLoader.refresh();
            }
        });
        mRecyclerView=mPullRecyclerView.getRefreshableView();
        AppLoadState2 loadState=new AppLoadState2(getActivity(),(ViewGroup) v.findViewById(R.id.load_state_container));
        loadState.setTips(getString(R.string.filter_tip_lesson),null,null,null,null,null);
        stateView=new LoadStatusViewDecoratee(loadState);
//        stateView=new LoadStatusViewDecoratee(null);
        if(AccountDataManager.isTeacher(getActivity())){
            mFilter.setVisibility(View.VISIBLE);
        }else {
            mFilter.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        lessonAdapter = new LessonAdapter(context,listView);
//        lessonAdapter.setDesc(getString(R.string.teach_data_empty));
//        lessonAdapter.setIcon(R.drawable.ic_teach_empty);
//        lessonAdapter.setButtonDesc(getString(R.string.lesson_creation));

//        listView.setAdapter(lessonAdapter);
        mAdapter = new HomeClassAdapter(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        initPageLoad();
//        mPullRecyclerView.setRefreshing();
        getRecordLessonData();
    }

    @OnClick({R.id.course_filter,R.id.my_course_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.course_filter:
                filter();
                break;
            case R.id.my_course_search:
                SearchRecordedLessonActivity.invoke(getActivity());
                break;
        }
    }

    private int[] group2disables={0,1,2,3,4};

    private void filter() {
        final MyClassFilterDialog dialog = new MyClassFilterDialog(context, false);
        dialog.setGroup2Title(R.string.course_state);
        dialog.setGroup1Selection(group1Position);
        dialog.setGroup2Selection(group2Position);
        if(group1Position==2){
            dialog.setGroup2Disables(group2disables);
        }
        dialog.setGroup1(getResources().getStringArray(R.array.recorded_lesson_filter_type)).setGroup2(getResources().getStringArray(R.array.recorded_lesson_filter_state));
        dialog.showAsDropDown(mFilterLine);
        dialog.setOnGroup1ItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==2){
                    dialog.setGroup2Selection(0);
                    dialog.setGroup2Disables(group2disables);
                }else {
                    dialog.setGroup2Disables(null);
                }
            }
        });
        mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
            }
        });
        dialog.setOnOkListener(new MyClassFilterDialog.OnOkListener() {
            @Override
            public void onOk(int group1Position, int group2Position) {
                RecordedLessonFragment.this.group1Position = group1Position;
                RecordedLessonFragment.this.group2Position = group2Position;
//                LessonFilterHelper.getType(group1Position);
//                LessonFilterHelper.getState(group2Position);
                dataPageLoader.refresh();
            }
        });
    }


    private void initPageLoad(){
        mPagination=new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(10);
        dataPageLoader=new DataPageLoader<RLesson,CollectionPage<RLesson>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;
            @Override
            public void onRequst(int page) {
                mPagination.setPage(page);
                getRecordLessonData();
                stateView.change(LoadStateListener.STATE_LOADING,"");
            }

            @Override
            public List<RLesson> adaptData(CollectionPage<RLesson> object) {
                if(object==null)return new ArrayList<>();
                return object.objectsOfPage;
            }

            @Override
            public void onSuccess(List<RLesson> curPage, List<RLesson> all) {
                pageChangeInRecyclerView.completeLoading();
                mPullRecyclerView.onRefreshComplete();
                if(ArrayUtil.isEmpty(all)){
                    stateView.change(LoadStateListener.STATE_ALL_EMPTY,"");
                }else {
                    stateView.change(LoadStateListener.STATE_NORMAL,"");
                }
                bindData(all);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
                mPullRecyclerView.onRefreshComplete();
                bindData(new ArrayList<RLesson>());
                stateView.change(LoadStateListener.STATE_LOADING_ERROR,"");
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView=new PageChangeInRecyclerView(mRecyclerView,this);

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

    private void getRecordLessonData(){
        RecordedLessonCriteria criteria=new RecordedLessonCriteria();
        criteria.role= RLessonFilterHelper.getType(group1Position);
        criteria.state= RLessonFilterHelper.getState(group2Position);
        LessonDataManager.getRecordedCourses(getActivity(),criteria,mPagination , dataPageLoader);
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
