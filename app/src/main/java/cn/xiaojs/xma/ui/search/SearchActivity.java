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
 * ======================================================================================== */

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.ToastUtil;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_lesson_list)
    CanInScrollviewListView mLesson;
    @BindView(R.id.search_people_list)
    CanInScrollviewListView mPeople;
    @BindView(R.id.search_input)
    EditText mInput;

    @BindView(R.id.search_organization_head)
    RoundedImageView mOrganizationHead;
    @BindView(R.id.search_organization_name)
    TextView mOrganizationName;
    @BindView(R.id.search_organization_level)
    TextView mOrganizationLevel;

    @BindView(R.id.search_lesson_list_wrapper)
    View mLessonWrapper;
    @BindView(R.id.search_people_list_wrapper)
    View mPeopleWrapper;
    @BindView(R.id.search_organization_list_wrapper)
    View mOrganizationWrapper;

    private final int MAX_LESSON = 3;
    private final int MAX_PEOPLE = 3;
    private final int MAX_ORGANIZATION = 1;

    private SearchLessonAdapter mLessonAdapter;
    private SearchPeopleAdapter mPeopleAdapter;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_global_search);
        needHeader(false);
//        CanInScrollviewListView.Adapter adapter = new SearchLessonAdapter(this,null);
//        mLesson.setNeedDivider(true);
//        mLesson.setAdapter(adapter);
//
//        CanInScrollviewListView.Adapter adapter1 = new SearchPeopleAdapter(this,null);
//        //mLesson.setDividerColor(R.color.main_bg);
//        mPeople.setNeedDivider(true);
//        mPeople.setAdapter(adapter1);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });
    }

    @OnClick({R.id.search_lesson_more, R.id.search_people_more, R.id.search_organization_more,
            R.id.search_organization_result, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.search_ok:
                search();
                break;
            case R.id.search_lesson_more:
                break;
            case R.id.search_people_more:
                break;
            case R.id.search_organization_more:
                break;
            case R.id.search_organization_result:
                break;
        }
    }

    private void search() {
        String query = mInput.getText().toString();
        if (TextUtils.isEmpty(query))
            return;
        SearchManager.searchAccounts(this, query, new APIServiceCallback<ArrayList<AccountSearch>>() {
            @Override
            public void onSuccess(ArrayList<AccountSearch> object) {
                updateDisplay(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void updateDisplay(ArrayList<AccountSearch> result) {
        if (result == null || result.size() == 0){
            mLessonWrapper.setVisibility(View.GONE);
            mPeopleWrapper.setVisibility(View.GONE);
            mOrganizationWrapper.setVisibility(View.GONE);
            return;
        }
        List<AccountSearch> lessons = SearchBusiness.getSearchResultByType(result, "Lesson");
        final List<AccountSearch> people = SearchBusiness.getSearchResultByType(result, "Person");
        List<AccountSearch> organization = SearchBusiness.getSearchResultByType(result, "Organization");

        if (lessons != null && lessons.size() > 0) {
            mLessonWrapper.setVisibility(View.VISIBLE);
            mLesson.setNeedDivider(true);
            if (mLessonAdapter == null){
                mLessonAdapter = new SearchLessonAdapter(this, lessons, MAX_LESSON);
                mLesson.setAdapter(mLessonAdapter);
            }else {
                mLessonAdapter.setData(lessons);
                mLessonAdapter.notifyDataSetChanged();
            }
        } else {
            mLessonWrapper.setVisibility(View.GONE);
        }

        if (people != null && people.size() > 0) {
            mPeopleWrapper.setVisibility(View.VISIBLE);
            mPeople.setNeedDivider(true);
            if (mPeopleAdapter == null){
                mPeopleAdapter = new SearchPeopleAdapter(this, people,MAX_PEOPLE);
                mPeople.setAdapter(mPeopleAdapter);
            }else {
                mPeopleAdapter.setData(people);
                mPeopleAdapter.notifyDataSetChanged();
            }
            mPeople.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    AccountSearch search = people.get(position);
                    SocialManager.followContact(SearchActivity.this, search._id, Social.ContactGroup.FRIENDS, new APIServiceCallback<Relation>() {
                        @Override
                        public void onSuccess(Relation object) {
                            ToastUtil.showToast(SearchActivity.this,R.string.followed);
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            ToastUtil.showToast(SearchActivity.this,errorMessage);
                        }
                    });
                }
            });
        } else {
            mPeopleWrapper.setVisibility(View.GONE);
        }

        if (organization != null && organization.size() > 0) {
            mOrganizationWrapper.setVisibility(View.VISIBLE);
            AccountSearch search = organization.get(0);
            mOrganizationName.setText(search._source.basic.getName());
        } else {
            mOrganizationWrapper.setVisibility(View.GONE);
        }
    }
}
