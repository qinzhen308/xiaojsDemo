package com.benyuan.xiaojs.ui;

import android.support.v4.app.Fragment;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseTabActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends BaseTabActivity {
    
    @Override
    protected void initView() {
        setMiddleTitle(R.string.app_name);
        List<Fragment> fs = new ArrayList<>();
        fs.add(new HomeFragment());
        fs.add(new LiveFragment());
        fs.add(new LearnFragment());
        fs.add(new MineFragment());
        setButtonType(BUTTON_TYPE_CENTER);
        addViews(new int[]{R.string.home_tab_index, R.string.home_tab_live, R.string.home_tab_learn, R.string.home_tab_mine},
                new int[]{R.drawable.home_tab_selector, R.drawable.home_tab_selector, R.drawable.home_tab_selector, R.drawable.home_tab_selector},
                fs);
        new OkHttpClient();
    }

}
