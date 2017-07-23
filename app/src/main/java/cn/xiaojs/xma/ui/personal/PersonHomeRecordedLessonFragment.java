package cn.xiaojs.xma.ui.personal;


import android.os.Bundle;

import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;

/**
 * Created by Paul Z on 2017/7/23.
 */

public class PersonHomeRecordedLessonFragment extends BaseScrollTabFragment {

    private PersonHomeRecordedLessonAdapter mAdapter;

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        String account = bundle.getString(PersonalBusiness.KEY_PERSONAL_ACCOUNT_ID, "");
        mAdapter = new PersonHomeRecordedLessonAdapter(getContext(), mList, account);
        mList.setDividerDrawable(null);
        mList.setAdapter(mAdapter);
    }


    public static PersonHomeRecordedLessonFragment newInstance(int position,String account){
        PersonHomeRecordedLessonFragment fragment=new PersonHomeRecordedLessonFragment();
        Bundle b1 = new Bundle();
        b1.putString(PersonalBusiness.KEY_PERSONAL_ACCOUNT_ID, account);
        fragment.setArguments(b1);
        fragment.setPagePosition(position);
        return fragment;
    }
}
