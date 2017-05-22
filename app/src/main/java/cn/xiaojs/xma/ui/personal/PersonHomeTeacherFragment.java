package cn.xiaojs.xma.ui.personal;


import android.os.Bundle;

import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;

/**
 * Created by Paul Z on 2017/05/22.
 */
public class PersonHomeTeacherFragment extends BaseScrollTabFragment {

    private PersonHomeOrgTeacherAdapter mAdapter;

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        String account = bundle.getString(PersonalBusiness.KEY_PERSONAL_ACCOUNT_ID, "");
        mAdapter = new PersonHomeOrgTeacherAdapter(getContext(), mList, account);
        mList.setAdapter(mAdapter);
    }


    public static PersonHomeTeacherFragment createInstance(String accountId){
        PersonHomeTeacherFragment fragment=new PersonHomeTeacherFragment();
        Bundle bundle=new Bundle();
        bundle.putString(PersonalBusiness.KEY_PERSONAL_ACCOUNT_ID,accountId);
        fragment.setArguments(bundle);
        return fragment;
    }
}
