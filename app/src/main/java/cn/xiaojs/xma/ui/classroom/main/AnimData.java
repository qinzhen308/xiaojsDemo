package cn.xiaojs.xma.ui.classroom.main;
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
 * Date:2017/5/11
 * Desc:
 *
 * ======================================================================================== */

public class AnimData {

    public AnimData() {

    }

    public AnimData(float alpha) {
        this.alpha = alpha;
    }

    public AnimData(float alpha, float translateX, float translateY, float scaleX, float scaleY) {
        this.alpha = alpha;
        this.translateX = translateX;
        this.translateY = translateY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public float alpha;
    public float translateX;
    public float translateY;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
}
