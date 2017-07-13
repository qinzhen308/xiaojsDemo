package cn.xiaojs.xma.ui.search;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/12
 * Desc:搜索个人、机构、课
 *
 * modify:Paul Z
 * date:2017/7/13
 *
 * ======================================================================================== */

import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.stateview.AppLoadState2;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.stateview.LoadStatusViewDecoratee;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;
import cn.xiaojs.xma.util.ArrayUtil;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_input)
    EditText mInput;
    @BindView(R.id.search_ok)
    TextView searchOk;
    @BindView(R.id.tab1)
    RadioButton tab1;
    @BindView(R.id.tab2)
    RadioButton tab2;
    @BindView(R.id.tab3)
    RadioButton tab3;
    @BindView(R.id.tab4)
    RadioButton tab4;
    @BindView(R.id.tab5)
    RadioButton tab5;
    @BindView(R.id.tab6)
    RadioButton tab6;
    @BindView(R.id.tab_bar)
    RadioGroup tabBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    CommonRVAdapter mAdapter;
    private final static int BEGIN_SEARCH=0xff;
    private final static int MAX_PER_PAGE=10;

    String typeName=Social.SearchType.ALL;
    String keywords;

    DataPageLoader<SearchResultV2,CollectionResult<SearchResultV2>> dataPageLoader;
    LoadStatusViewDecoratee stateView;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_global_search_v2);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter=new CommonRVAdapter(recyclerview);
        recyclerview.setAdapter(mAdapter);
        needHeader(false);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toSearch();
            }
        });

        tabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.tab1:
                        typeName= Social.SearchType.ALL;
                        break;
                    case R.id.tab2:
                        typeName= Social.SearchType.LESSON;
                        break;
                    case R.id.tab3:
                        typeName= Social.SearchType.LESSON;

                        break;
                    case R.id.tab4:
                        typeName= Social.SearchType.CLASS;
                        break;
                    case R.id.tab5:
                        typeName= Social.SearchType.PERSON;
                        break;
                    case R.id.tab6:
                        typeName= Social.SearchType.ORGANIZATION;
                        break;
                }
                dataPageLoader.refresh();
            }
        });

        stateView=new LoadStatusViewDecoratee(new AppLoadState2(this,(ViewGroup) findViewById(R.id.load_state_container)));
        initPageLoad();
    }

    private void initPageLoad(){
        dataPageLoader=new DataPageLoader<SearchResultV2,CollectionResult<SearchResultV2>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;
            @Override
            public void onRequst(int page) {
                stateView.change(LoadStateListener.STATE_LOADING,"");
                searchRequest(page);
            }

            @Override
            public List<SearchResultV2> adaptData(CollectionResult<SearchResultV2> object) {
                if(object==null)return new ArrayList<>();
                return object.results;
            }

            @Override
            public void onSuccess(List<SearchResultV2> curPage, List<SearchResultV2> all) {
                pageChangeInRecyclerView.completeLoading();
                getIntent().putExtra("extra_search_keywords",keywords);
                if(ArrayUtil.isEmpty(all)){
                    stateView.change(LoadStateListener.STATE_ALL_EMPTY,"");
                    bindData(new ArrayList<SearchResultV2>());
                }else {
                    stateView.change(LoadStateListener.STATE_NORMAL,"");
                    bindData(all);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
                stateView.change(LoadStateListener.STATE_LOADING_ERROR,"");
                bindData(new ArrayList<SearchResultV2>());
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView=new PageChangeInRecyclerView(recyclerview,this);
            }
        };
    }

    private void toSearch() {
        handler.removeMessages(BEGIN_SEARCH);
        String query = mInput.getText().toString();
        Message msg=new Message();
        msg.what=BEGIN_SEARCH;
        msg.obj=query;
        handler.sendMessageDelayed(msg,300);
    }

    @OnClick({R.id.back, R.id.search_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.search_ok:
                finish();
                break;
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(XiaojsConfig.DEBUG){
                Logger.d("qz--handleMessage---what="+msg.what+"---key="+msg.obj);
            }
            if(msg.what==BEGIN_SEARCH){
                String key=msg.obj.toString();
                if(!TextUtils.isEmpty(key)){
                    keywords=key;
                    dataPageLoader.refresh();
                }
            }
        }
    };

    private void searchRequest(int page){
        if(TextUtils.isEmpty(keywords))return;
        SearchManager.search(this,typeName,keywords,page,MAX_PER_PAGE,dataPageLoader);
    }

    private void bindData(List<SearchResultV2> data){
        mAdapter.setList(data);
        mAdapter.notifyDataSetChanged();
    }
}
