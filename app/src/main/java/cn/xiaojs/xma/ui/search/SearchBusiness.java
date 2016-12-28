package cn.xiaojs.xma.ui.search;
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
 * Date:2016/12/28
 * Desc:
 *
 * ======================================================================================== */

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.search.AccountSearch;

public class SearchBusiness {

    public static List<AccountSearch> getSearchResultByType(List<AccountSearch> origin,String type){
        List<AccountSearch> result = new ArrayList<>();
        for (AccountSearch search : origin){
            if (search._source.typeName.equalsIgnoreCase(type)){
                result.add(search);
            }
        }
        return result;
    }
}
