package cn.xiaojs.xma.ui.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.banner.PointIndicateView;
import cn.xiaojs.xma.ui.widget.scaleimage.PhotoView;
import cn.xiaojs.xma.ui.widget.scaleimage.PhotoViewAttacher;
import cn.xiaojs.xma.ui.widget.scaleimage.ScaleViewPager;
import cn.xiaojs.xma.util.CacheUtil;
import cn.xiaojs.xma.util.ToastUtil;


public class ImageViewActivity extends BaseActivity {
    public static final String IMAGE_POSITION_KEY = "imagePositionKey";
    public static final String IMAGE_PATH_KEY = "imagePathKey";

    //如果是从聊天跳转的话，需要传以下参数
    public static final String IMAGE_FROM_CHAT = "image_from_chat";//是否从聊天跳转
    public static final String CHAT_TARGET_ID = "chat_target_id";//如果是单人聊天，对方的id
    public static final String CHAT_GROUP_ID = "chat_group_id";//如果是群组聊天，群组id
    public static final String CHAT_MSG_LIST_ID = "chat_msg_list_id";//原聊天列表中图片消息的id
    public static final String CHAT_APP_KEY = "chat_app_key";//JMessage的appkey
    public static final String CHAT_MESSAGE_ID = "chat_message_id";//点击的message的id

    private ScaleViewPager mPager;
    private PointIndicateView pointContent;

    private List<String> mPathList;

    private boolean mFromChat;
    private int mMessageId;
    private Conversation mConv;
    private final static int PICTURE_PATH_LOADED = 0x2000;
    //存放图片消息的ID
    private List<Integer> mMsgIdList = new ArrayList<Integer>();
    private int mOriginPosition;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_image_viewer);
        needHeader(false);
        init();
    }

    private void init() {
        pointContent = (PointIndicateView) findViewById(R.id.image_pager_points);
        mPager = (ScaleViewPager) findViewById(R.id.image_view_pager);

        Intent intent = getIntent();
        if (intent != null) {
            mFromChat = intent.getBooleanExtra(IMAGE_FROM_CHAT,false);
            if (mFromChat){
                String targetAppKey = intent.getStringExtra(CHAT_APP_KEY);
                mMessageId = intent.getIntExtra(CHAT_MESSAGE_ID, 0);
                long groupId = intent.getLongExtra(CHAT_GROUP_ID, 0);
                if (groupId != 0) {
                    mConv = JMessageClient.getGroupConversation(groupId);
                } else {
                    String targetId = intent.getStringExtra(CHAT_TARGET_ID);
                    if (targetId != null) {
                        mConv = JMessageClient.getSingleConversation(targetId, targetAppKey);
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initImgPathList();
                        handler.sendEmptyMessage(PICTURE_PATH_LOADED);
                    }
                }).start();
            }else {
                mPathList = intent.getStringArrayListExtra(IMAGE_PATH_KEY);
                int position = intent.getIntExtra(IMAGE_POSITION_KEY, 0);
                if (mPathList != null && mPathList.size() != 0) {
                    showImages(position);
                } else {
//				showCancelableMsg(getString(R.string.image_view_error),
//						R.string.error_tip_title);
                }
            }

        }
    }

    private void showImages(int position){
        ImagePagerAdapter adapter = new ImagePagerAdapter(this,
                mPathList);
        mPager.setAdapter(adapter);
        pointContent.setViewPager(mPager, mPathList.size(), position,
                true, null);
    }

    /**
     * 初始化会话中的所有图片路径
     */
    private void initImgPathList() {
        mPathList = new ArrayList<>();
        mMsgIdList = this.getIntent().getIntegerArrayListExtra(CHAT_MSG_LIST_ID);
        cn.jpush.im.android.api.model.Message msg;
        ImageContent ic;
        for (int i = 0 ; i < mMsgIdList.size() ; i++) {
            int msgID = mMsgIdList.get(i);
            if (msgID == mMessageId){
                mOriginPosition = i;
            }
            msg = mConv.getMessage(msgID);
            if (msg.getContentType().equals(ContentType.image)) {
                ic = (ImageContent) msg.getContent();
                if (!TextUtils.isEmpty(ic.getLocalPath())) {
                    mPathList.add(ic.getLocalPath());
                } else {
                    mPathList.add(ic.getLocalThumbnailPath());
                }
            }
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private Context mContext;
        private List<String> mList;

        // private DisplayImageOptions mOptions;

        public ImagePagerAdapter(Context ctx, List<String> data) {
            mContext = ctx;
            mList = data;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_image_viewer, null);
            final PhotoView imageView = (PhotoView) view
                    .findViewById(R.id.drag_image_viewer);

            if (mFromChat){
                Glide.with(mContext)
                        .load(new File(mList.get(position % mList.size())))
                        .asBitmap()
                        .placeholder(R.drawable.default_lesson_cover)
                        .error(R.drawable.default_lesson_cover)
                        .into(imageView);
            }else {
                Glide.with(mContext)
                        .load(Social.getDrawing(mList.get(position % mList.size()),false))
                        .asBitmap()
                        .placeholder(R.drawable.default_lesson_cover)
                        .error(R.drawable.default_lesson_cover)
                        .into(imageView);
            }

            imageView.setClickListener(new PhotoViewAttacher.onClickListener() {
                @Override
                public void onClick() {
                    ListBottomDialog dialog = new ListBottomDialog(mContext);
                    String[] items = new String[]{"保存图片"};
                    dialog.setItems(items);
                    dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                        @Override
                        public void onItemClick(int position) {
                            Drawable drawable = imageView.getDrawable();
                            if (drawable instanceof BitmapDrawable) {
                                final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                Logger.i("bitmap = " + bitmap);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String path = CacheUtil.downloadImage(bitmap);
                                        Message msg = Message.obtain();
                                        msg.obj = path;
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                            }
                        }
                    });
                    dialog.show();
                }
            });
            ((ViewPager) container).addView(view, 0);
            return view;
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj == null) {
                    ToastUtil.showToast(getApplicationContext(),"下载失败!");
                }else {
                    ToastUtil.showToast(getApplicationContext(),"下载成功!保存路径为:" + msg.obj.toString());
                }
            }
        };

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (container != null) {
                ((ViewPager) container).removeView((View) object);
            }
        }

        @Override
        public int getCount() {
            if (mList != null && mList.size() > 1) {
                return Integer.MAX_VALUE;
            } else {
                return mList == null ? 0 : mList.size();
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PICTURE_PATH_LOADED:
                    showImages(mOriginPosition);
                    break;
            }
        }
    };
}
