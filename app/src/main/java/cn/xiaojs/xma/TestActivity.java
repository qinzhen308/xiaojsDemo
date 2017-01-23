package cn.xiaojs.xma;
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
 * Date:2017/1/18
 * Desc:
 *
 * ======================================================================================== */

import butterknife.BindView;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.PortraitView;

public class TestActivity extends BaseActivity {

//    @BindView(R.id.cut1)
//    CutView board;
//    @BindView(R.id.cut2)
//    CutView player;
    public static String pathLocal = "rtmp://demo.srs.tongchia.me:1935/live/livestream";

    @BindView(R.id.portrait)
    PortraitView por;

    @Override
    protected void addViewContent() {
        addView(R.layout.tt);
        needHeader(false);
        por.setSex("true");
//        board.setPath(pathLocal, true);
//        player.setPath(pathLocal, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        board.resume();
//        player.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        board.pause();
//        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        board.destroy();
//        player.destroy();
    }
}
