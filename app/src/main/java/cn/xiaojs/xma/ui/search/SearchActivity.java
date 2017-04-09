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

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.search.LessonSearch;
import cn.xiaojs.xma.model.search.PersonOriSearch;
import cn.xiaojs.xma.model.search.SearchResponse;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.ToastUtil;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_lesson_list)
    CanInScrollviewListView mLesson;
    @BindView(R.id.search_people_list)
    CanInScrollviewListView mPeople;
    @BindView(R.id.search_ori_list)
    CanInScrollviewListView mOri;
    @BindView(R.id.search_input)
    EditText mInput;

//    @BindView(R.id.search_organization_head)
//    ImageView mOrganizationHead;
//    @BindView(R.id.search_organization_name)
//    TextView mOrganizationName;
//    @BindView(R.id.search_organization_level)
//    TextView mOrganizationLevel;

    @BindView(R.id.search_lesson_list_wrapper)
    View mLessonWrapper;
    @BindView(R.id.search_people_list_wrapper)
    View mPeopleWrapper;
    @BindView(R.id.search_organization_list_wrapper)
    View mOrganizationWrapper;

    @BindView(R.id.search_category)
    View mCategory;
    @BindView(R.id.search_empty)
    View mEmpty;

    @BindView(R.id.search_lesson_more)
    View mLessonMore;
    @BindView(R.id.search_people_more)
    View mPeopleMore;
    @BindView(R.id.search_organization_more)
    View mOrganizationMore;

    private AccountInfo mOrganization;

    private final int MAX_LESSON = 3;
    private final int MAX_PEOPLE = 3;
    private final int MAX_ORGANIZATION = 3;

    private SearchLessonAdapter mLessonAdapter;
    private SearchPeopleAdapter mPeopleAdapter;
    private SearchOrganizationAdapter mOrganizationAdapter;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_global_search);
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
                //search();
                toSearch();
            }
        });
    }

    @OnClick({R.id.search_lesson_more, R.id.search_people_more, R.id.search_organization_more,
            R.id.search_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_ok:
                finish();
                break;
            case R.id.search_lesson_more:
                Intent lesson = new Intent(this,SearchListActivity.class);
                lesson.putExtra(SearchConstant.KEY_SEARCH_TYPE,Social.SearchType.LESSON);
                lesson.putExtra(SearchConstant.KEY_SEARCH_KEY,mInput.getText().toString());
                startActivity(lesson);
                break;
            case R.id.search_people_more:
                Intent people = new Intent(this,SearchListActivity.class);
                people.putExtra(SearchConstant.KEY_SEARCH_TYPE,Social.SearchType.PERSON);
                people.putExtra(SearchConstant.KEY_SEARCH_KEY,mInput.getText().toString());
                startActivity(people);
                break;
            case R.id.search_organization_more:
                Intent organization = new Intent(this,SearchListActivity.class);
                organization.putExtra(SearchConstant.KEY_SEARCH_TYPE,Social.SearchType.ORGANIZATION);
                organization.putExtra(SearchConstant.KEY_SEARCH_KEY,mInput.getText().toString());
                startActivity(organization);
                break;
//            case R.id.search_organization_result:
//                if (mOrganization != null){
//                    //follow(mOrganization._id,null);
//                    if (mOrganization != null){
//                        SearchBusiness.goPersonal(SearchActivity.this,mOrganization);
//                    }
//                }
//                break;
        }
    }

    private void toSearch() {
        String query = mInput.getText().toString();
        if (TextUtils.isEmpty(query)){
            mCategory.setVisibility(View.GONE);
            mEmpty.setVisibility(View.GONE);
            return;
        }

        SearchManager.searchAccountsOrLessons(this,
                query,
                "4",
                new APIServiceCallback<SearchResponse>() {

            @Override
            public void onSuccess(SearchResponse object) {

                updateDisplay(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                updateDisplay(null);
            }

        });
    }


    private void updateDisplay(SearchResponse searchResponse) {
        if (searchResponse == null || searchResponse.total == 0) {
            mLessonWrapper.setVisibility(View.GONE);
            mPeopleWrapper.setVisibility(View.GONE);
            mOrganizationWrapper.setVisibility(View.GONE);
            mCategory.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
            return;
        }
        mCategory.setVisibility(View.VISIBLE);
        mEmpty.setVisibility(View.GONE);


        reset();

        LessonSearch lessonSearch = searchResponse.results.lesson;
        if (lessonSearch !=null && lessonSearch.doc_count > 0) {
            final List<LessonInfo> lessonInfoList = lessonSearch.docs;
            if (lessonInfoList != null && lessonInfoList.size() > 0) {
                mLessonWrapper.setVisibility(View.VISIBLE);
                if (lessonInfoList.size() <= MAX_LESSON) {
                    mLessonMore.setVisibility(View.GONE);
                } else {
                    mLessonMore.setVisibility(View.VISIBLE);
                }
                mLesson.setNeedDivider(true);
                if (mLessonAdapter == null) {
                    mLessonAdapter = new SearchLessonAdapter(this, lessonInfoList, MAX_LESSON);
                    mLesson.setAdapter(mLessonAdapter);
                } else {
                    mLessonAdapter.setData(lessonInfoList);
                    mLessonAdapter.notifyDataSetChanged();
                }

                mLesson.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        LessonInfo lessonInfo = lessonInfoList.get(position);

                        if (lessonInfo != null) {
                            SearchBusiness.goLessonHome(SearchActivity.this, lessonInfo);
                        }

                    }
                });

            } else {
                mLessonWrapper.setVisibility(View.GONE);
            }
        }

        PersonOriSearch personSearch = searchResponse.results.person;
        if (personSearch !=null && personSearch.doc_count > 0) {

            final List<AccountInfo> personInfoList = personSearch.docs;
            if (personInfoList != null && personInfoList.size() > 0) {
                mPeopleWrapper.setVisibility(View.VISIBLE);
                if (personInfoList.size() <= MAX_PEOPLE) {
                    mPeopleMore.setVisibility(View.GONE);
                } else {
                    mPeopleMore.setVisibility(View.VISIBLE);
                }
                mPeople.setNeedDivider(true);
                if (mPeopleAdapter == null) {
                    mPeopleAdapter = new SearchPeopleAdapter(this, personInfoList, MAX_PEOPLE);
                    mPeople.setAdapter(mPeopleAdapter);
                } else {
                    mPeopleAdapter.setData(personInfoList);
                    mPeopleAdapter.notifyDataSetChanged();
                }
                mPeople.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        AccountInfo search = personInfoList.get(position);
                        if (search != null) {
                            SearchBusiness.goPersonal(SearchActivity.this, search);
                        }
                        //follow(search._id,search._source.basic.getName());
                    }
                });
            } else {
                mPeopleWrapper.setVisibility(View.GONE);
            }
        }

        PersonOriSearch oriSearch = searchResponse.results.organization;
        if (oriSearch !=null && oriSearch.doc_count > 0) {
            final List<AccountInfo> oriInfoList = oriSearch.docs;
            if (oriInfoList != null && oriInfoList.size() > 0) {
                mOrganizationWrapper.setVisibility(View.VISIBLE);
//                if (oriInfoList.size() <= MAX_ORGANIZATION) {
//                    mOrganizationMore.setVisibility(View.GONE);
//                } else {
//                    mOrganizationMore.setVisibility(View.VISIBLE);
//                }
//                mOrganization = oriInfoList.get(0);
//                mOrganizationName.setText(mOrganization.basic.getName());

                if (oriInfoList.size() <= MAX_ORGANIZATION) {
                    mOrganizationMore.setVisibility(View.GONE);
                } else {
                    mOrganizationMore.setVisibility(View.VISIBLE);
                }
                mOri.setNeedDivider(true);
                if (mOrganizationAdapter == null) {
                    mOrganizationAdapter = new SearchOrganizationAdapter(this, oriInfoList, MAX_PEOPLE);
                    mOri.setAdapter(mOrganizationAdapter);
                } else {
                    mOrganizationAdapter.setData(oriInfoList);
                    mOrganizationAdapter.notifyDataSetChanged();
                }
                mOri.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        AccountInfo search = oriInfoList.get(position);
                        if (search != null) {
                            SearchBusiness.goPersonal(SearchActivity.this, search);
                        }
                        //follow(search._id,search._source.basic.getName());
                    }
                });

            } else {
                mOrganizationWrapper.setVisibility(View.GONE);
            }
        }

    }

    private void reset() {
        mLessonWrapper.setVisibility(View.GONE);
        mOrganizationWrapper.setVisibility(View.GONE);
        mPeopleWrapper.setVisibility(View.GONE);
    }


//////////////////////////////////old search////////////////////////////////////////////////////////

//    private void search() {
//        String query = mInput.getText().toString();
//        if (TextUtils.isEmpty(query)){
//            mCategory.setVisibility(View.GONE);
//            mEmpty.setVisibility(View.GONE);
//            return;
//        }
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
//    private void updateDisplay(ArrayList<AccountSearch> result) {
//        if (result == null || result.size() == 0){
//            mLessonWrapper.setVisibility(View.GONE);
//            mPeopleWrapper.setVisibility(View.GONE);
//            mOrganizationWrapper.setVisibility(View.GONE);
//            mCategory.setVisibility(View.GONE);
//            mEmpty.setVisibility(View.VISIBLE);
//            return;
//        }
//        mCategory.setVisibility(View.VISIBLE);
//        mEmpty.setVisibility(View.GONE);
//        List<AccountSearch> lessons = SearchBusiness.getSearchResultByType(result, Social.SearchType.LESSON);
//        final List<AccountSearch> people = SearchBusiness.getSearchResultByType(result, Social.SearchType.PERSON);
//        List<AccountSearch> organization = SearchBusiness.getSearchResultByType(result, Social.SearchType.ORGANIZATION);
//
//        if (lessons != null && lessons.size() > 0) {
//            mLessonWrapper.setVisibility(View.VISIBLE);
//            if (lessons.size() <= MAX_LESSON){
//                mLessonMore.setVisibility(View.GONE);
//            }else {
//                mLessonMore.setVisibility(View.VISIBLE);
//            }
//            mLesson.setNeedDivider(true);
//            if (mLessonAdapter == null){
//                mLessonAdapter = new SearchLessonAdapter(this, lessons, MAX_LESSON);
//                mLesson.setAdapter(mLessonAdapter);
//            }else {
//                mLessonAdapter.setData(lessons);
//                mLessonAdapter.notifyDataSetChanged();
//            }
//        } else {
//            mLessonWrapper.setVisibility(View.GONE);
//        }
//
//        if (people != null && people.size() > 0) {
//            mPeopleWrapper.setVisibility(View.VISIBLE);
//            if (people.size() <= MAX_PEOPLE){
//                mPeopleMore.setVisibility(View.GONE);
//            }else {
//                mPeopleMore.setVisibility(View.VISIBLE);
//            }
//            mPeople.setNeedDivider(true);
//            if (mPeopleAdapter == null){
//                mPeopleAdapter = new SearchPeopleAdapter(this, people,MAX_PEOPLE);
//                mPeople.setAdapter(mPeopleAdapter);
//            }else {
//                mPeopleAdapter.setData(people);
//                mPeopleAdapter.notifyDataSetChanged();
//            }
//            mPeople.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    AccountSearch search = people.get(position);
//                    if (search != null){
//                        SearchBusiness.goPersonal(SearchActivity.this,search);
//                    }
//                    //follow(search._id,search._source.basic.getName());
//                }
//            });
//        } else {
//            mPeopleWrapper.setVisibility(View.GONE);
//        }
//
//        if (organization != null && organization.size() > 0) {
//            mOrganizationWrapper.setVisibility(View.VISIBLE);
//            if (organization.size() <= MAX_ORGANIZATION){
//                mOrganizationMore.setVisibility(View.GONE);
//            }else {
//                mOrganizationMore.setVisibility(View.VISIBLE);
//            }
//            mOrganization = organization.get(0);
//            mOrganizationName.setText(mOrganization._source.basic.getName());
//        } else {
//            mOrganizationWrapper.setVisibility(View.GONE);
//        }
//    }
//
//    private void follow(final String id, final String alias){
//        SocialManager.followContact(SearchActivity.this, id, Social.ContactGroup.FRIENDS, new APIServiceCallback<Relation>() {
//            @Override
//            public void onSuccess(Relation object) {
//                ToastUtil.showToast(SearchActivity.this,R.string.followed);
//                if (TextUtils.isEmpty(alias)){
//                    return;
//                }
//
//                Contact contact = new Contact();
//                contact.alias = alias;
//                contact.account = id;
//
//                DataManager.addContact(SearchActivity.this,Social.ContactGroup.FRIENDS,contact);
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                ToastUtil.showToast(SearchActivity.this,errorMessage);
//            }
//        });
//    }
}
