package com.benyuan.xiaojs.ui.mine;

import com.google.android.flexbox.FlexboxLayout;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.view.TagView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;

public class EditTeachingAbilityActivity extends BaseActivity {

    private String[] testAblityCategories = {"早教育儿","中小学","大学","留学","外语培训","公务员","学历考试"};
    private String[] testAblitiesSub1 = {"PPT","EXCEl","WROD","WPS","更多软件"};
    private String[] testAblitiesSub2 = {"求职简历","面试技巧","留学规划","电子商务","领导力培养","沟通谈判","演讲与口才"};

    @BindView(R.id.ability_listview)
    ListView listView;

    @BindView(R.id.ability_detail_layout)
    FlexboxLayout flexboxLayout;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_edit_teaching_ability);
        setMiddleTitle(R.string.teach_ability);
        setLeftImage(R.drawable.back_arrow);
        setRightText(R.string.finish);

        listView.setAdapter(new AbilityAdapter());
        //listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


        testShowAbilityItems();
    }

    @OnClick({R.id.left_image})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;

        }
    }


    private void testShowAbilityItems(){




        int margin = (int)getResources().getDimension(R.dimen.ability_item_margin);
        int width = (int)getResources().getDimension(R.dimen.ability_item_width);
        int height = (int)getResources().getDimension(R.dimen.ability_item_height);

        FlexboxLayout.LayoutParams layoutParamsTitle = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        layoutParamsTitle.setMargins(margin,margin,margin,margin);


        FlexboxLayout.LayoutParams layoutParamsItem = new FlexboxLayout.LayoutParams(width,height);
        layoutParamsItem.setMargins(margin,margin,margin,margin);


        TextView tvTitle = createAbilityTitle("办公软件");
        tvTitle.setLayoutParams(layoutParamsTitle);
        flexboxLayout.addView(tvTitle);

        for(int i=0;i<testAblitiesSub1.length;i++){

            TagView tagBtn = createAbilityItem(testAblitiesSub1[i]);
            tagBtn.setLayoutParams(layoutParamsItem);
            flexboxLayout.addView(tagBtn);
        }

        tvTitle = createAbilityTitle("职场技能");
        tvTitle.setLayoutParams(layoutParamsTitle);
        flexboxLayout.addView(tvTitle);

        for(int i=0;i<testAblitiesSub2.length;i++){

            TagView tagBtn = createAbilityItem(testAblitiesSub2[i]);
            tagBtn.setLayoutParams(layoutParamsItem);
            flexboxLayout.addView(tagBtn);
        }



    }

    private TextView createAbilityTitle(String text) {
        TextView textView = new TextView(this);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        return textView;
    }

    private TagView createAbilityItem(String text) {
        TagView tagBtn = new TagView(this);
        tagBtn.setBackgroundResource(R.drawable.tag_bg_selector);
        tagBtn.setText(text);
        tagBtn.setTextColor(Color.GRAY);
        //tagBtn.setGravity(Gravity.CENTER);


        return tagBtn;
    }

    private class AbilityAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return testAblityCategories.length;
        }

        @Override
        public String getItem(int position) {
            return testAblityCategories[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null){
                convertView =  LayoutInflater.from(EditTeachingAbilityActivity.this)
                        .inflate(R.layout.layout_ability_title,null);

                holder = new ViewHolder(convertView);

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }


            holder.titleView.setText(getItem(position));


            return convertView;
        }


    }

    static class ViewHolder extends BaseHolder{

        @BindView(R.id.ability_title_view)
        TextView titleView;

        @BindView(R.id.ability_icon_view)
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
        }



    }
}
