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
 * Date:2016/12/12
 * Desc:搜索个人、机构、课
 *
 * ======================================================================================== */

import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_lesson_list)
    CanInScrollviewListView mLesson;
    @BindView(R.id.search_people_list)
    CanInScrollviewListView mPeople;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_global_search);
        needHeader(false);
        CanInScrollviewListView.Adapter adapter = new SearchLessonAdapter(this,null);
        //mLesson.setDividerColor(R.color.main_bg);
        mLesson.setNeedDivider(true);
        mLesson.setAdapter(adapter);

        CanInScrollviewListView.Adapter adapter1 = new SearchPeopleAdapter(this,null);
        //mLesson.setDividerColor(R.color.main_bg);
        mPeople.setNeedDivider(true);
        mPeople.setAdapter(adapter1);

//        tt.setText("这个是中文");
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_msg_bg);
//        tt.setIcon(BitmapUtils.getDrawableWithText(this,bitmap.copy(Bitmap.Config.ARGB_8888,true),"22"));
//        mExpand.show(28);
//        Bitmap b1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b2 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b3 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b4 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b5 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b6 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b7 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b8 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b9= BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//        Bitmap b10 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
//
//        List<Bitmap> list = new ArrayList<>();
//        list.add(b1);
//        list.add(b2);
//        list.add(b3);
//        list.add(b4);
//        list.add(b5);
//        list.add(b6);
//        list.add(b7);
//        list.add(b8);
////        list.add(b9);
////        list.add(b10);
////        list.add(b10);
////        list.add(b10);
////        list.add(b10);
////        list.add(b10);
////        list.add(b10);
////        list.add(b10);
////        list.add(b10);
//
//        flow.show(list);
//
//        Spannable spannable = StringUtil.getSpecialString("显示一种颜色一种","一种",getResources().getColor(R.color.font_blue));
//        tx.setText(spannable);
    }

    @OnClick({R.id.search_lesson_more,R.id.search_people_more,R.id.search_organization_more,
                R.id.search_organization_result})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.search_lesson_more:
                break;
            case R.id.search_people_more:
                break;
            case R.id.search_organization_more:
                break;
            case R.id.search_organization_result:
                break;
        }
    }
}
