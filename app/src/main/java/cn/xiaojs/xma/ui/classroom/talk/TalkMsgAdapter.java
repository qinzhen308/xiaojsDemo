package cn.xiaojs.xma.ui.classroom.talk;
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
 * Date:2016/12/20
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.ClassroomBusiness;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.TimeUtil;

public class TalkMsgAdapter extends AbsChatAdapter<TalkItem, TalkMsgAdapter.Holder> implements View.OnClickListener{
    public final static int TYPE_MY_SPEAKER = 0;
    public final static int TYPE_OTHER_SPEAKER = 1;
    private static int MAX_SIZE = 280;

    private Context mContext;
    private String mTicket;
    private LiveCriteria mLiveCriteria;
    private OnImageClickListener mOnImageClickListener;

    public TalkMsgAdapter(Context context, String ticket, LiveCriteria liveCriteria, PullToRefreshListView listView) {
        super(context, listView);
        mContext = context;
        mTicket = ticket;
        mLiveCriteria = liveCriteria;
        MAX_SIZE = context.getResources().getDimensionPixelSize(R.dimen.px280);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    @Override
    public void add(TalkItem talkItem) {
        if (!contains(talkItem)) {
            super.add(talkItem);
        }
    }

    @Override
    protected void setViewContent(Holder holder, TalkItem bean, int position) {
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.px90);
        String portraitUrl = Account.getAvatar(bean.from != null ? bean.from.accountId : null, size);
        Glide.with(mContext)
                .load(portraitUrl)
                .error(R.drawable.default_avatar)
                .into(holder.portrait);
        holder.name.setText(bean.from.name);
        boolean isText = false;
        String imgKey = null;
        String txt = null;
        if (bean.body != null) {
            if (!TextUtils.isEmpty(bean.body.text)) {
                txt = bean.body.text;
                if (bean.body.contentType == Communications.ContentType.TEXT) {
                    isText = true;
                }
            } else {
                if (bean.body.drawing != null) {
                    imgKey = bean.body.drawing.name;
                }
            }
        }

        holder.msgImg.setTag(position);
        if (isText) {
            holder.msgImg.setVisibility(View.GONE);
            holder.msgTxt.setVisibility(View.VISIBLE);
            holder.msgTxt.setText(bean.body != null ? bean.body.text : null);
        } else if (!TextUtils.isEmpty(txt) || !TextUtils.isEmpty(imgKey)){
            holder.msgTxt.setVisibility(View.GONE);
            holder.msgImg.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(txt)) {
                //decode base64 to bitmap
                //TODO 待优化
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)holder.msgImg.getLayoutParams();
                //byte[] imgData = Base64.decode(txt, Base64.DEFAULT);
                //Bitmap bmp = BitmapUtils.byteArrayToBitmap(imgData);
                Bitmap bmp = ClassroomBusiness.base64ToBitmap(txt);
                if (bmp != null) {
                    int w = MAX_SIZE;
                    int h = MAX_SIZE;
                    if (bmp.getWidth() > bmp.getHeight()) {
                        w = MAX_SIZE;
                        h = (int)((bmp.getHeight() / (float)bmp.getWidth()) * MAX_SIZE);
                    } else {
                        h = MAX_SIZE;
                        w = (int)((bmp.getWidth() / (float)bmp.getHeight()) * MAX_SIZE);
                    }
                    params.width = w;
                    params.height = h;
                }
                holder.msgImg.setImageBitmap(bmp);
                //new LoadBase64ImgTask(holder.msgImg).execute(txt);
            } else {
                //load img from qiniu url
                String imgUrl = ClassroomBusiness.getSnapshot(imgKey, MAX_SIZE);
                Glide.with(mContext)
                        .load(imgUrl)
                        .into(holder.msgImg);
            }

        }

        holder.time.setText(TimeUtil.format(bean.time, TimeUtil.TIME_MM_SS));
    }

    @Override
    public int getItemViewType(int position) {
        List<TalkItem> talkItems = getList();
        TalkItem item = null;
        if (talkItems == null || (item = talkItems.get(position)) == null) {
            Logger.i("task items is empty");
            return TYPE_MY_SPEAKER;
        }

        if (item == null || item.from == null) {
            Logger.i("item is empty");
            return TYPE_MY_SPEAKER;
        }

        return isMyself(item.from.accountId) ? TYPE_MY_SPEAKER : TYPE_OTHER_SPEAKER;
    }

    @Override
    protected View createContentView(int position) {
        int type = getItemViewType(position);
        View v = null;
        switch (type) {
            case TYPE_MY_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_talk_my_speaker_item, null);
                break;
            case TYPE_OTHER_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_talk_other_speaker_item, null);
                break;
        }
        return v;
    }

    @Override
    protected Holder initHolder(View v) {
        Holder holder = new Holder(v);
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.time = (TextView) v.findViewById(R.id.time);
        holder.msgTxt = (TextView) v.findViewById(R.id.msg_txt);
        holder.msgImg = (ImageView) v.findViewById(R.id.msg_img);
        holder.msgImg.setOnClickListener(this);
        return holder;
    }

    @Override
    protected void doRequest() {
        //onSuccess(getTalkList());
        LiveManager.getTalks(mContext, mTicket, mLiveCriteria, mPagination, new APIServiceCallback<CollectionPage<TalkItem>> () {
            @Override
            public void onSuccess(CollectionPage<TalkItem> object) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "获取消息成功", Toast.LENGTH_SHORT).show();
                }
                TalkMsgAdapter.this.onSuccess(object.objectsOfPage);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "获取消息失败", Toast.LENGTH_SHORT).show();
                }
                TalkMsgAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
       Object obj = v.getTag();
        try {
            Integer position = (Integer) obj;
            TalkItem talkItem = getItem(position);

            boolean isText = false;
            String drawingKey = null;
            String base64Txt = null;

            if (talkItem.body != null) {
                if (!TextUtils.isEmpty(talkItem.body.text)) {
                    base64Txt = talkItem.body.text;
                    if (talkItem.body.contentType == Communications.ContentType.TEXT) {
                        isText = true;
                    }
                } else {
                    if (talkItem.body.drawing != null) {
                        drawingKey = talkItem.body.drawing.name;
                    }
                }
            }

            if (!isText && (!TextUtils.isEmpty(base64Txt) || !TextUtils.isEmpty(drawingKey))) {
                if (!TextUtils.isEmpty(base64Txt)) {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(OnImageClickListener.IMG_FROM_BASE64, base64Txt);
                    }
                } else {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(OnImageClickListener.IMG_FROM_QINIU, drawingKey);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    class Holder extends BaseHolder {
        RoundedImageView portrait;
        TextView name;
        TextView time;
        TextView msgTxt;
        ImageView msgImg;

        public Holder(View view) {
            super(view);
        }
    }

    private boolean isMyself(String currAccountId) {
        String accountId = AccountDataManager.getAccountID(mContext);
        return accountId != null && accountId.equals(currAccountId);
    }

    private class LoadBase64ImgTask extends AsyncTask<String, Integer, Bitmap> {
        private ImageView mImg;

        public LoadBase64ImgTask(ImageView img) {
            mImg = img;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (params == null || params.length == 0 || mImg == null) {
                return null;
            }

            String content = params[0];
            if (TextUtils.isEmpty(content)) {
                return null;
            }

            return ClassroomBusiness.base64ToBitmap(content);
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            if (bmp != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mImg.getLayoutParams();
                int w = MAX_SIZE;
                int h = MAX_SIZE;
                if (bmp.getWidth() > bmp.getHeight()) {
                    w = MAX_SIZE;
                    h = (int)((bmp.getHeight() / (float)bmp.getWidth()) * MAX_SIZE);
                } else {
                    h = MAX_SIZE;
                    w = (int)((bmp.getWidth() / (float)bmp.getHeight()) * MAX_SIZE);
                }
                params.width = w;
                params.height = h;
                mImg.setImageBitmap(bmp);
            }
        }
    }
}
