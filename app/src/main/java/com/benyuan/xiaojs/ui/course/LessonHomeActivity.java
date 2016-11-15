package com.benyuan.xiaojs.ui.course;


import com.google.android.flexbox.FlexboxLayout;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.BlockTabView;
import com.benyuan.xiaojs.ui.widget.BottomSheet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class LessonHomeActivity extends BaseActivity {


    @BindView(R.id.block_detail_bar)
    BlockTabView blockTabView;

    @BindView(R.id.lession_old_money)
    TextView olgMoneyTextView;


    private ArrayList<TextView> textViews;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_lession_detail);
        setMiddleTitle(R.string.lession_detail);
        setLeftImage(R.drawable.back_arrow);

        initTabBar();

        olgMoneyTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);


    }

    @OnClick({R.id.left_image, R.id.apply_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.apply_btn:
                showApplyDlg();
                break;

        }
    }


    private void initTabBar() {

        String[] titles =getResources().getStringArray(R.array.lesson_home_tab_titles);

        initShowTextView();

        blockTabView.setViews("",titles,textViews,"");
    }

    private void initShowTextView() {


        textViews = new ArrayList<>(2);




        TextView textView1 = createTextView();
        textView1.setText("大时代雷锋萨达六块腹肌阿斯利康的风景阿" +
                "斯顿发克里斯朵夫及阿莱克但是公司的分公司地方公司的分公司的风格梵蒂冈的风格斯打飞机啊水淀粉是东方" +
                "时空的风景是达六块腹肌阿电饭锅电饭锅电饭锅里斯顿开发及阿里山的风景阿" +
                "斯顿发水淀粉矢的分公司地方公司的风格口抵赖飞机失联" +
                "的开发及历史的风的分公司地方公司的风格景阿斯顿浪费");


        TextView textView2 = createTextView();
        textView2.setText("电饭锅电饭锅电饭锅；司法解释；对佛诶认为哦 i 人 大是大非 i 欧式地方 i 哦苏东坡" +
                "发送东方爬上发呆第三方 i 是豆腐皮送到附送IP的发送滴批发 u 烧豆腐似佛苏东坡发 i" +
                "的发送东方 i 哦说发破碎都发破碎地方视频地方水淀粉是的分手第发送东方 i 是滴批发 i 偶阿" +
                "首都发死都发视频奋斗的 v 旧恨新仇离开 v 就喜欢 v 吃来了虚假看了下火车 v 新款雏菊行cvxcliv");

        textViews.add(textView1);
        textViews.add(textView2);



    }

    private TextView createTextView() {

        TextView textView = new TextView(this);

        FlexboxLayout.LayoutParams p = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.MATCH_PARENT,
                FlexboxLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(p);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_32px));

        int pad = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);

        textView.setPadding(pad,pad,pad,pad);
        textView.setTextColor(getResources().getColor(R.color.font_black));

        textView.setLineSpacing(2f, 1.5f);

        return textView;
    }


    private void showApplyDlg() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_apply_lession_dlg, null);
        ListView payListView = (ListView) view.findViewById(R.id.list_pay);

        String[] payArray = getResources().getStringArray(R.array.lesson_pay_methods);
        PayAdapter payAdapter = new PayAdapter(this, R.layout.layout_pay_lesson_item, payArray);
        payListView.setAdapter(payAdapter);
        payListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        BottomSheet bottomSheet = new BottomSheet(this);
        bottomSheet.setTitleVisibility(View.GONE);
        bottomSheet.setContent(view);
        bottomSheet.show();

    }


    private class PayAdapter extends ArrayAdapter<String> {
        public PayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            CheckedTextView textView = (CheckedTextView) v;

            if (position == 0) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alipay, 0, 0, 0);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wechat, 0, 0, 0);
            }
            return v;
        }
    }




}
