package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.concurrent.TimeUnit;

import cn.xiaojs.xma.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class FilterPopupMenu {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ListView mListView;
    //private View mRootView;
    private OnItemClickListener mListener;

    private int offset;
    private String[] titles;

    public FilterPopupMenu(Context ctx, String[] titles, int defualtCheckedIndex) {
        this.titles = titles;
        init(ctx, 0, defualtCheckedIndex);
    }

    private void init(Context ctx, int pix, int checkedIndex) {
        mContext = ctx;
        View rootView = LayoutInflater.from(ctx).inflate(
                R.layout.layout_classroom2_material_popup_menu, null);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                R.layout.layout_classroom2_material_fiter_menu_item, R.id.title, titles);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setItemChecked(checkedIndex, true);

        mPopupWindow = new PopupWindow(rootView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_popup_menu));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Observable.timer(100, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                dismiss();
                            }
                        });
                
                mListener.onItemClick(parent, view, position, id);


            }

        });
    }

    public String getItem(int position) {
        return titles[position];
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

}
