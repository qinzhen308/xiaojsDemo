package cn.xiaojs.xma.ui;
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
 * Date:2016/12/12
 * Desc:搜索个人、机构、课
 *
 * ======================================================================================== */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.ImageMatrixExpandableLayout;
import cn.xiaojs.xma.ui.widget.flow.ImageFlowLayout;
import cn.xiaojs.xma.util.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GlobalSearchActivity extends BaseActivity {

    @BindView(R.id.tt)
    IconTextView tt;
    @BindView(R.id.image_expand)
    ImageMatrixExpandableLayout mExpand;
    @BindView(R.id.flow)
    ImageFlowLayout flow;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_global_search);
        tt.setText("这个是中文");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_msg_bg);
        tt.setIcon(BitmapUtils.getDrawableWithText(this,bitmap.copy(Bitmap.Config.ARGB_8888,true),"22"));
        mExpand.show(28);
        Bitmap b1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b2 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b3 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b4 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b5 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b6 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b7 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b8 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b9= BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b10 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);

        List<Bitmap> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        list.add(b3);
        list.add(b4);
        list.add(b5);
        list.add(b6);
        list.add(b7);
        list.add(b8);
//        list.add(b9);
//        list.add(b10);
//        list.add(b10);
//        list.add(b10);
//        list.add(b10);
//        list.add(b10);
//        list.add(b10);
//        list.add(b10);
//        list.add(b10);

        flow.show(list);
    }
}
