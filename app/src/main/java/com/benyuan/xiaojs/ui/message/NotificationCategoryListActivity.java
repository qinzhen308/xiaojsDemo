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
import android.widget.AdapterView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.NotificationDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.view.CommonPopupMenu;
import com.benyuan.xiaojs.util.DeviceUtil;
import com.benyuan.xiaojs.util.ToastUtil;
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
            mList.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    int index = position - mList.getRefreshableView().getHeaderViewsCount();
                    showLongClickDialog(view,index);
                    return true;
                }
            });
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

    private void showLongClickDialog(View view, final int position){
        CommonPopupMenu menu = new CommonPopupMenu(this);
        String[] items = new String[]{"删除"};
        menu.addTextItems(items);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        delete(position);
                        break;
                }
            }
        });
        int offset = DeviceUtil.getScreenWidth(this) / 2;
        menu.show(view,offset);
    }

    private void delete(final int position){
        String id = adapter.getItem(position).id;
        NotificationDataManager.requestDelNotification(this, XiaojsConfig.mLoginUser.getSessionID(), id, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                setResult(RESULT_OK);
                adapter.removeItem(position);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(NotificationCategoryListActivity.this,"删除失败！");
            }
        });
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
