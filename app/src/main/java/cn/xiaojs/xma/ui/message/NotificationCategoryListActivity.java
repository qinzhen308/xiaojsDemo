package cn.xiaojs.xma.ui.message;
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

import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.NotificationDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.IgnoreNResponse;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCriteria;
import cn.xiaojs.xma.ui.base.BaseListActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.ToastUtil;

import java.util.Date;
import java.util.List;

public class NotificationCategoryListActivity extends BaseListActivity {
    private String categoryId;

    private NotificationCategoryAdapter adapter;

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getStringExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID);
            String title = intent.getStringExtra(NotificationConstant.KEY_NOTIFICATION_TITLE);
            setMiddleTitle(title);
        }

        if (!TextUtils.isEmpty(categoryId)) {
            adapter = new NotificationCategoryAdapter(this, mList);
            NotificationCriteria criteria = new NotificationCriteria();
            criteria.before = new Date(System.currentTimeMillis());
            criteria.state = Platform.NotificationState.NONE;
            criteria.category = categoryId;
            adapter.setCriteria(criteria);
            mList.setAdapter(adapter);
        }
    }

    private void showLongClickDialog(View view, final int position) {
        CommonPopupMenu menu = new CommonPopupMenu(this);
        String[] items = new String[]{"删除"};
        menu.addTextItems(items);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        delete(position);
                        break;
                }
            }
        });
        int offset = DeviceUtil.getScreenWidth(this) / 2;
        menu.show(view, offset);
    }

    private void delete(final int position) {
        String id = adapter.getItem(position).id;
        NotificationDataManager.requestDelNotification(this, id, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                setResult(RESULT_OK);
                adapter.removeItem(position);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(NotificationCategoryListActivity.this, errorMessage);
            }
        });
    }

    private void ignoreNotifications() {
        if (adapter != null) {
            List<Notification> notifications = adapter.getList();
            if (notifications != null && notifications.size() > 0) {

                NotificationCriteria criteria = new NotificationCriteria();
                Duration duration = new Duration();
                duration.setStart(notifications.get(notifications.size() - 1).createdOn);
                duration.setEnd(notifications.get(0).createdOn);
                criteria.category = categoryId;
                criteria.duration = duration;
                NotificationDataManager.ignoreNotifications(this, criteria, new APIServiceCallback<IgnoreNResponse>() {
                    @Override
                    public void onSuccess(IgnoreNResponse object) {

                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                    }
                });
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        ignoreNotifications();
    }
}
