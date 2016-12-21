package cn.xiaojs.xma.util;
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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberUtil {
    /**
     * double转换成string 可以防止double显示成4.99958333E7
     *
     * @param value     待转换值
     * @param precision 需要保留的位数
     * @return
     */
    public static String doubleToString(double value, int precision) {
        return doubleToString(value, precision, true);
    }

    /**
     * double转换成string 可以防止double显示成4.99958333E7 不四舍五入 不四舍五入
     *
     * @param value     待转换值
     * @param precision 需要保留的位数
     * @param isUpDown  是否需要四舍五入 false 直接截取
     * @return
     */
    public static String doubleToString(double value, int precision,
                                        boolean isUpDown) {
        NumberFormat df = NumberFormat.getInstance();
        df.setMaximumFractionDigits(precision);
        df.setMinimumFractionDigits(precision);
        if (!isUpDown) {
            df.setRoundingMode(RoundingMode.DOWN);
        }
        return df.format(value).replace(",", "");
    }

    public static String getPrice(double money){
        return '￥' + doubleToString(money,2);
    }
}
