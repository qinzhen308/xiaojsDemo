package cn.xiaojs.xma.ui.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class CommonPopupMenu extends PopupWindow {
	private ArrayList<String> mItemList;
	private ArrayList<Integer> mImgList;
	private Context mContext;
	private PopupWindow mPopupWindow;
	private ListView mListView;
	private View mRootView;
	private OnItemClickListener mListener;

	private int offset;
	public CommonPopupMenu(Context ctx) {
		super(ctx);
		init(ctx,0);
	}
	
	private void init(Context ctx,int pix) {
		mContext = ctx;
		mRootView = LayoutInflater.from(ctx).inflate(
				R.layout.common_popup_menu, null);
		mItemList = new ArrayList<String>();
		mImgList = new ArrayList<Integer>();
		mListView = (ListView) mRootView.findViewById(R.id.listView);

		mListView = (ListView) mRootView.findViewById(R.id.listView);

		if(pix == 0)
			mListView.setAdapter(new PopupMenuAdapter());
		else
			mListView.setAdapter(new PopupMenuAdapter(pix));
		mPopupWindow = new PopupWindow(mRootView, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
	public void setBg(int resId) {
		mRootView.findViewById(R.id.popup_view_content).setBackgroundResource(
				resId);
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

	@Override
	public void showAsDropDown(View parent) {
		mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		final int listWidth = mListView.getMeasuredWidth();
		int left = DeviceUtil.getScreenWidth(mContext)  - listWidth - offset;
		mPopupWindow.showAsDropDown(parent, left, 0);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
	}

	@Override
	public void showAsDropDown(View parent, int x, int y) {
		mPopupWindow.showAsDropDown(parent, x, y);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
	}

	public void show(View anchor,int offset){
		this.offset = offset;
		showAsDropDown(anchor);
	}

	@Override
	public void dismiss() {
		mPopupWindow.dismiss();
	}

	private final class PopupMenuAdapter extends BaseAdapter {
		public final static int POP_DEFAULT_TYPE = 0;
		public final static int POP_PX24_TYPE = 1;
		private ItemHolder mHolder = null;
		private int mTextSizeType;

		public PopupMenuAdapter(){
			mTextSizeType = POP_DEFAULT_TYPE;
		}
		/**
		 * 这里直接设置像素有问题。暂时默认了。
		 * @param pix
		 */
		public PopupMenuAdapter(int pix){
			mTextSizeType = POP_PX24_TYPE;
		}

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
			if (convertView == null) {
				if(mTextSizeType == POP_DEFAULT_TYPE)
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.common_popup_menu_item, null);
				else{}

				TextView menuItem = (TextView) convertView;
				menuItem.setText(mItemList.get(position));
				mHolder = new ItemHolder();
				mHolder.mMenuItem = menuItem;
				convertView.setTag(mHolder);
			} else {
				mHolder = (ItemHolder) convertView.getTag();
			}
			try {
				mHolder.mMenuItem.setText(mItemList.get(position));
				Drawable d = mContext.getResources().getDrawable(mImgList.get(position));
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				mHolder.mMenuItem.setCompoundDrawables(d, null, null, null);
			} catch (Exception e) {

			}
			return convertView;
		}
	}

	private static class ItemHolder {
		TextView mMenuItem;
	}

}
