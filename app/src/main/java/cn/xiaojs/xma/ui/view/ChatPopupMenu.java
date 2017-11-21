package cn.xiaojs.xma.ui.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import cn.xiaojs.xma.R;

public class ChatPopupMenu {
    private Context mContext;
    private PopupWindow mPopupWindow;

    public ChatPopupMenu(Context ctx) {
        init(ctx, 0);
    }

    private void init(Context ctx, int pix) {
        mContext = ctx;
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(ctx).inflate(
                R.layout.layout_classroom2_chat_item_popup_menu, null);

        int width = mContext.getResources().getDimensionPixelSize(R.dimen.px100);

        mPopupWindow = new PopupWindow(linearLayout, width, ViewGroup.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_popup_menu));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    public void setWidth(int px) {
        mPopupWindow.setWidth(px);
//        mListView.getLayoutParams().width=px;
    }


    public void showAsDropDown(View parent) {
        //mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//		final int listWidth = mListView.getMeasuredWidth();
//		int left = DeviceUtil.getScreenWidth(mContext)  - listWidth - offset;
        mPopupWindow.showAsDropDown(parent);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
    }

    public void showAsDropDown(View parent, int x, int y) {
        mPopupWindow.showAsDropDown(parent, x, y);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
    }

    public void show(View anchor) {
        showAsDropDown(anchor);
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

}
