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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.DeviceUtil;

import java.util.ArrayList;

public class CommonPopupMenu {
    private ArrayList<String> mItemList;
    private ArrayList<Integer> mImgList;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ListView mListView;
    //private View mRootView;
    private OnItemClickListener mListener;

    private int offset;

    public CommonPopupMenu(Context ctx) {
        init(ctx, 0);
    }

    private void init(Context ctx, int pix) {
        mContext = ctx;
        mListView = (ListView) LayoutInflater.from(ctx).inflate(
                R.layout.common_popup_menu, null);
        mListView.setPadding(
                ctx.getResources().getDimensionPixelSize(R.dimen.px15),
                ctx.getResources().getDimensionPixelSize(R.dimen.px30),
                ctx.getResources().getDimensionPixelSize(R.dimen.px15),
                ctx.getResources().getDimensionPixelSize(R.dimen.px25));
        mItemList = new ArrayList<String>();
        mImgList = new ArrayList<Integer>();

        mListView.setAdapter(new PopupMenuAdapter());

        int width = mContext.getResources().getDimensionPixelSize(R.dimen.px244);

        mPopupWindow = new PopupWindow(mListView, width, ViewGroup.LayoutParams.WRAP_CONTENT);
        //mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_popup_menu));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

    }

    public void setBg(@DrawableRes int drawable) {
        mListView.setBackgroundResource(drawable);
    }

    public void setWidth(int px){
        mPopupWindow.setWidth(px);
//        mListView.getLayoutParams().width=px;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mListener.onItemClick(parent, view, position, id);
                dismiss();
            }

        });
    }

    public void addTextItems(String[] items) {
        for (String s : items)
            mItemList.add(s);
    }

    public void addImgItems(Integer[] imgItems) {
        for (Integer s : imgItems)
            mImgList.add(s);
    }

    public Object getItem(int position) {
        return mItemList.get(position);
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

    public void show(View anchor, int offset) {
        this.offset = offset;
        showAsDropDown(anchor);
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    private final class PopupMenuAdapter extends BaseAdapter {
        public final static int POP_DEFAULT_TYPE = 0;
        public final static int POP_PX24_TYPE = 1;


        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder mHolder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.common_popup_menu_item, parent, false);

                TextView menuItem = (TextView) convertView;
                mHolder = new ItemHolder();
                mHolder.mMenuItem = menuItem;
                convertView.setTag(mHolder);
            } else {
                mHolder = (ItemHolder) convertView.getTag();
            }
            mHolder.mMenuItem.setText(mItemList.get(position));
            Drawable d = mContext.getResources().getDrawable(mImgList.get(position));
            mHolder.mMenuItem.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);

            return convertView;
        }
    }

    private static class ItemHolder {
        TextView mMenuItem;
    }

}
