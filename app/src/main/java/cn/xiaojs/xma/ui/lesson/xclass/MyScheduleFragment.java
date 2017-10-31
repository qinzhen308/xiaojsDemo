package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import butterknife.BindColor;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;

/**
 * Created by Paul Z on 2017/5/19.
 */

public class MyScheduleFragment extends BaseFragment implements IUpdateMethod{

    @BindColor(R.color.white) int textColorWhite;
    @BindColor(R.color.font_light_gray) int textColorGray;

    private int curTabIndex=TAB_WANNA_LEARN;
    private final static int TAB_WANNA_LEARN=0;
    private final static int TAB_WANNA_TEACH=1;

    MyScheduleBuz contentBuz;

    MyScheduleBuz.OnMonthChangeListener onMonthChangeListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rejectButterKnife();
    }

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_my_schedule, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void init() {
        contentBuz=new MyScheduleBuz();
        contentBuz.setOnMonthChangeListener(onMonthChangeListener);
        contentBuz.init(getActivity(),mContent.findViewById(R.id.layout_schedule_root));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    public void updateData(){
        contentBuz.update();
    }


    @Override
    protected int registerDataChangeListenerType() {
        return SimpleDataChangeListener.CREATE_CLASS_CHANGED|SimpleDataChangeListener.LESSON_CREATION_CHANGED;
    }


    @Override
    protected void onDataChanged() {
        contentBuz.update();
    }

    @Override
    public void updateData(boolean justNative,Object... others) {
        if(justNative){
            contentBuz.mAdapter.notifyDataSetChanged();
        }else {
            updateData();
        }
    }

    @Override
    public void updateItem(int position, Object obj, Object... others) {
        updateData();
    }

    public long getSelectedDate(){
        return contentBuz.getSelectedDate();
    }

    public void setOnMonthChangeListener(MyScheduleBuz.OnMonthChangeListener onMonthChangeListener) {
        this.onMonthChangeListener=onMonthChangeListener;
        if(contentBuz!=null){
            contentBuz.setOnMonthChangeListener(onMonthChangeListener);
        }
    }

}
