package cn.xiaojs.xma.ui.view;

import android.app.Activity;
import android.view.View;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class HomeClassContentBuz {
    Activity mContext;
    View mRoot;


    /**
     * @link cn.xiaojs.xma.R.layout.fragment_home_class_normal.xml
     * @param context
     * @param root
     */
    public void init(Activity context, View root){
        mContext=context;
        mRoot=root;

//                R.layout.fragment_home_class_normal
        ButterKnife.bind(this,root);
    }


}
