package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.util.ToastUtil;


/**
 * Created by Paul Z on 2017/7/20.
 */

public class SelectDirectoryActivity extends BaseActivity {

    public static final String EXTRA_KEY_SELETED_POSITION="extra_key_seleted_position";
    public static final String EXTRA_KEY_DIRS="extra_key_dirs";
    public static final int SELECTED_POSITION_NONE=-1;//还未选择
    public static final int REQUEST_CODE=33;

    @BindView(R.id.listview)
    ListView listview;
    ChoiceAdapter adapter;

    private int seletedPosition=SELECTED_POSITION_NONE;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_select_belong_to_dir);
        setMiddleTitle(R.string.select_belong_to_dir);
        setRightText(R.string.finish);
        listview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        Intent intent=getIntent();
        seletedPosition=intent.getIntExtra(EXTRA_KEY_SELETED_POSITION,SELECTED_POSITION_NONE);
        initData((ArrayList<RLDirectory>) intent.getSerializableExtra(EXTRA_KEY_DIRS));
    }

    private void initData(ArrayList<RLDirectory> list){

        adapter = new ChoiceAdapter(this,
                R.layout.item_rl_select_dir,
                R.id.tv_title,
                list);
        listview.setAdapter(adapter);

        if (seletedPosition >= 0 && listview.getCount() > seletedPosition) {
            listview.setItemChecked(seletedPosition, true);
        }

    }



    @OnClick({R.id.left_image,R.id.right_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_view:
                save();
                break;
        }
    }

    private void save(){
        seletedPosition=listview.getCheckedItemPosition();
        if(seletedPosition<0){
            ToastUtil.showToast(getApplicationContext(),R.string.please_select);
            return;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_KEY_SELETED_POSITION,seletedPosition);
        setResult(RESULT_OK,intent);
        finish();
    }


    public static void invoke(Activity context, ArrayList<RLDirectory> dirs, int selectedPosition){
        Intent intent=new Intent(context,SelectDirectoryActivity.class);
        intent.putExtra(EXTRA_KEY_DIRS,dirs);
        intent.putExtra(EXTRA_KEY_SELETED_POSITION,selectedPosition<0?SELECTED_POSITION_NONE:selectedPosition);
        context.startActivityForResult(intent,REQUEST_CODE);
    }


    private class ChoiceAdapter extends ArrayAdapter<RLDirectory> {
        public ChoiceAdapter(Context context, int resource, int tid, List<RLDirectory> objects) {
            super(context, resource, tid, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView title = (TextView) v.findViewById(R.id.tv_title);
            TextView count = (TextView) v.findViewById(R.id.tv_count);
            title.setText(getItem(position).name);
            count.setText("（"+getItem(position).getChildrenCount()+"）");
            return v;
        }
    }



}
