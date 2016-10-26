/*
 * =======================================================================================
 * Package Name :  com.benyuan.xiaojs.api.HubMsg
 * Source Name   :  HubMsg.java
 * Abstract       :
 *
 * ---------------------------------------------------------------------------------------
 *
 * Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 * This computer program source code file is protected by copyright law and international
 * treaties. Unauthorized distribution of source code files, programs, or portion of the
 * package, may result in severe civil and criminal penalties, and will be prosecuted to
 * the maximum extent under the law.
 *
 * ---------------------------------------------------------------------------------------
 * Revision History:
 * Date          :  Revised on 16-10-26 下午4:20
 * Abstract    :  Initial version by maxiaobao
 *
 * ========================================================================================
 */

package com.benyuan.xiaojs.api;

/**
 * Created by maxiaobao on 2016/10/26.
 */

public class HubMsg {

    private String message;
    private String documentationUrl;

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The documentationUrl
     */
    public String getDocumentationUrl() {
        return documentationUrl;
    }

    /**
     *
     * @param documentationUrl
     * The documentation_url
     */
    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }


}
