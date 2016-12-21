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
 * Date:2016/11/17
 * Desc:
 *
 * ======================================================================================== */

import android.text.TextUtils;

import cn.xiaojs.xma.common.xf_foundation.platform.NotificationTemplate;
import cn.xiaojs.xma.model.NotificationCategory;

import java.util.ArrayList;
import java.util.List;

public class NotificationBusiness {

    public static List<NotificationCategory> getPlatformMessageCategory(List<NotificationCategory> notifications){
        List<NotificationCategory> result = new ArrayList<>();
        for (NotificationCategory notification : notifications){
            if (NotificationTemplate.ANSWERS_NOTIFICATION.equalsIgnoreCase(notification.name)
                    ||NotificationTemplate.PLATFORM_NOTIFICATION.equalsIgnoreCase(notification.name)
                    ||NotificationTemplate.FOLLOW_NOTIFICATION.equalsIgnoreCase(notification.name)
                    ||NotificationTemplate.INVITATION_NOTIFICATION.equalsIgnoreCase(notification.name)
                    ||NotificationTemplate.CTL_NOTIFICATION.equalsIgnoreCase(notification.name)
                    ||NotificationTemplate.FINANCE_NOTIFICATION.equalsIgnoreCase(notification.name)
                    ||NotificationTemplate.ARTICLE_NOTIFICATION.equalsIgnoreCase(notification.name)){
                result.add(notification);
            }
        }

        return result;
    }

    public static NotificationCategory getPlatformMessageCategory(List<NotificationCategory> notifications,int position){
        if (notifications == null)
            return null;

        String category = getMessageCategory(position);
        if (!TextUtils.isEmpty(category)){
            for (NotificationCategory notificationCategory : notifications){
                if (category.equalsIgnoreCase(notificationCategory.name)){
                    return notificationCategory;
                }
            }
        }

        return null;
    }

    public static String getMessageCategory(int position){
        switch (position){
            case 0:
                return NotificationTemplate.INVITATION_NOTIFICATION;
            case 1:
                return NotificationTemplate.CTL_NOTIFICATION;
            case 2:
                return NotificationTemplate.FOLLOW_NOTIFICATION;
            case 3:
                return NotificationTemplate.ANSWERS_NOTIFICATION;
            case 4:
                return NotificationTemplate.FINANCE_NOTIFICATION;
            case 5:
                return NotificationTemplate.ARTICLE_NOTIFICATION;
            case 6:
                return NotificationTemplate.PLATFORM_NOTIFICATION;

        }
        return null;
    }
}
