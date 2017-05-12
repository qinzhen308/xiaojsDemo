package cn.xiaojs.xma.ui.lesson;
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.search.LessonSearch;
import cn.xiaojs.xma.model.search.PersonOriSearch;
import cn.xiaojs.xma.model.search.SearchResponse;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.search.SearchBusiness;
import cn.xiaojs.xma.ui.search.SearchConstant;
import cn.xiaojs.xma.ui.search.SearchLessonAdapter;
import cn.xiaojs.xma.ui.search.SearchListActivity;
import cn.xiaojs.xma.ui.search.SearchOrganizationAdapter;
import cn.xiaojs.xma.ui.search.SearchPeopleAdapter;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;

public class TeachingSubjectSearchActivity extends BaseActivity {

    @BindView(R.id.search_input)
    EditText mInput;

    @BindView(R.id.listview)
    ListView mListView;
    TeachingSubjectSearchAdapter mAdapter;



    @Override
    protected void addViewContent() {
        addView(R.layout.activity_teaching_subject_search);
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
        initView();
    }

    private void initView(){
        mAdapter=new TeachingSubjectSearchAdapter(this);
        mListView.setAdapter(mAdapter);
    }



    @OnClick({R.id.search_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_ok:
                finish();
                break;

        }
    }

    private void toSearch() {
        String query = mInput.getText().toString();
        List<Object> list=new ArrayList<>();
        if(!TextUtils.isEmpty(query)){
            list.add(query);
        }
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
    }


    private void updateDisplay() {



    }


}
