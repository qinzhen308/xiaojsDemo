package cn.xiaojs.xma.ui.personal;


import android.os.Bundle;

import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;

/**
 * Created by Paul Z on 2017/7/23.
 */

public class PersonHomeClassFragment extends BaseScrollTabFragment {

    private PersonHomeClassAdapter mAdapter;

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        String account = bundle.getString(PersonalBusiness.KEY_PERSONAL_ACCOUNT_ID, "");
        mAdapter = new PersonHomeClassAdapter(getContext(), mList, account);
        mList.getRefreshableView().setDivider(null);
        mList.setAdapter(mAdapter);
    }


    public static PersonHomeClassFragment newInstance(int position, String account){
        PersonHomeClassFragment fragment=new PersonHomeClassFragment();
        Bundle b1 = new Bundle();
        b1.putString(PersonalBusiness.KEY_PERSONAL_ACCOUNT_ID, account);
        fragment.setArguments(b1);
        fragment.setPagePosition(position);
        return fragment;
    }
}
