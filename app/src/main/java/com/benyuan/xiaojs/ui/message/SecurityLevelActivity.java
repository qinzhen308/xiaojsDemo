package com.benyuan.xiaojs.ui.message;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SecurityLevelActivity extends BaseActivity {

    @BindView(R.id.select_list)
    ListView listView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_security_level);
        setMiddleTitle(R.string.who_can_see_me);
        setRightText(R.string.finish);

        init();

    }

    private void init() {



        String [] levels = getResources().getStringArray(R.array.post_security_level);

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//                R.layout.layout_single_select_item,levels);

        listView.setAdapter(new LevelAdapter(this,levels));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }


    @OnClick({R.id.left_image, R.id.right_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_view:

                break;

        }
    }


    private class LevelAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private String[] datas;

        public LevelAdapter(Context context,String[] data) {

            this.datas = data;
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_single_select_item,parent,false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.title);
            textView.setText(datas[position]);

            return convertView;
        }
    }



}
