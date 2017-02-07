package cn.xiaojs.xma.ui.classroom;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardProcessor;

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

public class WhiteboardManageFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    public static final String WHITE_BOARD_COLL = "white_board_coll";
    public static final String WHITE_BOARD_CLIENT = "white_board_client";

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
    private String mLiveWhiteboardName;
    private Constants.User mUser = Constants.User.TEACHER;
    private String mTicket;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mContext instanceof ClassroomActivity) {
            ((ClassroomActivity) mContext).updateWhiteboardCollCountStyle();
        }
        if (mWbAdapter != null) {
            mWbAdapter.exitCloseMode(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mTransparentBg = new ColorDrawable(Color.TRANSPARENT);
        mLiveWhiteboardName = mContext.getResources().getString(R.string.live_whiteboard);
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
        mCOverHeight = (int) (0.75f * mCoverWidth);
    }

    private void initData() {
        if (mWbAdapter == null) {
            mWbAdapter = new WbAdapter();
        }
        mGridView.setAdapter(mWbAdapter);

        Bundle data = getArguments();
        if (data != null) {
            mUser = (Constants.User) data.getSerializable(WHITE_BOARD_CLIENT);
            mCollections = WhiteboardManager.getInstance().getWhiteboardCollectionList();
            mWbAdapter.setData(mCollections);
        }

        if (mContext instanceof ClassroomActivity) {
            mTicket = ((ClassroomActivity) mContext).getTicket();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = mWbAdapter.getItem(position);
        if (obj instanceof WhiteboardCollection) {
            ((ClassroomActivity) mContext).onSwitchWhiteboardCollection((WhiteboardCollection) obj);
            WhiteboardManageFragment.this.dismiss();
        }
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
                    addDefaultWhiteboard();
                    break;
                case R.id.open_docs:
                    //open document
                    if (mWbAdapter != null) {
                        if (mWbAdapter.isCloseMode()) {
                            mWbAdapter.exitCloseMode(true);
                        }
                    }
                    Intent i = new Intent(mContext, DocumentActivity.class);
                    mContext.startActivity(i);
                    break;
                case R.id.del_white_board:
                    //remove white boards
                    if (mWbAdapter != null) {
                        if (mWbAdapter.isCloseMode()) {
                            mWbAdapter.exitCloseMode(true);
                        } else {
                            mWbAdapter.enterCloseMode();
                        }
                    }
                    break;
            }
        }
    };


    private void addDefaultWhiteboard() {
        WhiteboardManager.getInstance().addDefaultBoard(mContext, mUser, new WhiteboardManager.WhiteboardAddListener() {
            @Override
            public void onWhiteboardAdded(WhiteboardCollection boardCollection) {
                mWbAdapter.notifyDataSetChanged();
                if (mContext instanceof ClassroomActivity) {
                    ((ClassroomActivity) mContext).updateWhiteboardCollCountStyle();
                }
            }

        });
    }

    /**
     * 添加课件白板
     */
    private void addCourseWhiteboard() {
        WhiteboardManager.getInstance().addDefaultBoard(mContext, mUser, new WhiteboardManager.WhiteboardAddListener() {
            @Override
            public void onWhiteboardAdded(WhiteboardCollection boardCollection) {
                mWbAdapter.notifyDataSetChanged();
                if (mContext instanceof ClassroomActivity) {
                    ((ClassroomActivity) mContext).updateWhiteboardCollCountStyle();
                }
            }

        });
    }

    private class WbAdapter extends BaseAdapter implements View.OnClickListener {
        private ArrayList<WhiteboardCollection> mWbCollList;
        private boolean mCloseMode = false;
        private int mWhiteColor;

        public WbAdapter() {
            mWhiteColor = mContext.getResources().getColor(R.color.white);
        }

        public WbAdapter(ArrayList<WhiteboardCollection> list) {
            mWbCollList = list;
            mWhiteColor = mContext.getResources().getColor(R.color.white);
        }

        public void setData(ArrayList<WhiteboardCollection> list) {
            mWbCollList = list;
            notifyDataSetChanged();
        }

        public void enterCloseMode() {
            mCloseMode = true;
            notifyDataSetChanged();
        }

        public void exitCloseMode(boolean needRefresh) {
            mCloseMode = false;
            if (needRefresh) {
                notifyDataSetChanged();
            }
        }

        public boolean isCloseMode() {
            return mCloseMode;
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

            holder = (Holder) convertView.getTag();

            bindData(holder, position);
            return convertView;
        }


        private View createView() {
            View v = mInflater.inflate(R.layout.layout_wb_management_item, null);
            Holder holder = new Holder();

            holder.title = (TextView) v.findViewById(R.id.wb_title);
            holder.cover = (ImageView) v.findViewById(R.id.wb_cover);
            holder.closeBtn = (ImageView) v.findViewById(R.id.wb_close);
            v.setTag(holder);

            ViewGroup.LayoutParams params = holder.cover.getLayoutParams();
            if (params != null) {
                params.width = mCoverWidth;
                params.height = mCOverHeight;
            } else {
                params = new LinearLayout.LayoutParams(mCoverWidth, mCOverHeight);
                holder.cover.setLayoutParams(params);
            }

            holder.closeBtn.setOnClickListener(this);
            return v;
        }

        private void bindData(Holder holder, int pos) {
            WhiteboardCollection wbColl = mWbCollList.get(pos);
            holder.title.setText(wbColl.isLive() ? mLiveWhiteboardName : wbColl.getTitle());
            holder.cover.setBackgroundColor(mWhiteColor);
            holder.cover.setTag(pos);
            if (wbColl.isDefaultWhiteboard()) {
                new LoadDefaultWhiteboardTask(holder.cover, pos).execute(wbColl);
                //Bitmap bmp = WhiteboardProcessor.process(wbColl, mCoverWidth, mCOverHeight);
                //holder.cover.setImageBitmap(bmp);
            }

            if (!mCloseMode || (wbColl.isLive() && mUser == Constants.User.STUDENT)) {
                holder.closeBtn.setVisibility(View.GONE);
            } else {
                holder.closeBtn.setVisibility(View.VISIBLE);
                holder.closeBtn.setTag(wbColl);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wb_close:
                    Object obj = v.getTag();
                    if (obj instanceof WhiteboardCollection) {
                        WhiteboardManager.getInstance().closeBoard(mContext, mUser, (WhiteboardCollection) obj,
                                new WhiteboardManager.WhiteboardCloseListener() {
                                    @Override
                                    public void onWhiteboardClosed(WhiteboardCollection boardCollection) {
                                        notifyDataSetChanged();
                                    }
                                });
                    }
                    break;
            }
        }

        private class Holder {
            public TextView title;
            public ImageView cover;
            public ImageView closeBtn;
        }

        private class LoadDefaultWhiteboardTask extends AsyncTask<WhiteboardCollection, Integer, Bitmap> {
            private ImageView mCoverImg;
            private int mPos;

            public LoadDefaultWhiteboardTask(ImageView imgV, int pos) {
                mCoverImg = imgV;
                mPos = pos;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                int refreshPos = (Integer) mCoverImg.getTag();
                if (mCoverImg != null && refreshPos == mPos) {
                    mCoverImg.setImageBitmap(bitmap);
                } else {
                    mCoverImg.setImageBitmap(null);
                }
            }

            @Override
            protected Bitmap doInBackground(WhiteboardCollection... params) {
                if (params == null || params.length == 0) {
                    return null;
                }

                return WhiteboardProcessor.process(params[0], mCoverWidth, mCOverHeight);
            }
        }
    }

}
