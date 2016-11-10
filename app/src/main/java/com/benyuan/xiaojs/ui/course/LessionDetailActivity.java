package com.benyuan.xiaojs.ui.course;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.BottomSheet;

import butterknife.OnClick;

public class LessionDetailActivity extends BaseActivity {


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_lession_detail);
        setMiddleTitle(R.string.lession_detail);
        setLeftImage(R.drawable.back_arrow);


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
