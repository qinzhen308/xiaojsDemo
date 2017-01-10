package cn.xiaojs.xma.ui.classroom.socketio;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/1/9
 * Desc:
 *
 * ======================================================================================== */

public class Commend {
    public String src;
    public String cm;
    public String id;
    public String params;

    @Override
    public String toString() {
        return "==========src=" + src + " cm=" + cm + " id=" + id + " params=" + params;
    }
}
