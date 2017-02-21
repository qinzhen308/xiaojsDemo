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

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;

public class SearchBusiness {

    public static List<AccountSearch> getSearchResultByType(List<AccountSearch> origin, String type) {
        List<AccountSearch> result = new ArrayList<>();
        for (AccountSearch search : origin) {
            if (search._source.typeName.equalsIgnoreCase(type)) {
                result.add(search);
            }
        }
        return result;
    }

    public static void goPersonal(Context context, AccountSearch search) {
        Intent intent = new Intent(context, PersonHomeActivity.class);
        intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, search._id);
        context.startActivity(intent);
    }
}
