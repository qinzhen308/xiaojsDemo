package cn.xiaojs.xma.ui.lesson.xclass.animlib;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/6/7.
 */

public class Xiaojs120Anim {
    WindowManager manager;
    ImageView fistCircleEditBtn;

    public Xiaojs120Anim(Activity context) {
        manager=context.getWindowManager();
    }


    public void attachCircleEditView(Activity context, View tagView){
        if(fistCircleEditBtn==null){
            fistCircleEditBtn=new ImageView(context);
            fistCircleEditBtn.setImageResource(R.drawable.ic_first_circle_edit_btn);
        }
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams(tagView.getWidth(),tagView.getHeight());
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        lp.type=WindowManager.LayoutParams.TYPE_TOAST;
        lp.alpha=0;
        int[] xy=new int[2];
        tagView.getLocationInWindow(xy);
        lp.x=xy[0];
        lp.y=xy[1];
        manager.addView(fistCircleEditBtn,lp);
    }

    public void dettachCircleEditView(Activity context){
        if(fistCircleEditBtn!=null)
        manager.removeViewImmediate(fistCircleEditBtn);
    }


}
