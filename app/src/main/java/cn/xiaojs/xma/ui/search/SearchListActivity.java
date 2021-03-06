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
 * Date:2016/12/30
 * Desc:
 *
 * ======================================================================================== */

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class SearchListActivity extends BaseActivity {

    @BindView(R.id.search_list)
    PullToRefreshSwipeListView mList;

    @BindView(R.id.search_input)
    EditText inputView;
    @BindView(R.id.search_type)
    TextView typeView;

    private String keyWord;
    private SearchAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_search_list);

        keyWord = getIntent().getStringExtra(SearchConstant.KEY_SEARCH_KEY);
        inputView.setText(keyWord);
        inputView.setSelection(keyWord.length());

        typeView.setVisibility(View.GONE);

        needHeader(false);
        needHeaderDivider(false);

        String searchType = getIntent().getStringExtra(SearchConstant.KEY_SEARCH_TYPE);
        if (!TextUtils.isEmpty(searchType)) {
            if (searchType.equalsIgnoreCase(Social.SearchType.LESSON)) {
                adapter = new LessonInfoAdapter(this, mList);
            } else if (searchType.equalsIgnoreCase(Social.SearchType.PERSON)) {
                adapter = new PersonOriAdapter(this, mList);
            } else if (searchType.equalsIgnoreCase(Social.SearchType.ORGANIZATION)) {
                adapter = new OrganizationAdapter(this, mList);
            }
        } else {
            adapter = new LessonInfoAdapter(this, mList);
        }

        adapter.setKeyWord(keyWord);
        mList.setAdapter(adapter);


        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                requery();
            }
        });

    }

    @OnClick({R.id.search_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_ok:
                finish();
                break;
        }
    }

    private void requery() {
        if (adapter != null) {
            String query = inputView.getText().toString();
            adapter.setKeyWord(query);

            if (TextUtils.isEmpty(query)) {
                adapter.onSuccess(null);
                return;
            }
            adapter.requery();
        }
    }


    //    @BindView(R.id.search_list)
//    CanInScrollviewListView mList;
//    @BindView(R.id.search_input)
//    EditText mInput;
//    @BindView(R.id.search_type)
//    TextView mType;
//
//    private String mTypeName;
//    private String mKeyWord;
//
//    private SearchPeopleAdapter mPeopleAdapter;
//    private SearchOrganizationAdapter mOrganizationAdapter;

//    @Override
//    protected void addViewContent() {
//        addView(R.layout.activity_search_list);
//        needHeader(false);
//        Intent intent = getIntent();
//        if (intent != null){
//            mTypeName = intent.getStringExtra(SearchConstant.KEY_SEARCH_TYPE);
//            if (!TextUtils.isEmpty(mTypeName)){
//                mKeyWord = intent.getStringExtra(SearchConstant.KEY_SEARCH_KEY);
//                if (mTypeName.equalsIgnoreCase(Social.SearchType.LESSON)){
//                    mType.setText(R.string.lesson);
//                }else if (mTypeName.equalsIgnoreCase(Social.SearchType.PERSON)){
//                    mType.setText(R.string.personal);
//                }else if (mTypeName.equalsIgnoreCase(Social.SearchType.ORGANIZATION)){
//                    mType.setText(R.string.organization);
//                }
//            }
//            mInput.setText(mKeyWord);
//            search();
//            mInput.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    search();
//                }
//            });
//        }
//    }
//
//    private void search() {
//        String query = mInput.getText().toString();
//        if (TextUtils.isEmpty(query)){
//            mList.setVisibility(View.GONE);
//            return;
//        }
//        mList.setVisibility(View.VISIBLE);
//        SearchManager.searchAccounts(this, query, new APIServiceCallback<ArrayList<AccountSearch>>() {
//            @Override
//            public void onSuccess(ArrayList<AccountSearch> object) {
//                updateDisplay(object);
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                updateDisplay( null);
//            }
//        });
//    }
//
//    private void updateDisplay(List<AccountSearch> searches){
//        if (searches == null || searches.size() <= 0){
//            mList.setVisibility(View.GONE);
//            return;
//        }
//        final List<AccountSearch> result = SearchBusiness.getSearchResultByType(searches,mTypeName);
//        if (Social.SearchType.PERSON.equalsIgnoreCase(mTypeName)){
//            mList.setNeedDivider(true);
//            if (mPeopleAdapter == null){
//                mPeopleAdapter = new SearchPeopleAdapter(this, result);
//                mList.setAdapter(mPeopleAdapter);
//            }else {
//                mPeopleAdapter.setData(result);
//                mPeopleAdapter.notifyDataSetChanged();
//            }
//        }else if (Social.SearchType.ORGANIZATION.equalsIgnoreCase(mTypeName)){
//            mList.setNeedDivider(true);
//            if (mOrganizationAdapter == null){
//                mOrganizationAdapter = new SearchOrganizationAdapter(this,result);
//                mList.setAdapter(mOrganizationAdapter);
//            }else {
//                mOrganizationAdapter.setData(result);
//                mOrganizationAdapter.notifyDataSetChanged();
//            }
//        }
//        mList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                AccountSearch search = result.get(position);
//                if (search == null)
//                    return;
//                SearchBusiness.goPersonal(SearchListActivity.this,search);
//            }
//        });
//    }
//
//    @OnClick({R.id.search_ok})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.search_ok:
//                finish();
//                break;
//        }
//    }
}
