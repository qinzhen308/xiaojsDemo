package cn.xiaojs.xma.common.pageload;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.stateview.AppLoadState;
import cn.xiaojs.xma.common.pageload.stateview.LoadStatusViewDecoratee;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInPullRefresh;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.CategoriesManager;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectSearchAdapter;

/**
 * Created by Administrator on 2017/5/16.
 */

public class TestActivity extends Activity {
    @BindView(R.id.listview)
    PullToRefreshSwipeListView mListView;
    DataPageLoader<CSubject,CollectionPage<CSubject>> loader;
    TeachingSubjectSearchAdapter mAdapter;
    Pagination mPagination;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        init();
        request();
    }
    private void init(){
        mPagination=new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(10);
        loader=new DataPageLoader<CSubject, CollectionPage<CSubject>>() {
            LoadStatusViewDecoratee loadState;
            @Override
            public void onRequst(int page) {
                mPagination.setPage(page);
                request();
            }

            @Override
            public List<CSubject> adaptData(CollectionPage<CSubject> object) {
                return object.objectsOfPage;
            }

            @Override
            public void onSuccess(List<CSubject> curPage, List<CSubject> all) {
                mListView.onRefreshComplete();
                mAdapter.setList((List<Object>) ((Object) all));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                mListView.onRefreshComplete();
            }

            @Override
            public void prepare() {
                new PageChangeInPullRefresh(mListView,this);
                loadState=new LoadStatusViewDecoratee(new AppLoadState(TestActivity.this,mListView));

            }

        };

    }

    private void initView(){
        setContentView(R.layout.activity_teaching_subject_search);
        ButterKnife.bind(this);
        mAdapter=new TeachingSubjectSearchAdapter(this);
//        mListView.setAdapter(mAdapter);
    }

    private void request(){
//        CategoriesManager.searchSubjects(this,"数",mPagination,loader);
    }
}
