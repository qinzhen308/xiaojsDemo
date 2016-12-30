package cn.xiaojs.xma.model;
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
 * Date:2016/12/29
 * Desc:
 *
 * ======================================================================================== */

import java.io.Serializable;

import cn.xiaojs.xma.model.social.Dynamic;

public class DynamicStatus implements Serializable{
    public String id;
    public boolean liked;
    public Dynamic.DynStatus status;
}
