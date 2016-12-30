package cn.xiaojs.xma.ui.classroom;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.document.DocumentActivity;
import retrofit2.http.PUT;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

public class WhiteBoardManagement extends DialogFragment implements AdapterView.OnItemClickListener {
    public static final String WHITE_BOARD_COLL = "white_board_coll";
    private static final int NUM_COLUMN = 4;
    private static final int MAX_COUNT = 8;

    private View mRoot;
    private GridView mGridView;
    private ImageView mAddWbBtn;
    private ImageView mOpenDocsBtn;
    private ImageView mDelWbBtn;
    private Drawable mTransparentBg;
    private WbAdapter mWbAdapter;

    private Context mContext;
    private LayoutInflater mInflater;
    private int mHorizontalPadding;
    private int mScreenWidth;
    private int mItemWidth;
    private int mCoverWidth;
    private int mCOverHeight;

    private ArrayList<WhiteboardCollection> mCollections;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mContext instanceof ClassroomActivity) {
            ((ClassroomActivity)mContext).updateWhiteboardCollCountStyle();
        }
        if (mWbAdapter != null) {
            mWbAdapter.exitRemoveMode(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mTransparentBg = new ColorDrawable(Color.TRANSPARENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_white_board_management, null);
        return mRoot;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Window w = getDialog().getWindow();
        if (w != null) {
            w.setBackgroundDrawable(mTransparentBg);
        }*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();

    }

    private void initView() {
        mInflater = LayoutInflater.from(mContext);
        mGridView = (GridView) mRoot.findViewById(R.id.white_coll_list);
        mAddWbBtn = (ImageView) mRoot.findViewById(R.id.add_white_board);
        mOpenDocsBtn = (ImageView) mRoot.findViewById(R.id.open_docs);
        mDelWbBtn = (ImageView) mRoot.findViewById(R.id.del_white_board);

        mRoot.setOnClickListener(mClickListener);
        mAddWbBtn.setOnClickListener(mClickListener);
        mOpenDocsBtn.setOnClickListener(mClickListener);
        mDelWbBtn.setOnClickListener(mClickListener);

        mGridView.setOnItemClickListener(this);

        Resources rs = mContext.getResources();
        mScreenWidth = rs.getDisplayMetrics().widthPixels;
        mHorizontalPadding = rs.getDimensionPixelOffset(R.dimen.px6);
        int gdMargin = rs.getDimensionPixelOffset(R.dimen.px12);
        int coverMargin = rs.getDimensionPixelOffset(R.dimen.px12);
        int contentMargin = rs.getDimensionPixelOffset(R.dimen.px12);

        mGridView.setHorizontalSpacing(mHorizontalPadding);
        mGridView.setNumColumns(NUM_COLUMN);

        mCoverWidth = (mScreenWidth - 2 * gdMargin - (NUM_COLUMN - 1) * mHorizontalPadding) / 4 - (coverMargin + contentMargin) * 2;
        mCOverHeight =  (int)(0.75f * mCoverWidth);
    }

    private void initData() {
        if (mWbAdapter == null) {
            mWbAdapter = new WbAdapter();
        }
        mGridView.setAdapter(mWbAdapter);

        Bundle data = getArguments();
        if (data != null) {
            mCollections = data.getParcelableArrayList(WHITE_BOARD_COLL);
            mWbAdapter.setData(mCollections);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int count = mCollections != null ? mCollections.size() : 0;
            if (count >= MAX_COUNT && v.getId() != R.id.del_white_board) {
                String tips = String.format(mContext.getString(R.string.add_white_board_exceed), MAX_COUNT);
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                return;
            }

            switch (v.getId()) {
                case R.id.add_white_board:
                    //add default board
                    WhiteboardCollection wbColl = new WhiteboardCollection();
                    if (mContext instanceof ClassroomActivity) {
                        ((ClassroomActivity)mContext).onAddWhiteboardCollection(wbColl);
                    }
                    mWbAdapter.notifyDataSetChanged();
                    break;
                case R.id.open_docs:
                    //open document
                    if (mWbAdapter != null) {
                        if (mWbAdapter.isRemoveMode()) {
                            mWbAdapter.exitRemoveMode(true);
                        }
                    }
                    Intent i = new Intent(mContext, DocumentActivity.class);
                    mContext.startActivity(i);
                    break;
                case R.id.del_white_board:
                    //remove white boards
                    if (mWbAdapter != null) {
                        if (mWbAdapter.isRemoveMode()) {
                            mWbAdapter.exitRemoveMode(true);
                        } else {
                            mWbAdapter.enterRemoveMode();
                        }
                    }
                    break;
            }
        }
    };

    private class WbAdapter extends BaseAdapter implements View.OnClickListener{
        private ArrayList<WhiteboardCollection> mWbCollList;
        private boolean mRemoveMode = false;

        public WbAdapter() {
        }

        public WbAdapter(ArrayList<WhiteboardCollection> list) {
            mWbCollList = list;
        }

        public void setData(ArrayList<WhiteboardCollection> list) {
            mWbCollList = list;
            notifyDataSetChanged();
        }

        public void enterRemoveMode() {
            mRemoveMode = true;
            notifyDataSetChanged();
        }

        public void exitRemoveMode(boolean needRefresh) {
            mRemoveMode = false;
            if (needRefresh) {
                notifyDataSetChanged();
            }
        }

        public boolean isRemoveMode() {
            return mRemoveMode;
        }

        @Override
        public int getCount() {
            return mWbCollList != null ? mWbCollList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mWbCollList != null ? mWbCollList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;

            if (convertView == null) {
                convertView = createView();
            }

            holder = (Holder)convertView.getTag();

            bindData(holder, position);
            return convertView;
        }


        private View createView() {
            View v = mInflater.inflate(R.layout.layout_wb_management_item, null);
            Holder holder = new Holder();

            holder.title = (TextView) v.findViewById(R.id.wb_title);
            holder.cover = (ImageView) v.findViewById(R.id.wb_cover);
            holder.removeBtn = (ImageView) v.findViewById(R.id.wb_remove);
            v.setTag(holder);

            ViewGroup.LayoutParams params = holder.cover.getLayoutParams();
            if (params != null) {
                params.width = mCoverWidth;
                params.height = mCOverHeight;
            } else {
                params = new LinearLayout.LayoutParams(mCoverWidth, mCOverHeight);
                holder.cover.setLayoutParams(params);
            }

            holder.removeBtn.setOnClickListener(this);
            return v;
        }

        private void bindData(Holder holder, int pos) {
            WhiteboardCollection wbColl = mWbCollList.get(pos);
            holder.title.setText(wbColl.getName());
            holder.cover.setBackgroundColor(Color.GRAY);

            if (mRemoveMode) {
                holder.removeBtn.setVisibility(View.VISIBLE);
                holder.removeBtn.setTag(wbColl);
            } else {
                holder.removeBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wb_remove:
                    Object obj = v.getTag();
                    if (mWbCollList != null && obj instanceof WhiteboardCollection) {
                        mWbCollList.remove(obj);
                        notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    private static class Holder {
        public TextView title;
        public ImageView cover;
        public ImageView removeBtn;
    }
}
