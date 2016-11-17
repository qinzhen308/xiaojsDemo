package com.benyuan.xiaojs.ui.message;
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
 * Date:2016/11/16
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

public class NotificationCategoryListActivity extends BaseActivity {

    @BindView(R.id.message_list)
    AutoPullToRefreshListView mList;


    private String categoryId;

    private NotificationCategoryAdapter adapter;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_message_list);
        Intent intent = getIntent();
        if (intent != null){
            categoryId = intent.getStringExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID);
            String title = intent.getStringExtra(NotificationConstant.KEY_NOTIFICATION_TITLE);
            setMiddleTitle(title);
        }

        if (!TextUtils.isEmpty(categoryId)){
            adapter = new NotificationCategoryAdapter(this,mList,categoryId);
            mList.setAdapter(adapter);
//            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (adapter != null){
//                        Notification notification = adapter.getItem(position);
//                        if (notification != null){
//                            notification.read = true;
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            });
        }
    }

    @OnClick({R.id.left_image})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
        }
    }
}
