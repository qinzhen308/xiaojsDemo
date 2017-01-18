package cn.xiaojs.xma.ui.grade;
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
 * Date:2017/1/8
 * Desc:同学列表
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.DeviceUtil;

public class RoomMatesActivity extends BaseActivity {

    @BindView(R.id.room_mates_grid)
    GridView mGrid;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.room_mates);
        addView(R.layout.activity_room_mates);
        int gridWidth = DeviceUtil.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.px30);
        int numColumns = gridWidth / getResources().getDimensionPixelSize(R.dimen.px130);
        mGrid.setNumColumns(numColumns);
        List<Date> dates = new ArrayList<>();
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());
        PersonalProfileAdapter adapter = new PersonalProfileAdapter(dates, this,false);

        mGrid.setAdapter(adapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.left_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
        }
    }
}
