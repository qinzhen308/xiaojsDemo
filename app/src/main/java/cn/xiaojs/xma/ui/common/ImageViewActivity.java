package cn.xiaojs.xma.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.banner.PageNumView;
import cn.xiaojs.xma.ui.widget.scaleimage.PhotoView;
import cn.xiaojs.xma.ui.widget.scaleimage.PhotoViewAttacher;
import cn.xiaojs.xma.ui.widget.scaleimage.ScaleViewPager;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.ToastUtil;


public class ImageViewActivity extends BaseActivity {
    private static final int PERMISSION_SAVE_HUGE_IMAGE = 110;
    private static final int PERMISSION_SAVE_NORMAL_IMAGE = 111;

    public static final String IMAGE_POSITION_KEY = "imagePositionKey";
    public static final String IMAGE_PATH_KEY = "imagePathKey";
    public static final String IMAGE_TITLE_KEY = "imageTitleKey";

    //如果是从聊天跳转的话，需要传以下参数
    public static final String IMAGE_FROM_CHAT = "image_from_chat";//是否从聊天跳转
    public static final String EXTRA_IS_LOCAL = "extra_is_local";//是否本地图片路径
    public static final String CHAT_TARGET_ID = "chat_target_id";//如果是单人聊天，对方的id
    public static final String CHAT_GROUP_ID = "chat_group_id";//如果是群组聊天，群组id
    public static final String CHAT_MSG_LIST_ID = "chat_msg_list_id";//原聊天列表中图片消息的id
    public static final String CHAT_APP_KEY = "chat_app_key";//JMessage的appkey
    public static final String CHAT_MESSAGE_ID = "chat_message_id";//点击的message的id

    private ScaleViewPager mPager;
    //private PointIndicateView pointContent;
    private PageNumView pageNumView;

    private List<String> mPathList;

    private boolean isLocalImg;
    private boolean mFromChat;
    private int mMessageId;
    private Conversation mConv;
    private static final String POSITION = "position";
    private static final int PICTURE_PATH_LOADED = 0x2000;
    private static final int DOWNLOAD_ORIGIN_IMAGE_SUCCEED = 1;
    private static final int SAVE_SUCCEED = 12;
    private cn.jpush.im.android.api.model.Message mMsg;

    //存放图片消息的ID
    private List<Integer> mMsgIdList = new ArrayList<Integer>();
    private int mOriginPosition;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_image_viewer);
        needHeader(false);
        init();
    }

    private void init() {
        //pointContent = (PointIndicateView) findViewById(R.id.image_pager_points);
        pageNumView = (PageNumView) findViewById(R.id.text_pager_points);
        mPager = (ScaleViewPager) findViewById(R.id.image_view_pager);
        String title=getIntent().getStringExtra(IMAGE_TITLE_KEY);
        tvTitle.setText(title==null?"":title);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                mMsg = mConv.getMessage(mMsgIdList.get(position % mPathList.size()));
//                ImageContent ic = (ImageContent) mMsg.getContent();
//                if (ic.getLocalPath() == null) {
////                    mLoadBtn.setVisibility(View.VISIBLE);
//                    downloadImage();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            mFromChat = intent.getBooleanExtra(IMAGE_FROM_CHAT, false);
            isLocalImg = intent.getBooleanExtra(EXTRA_IS_LOCAL, false);
            if (mFromChat) {
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
            }else if(isLocalImg){
                mPathList = intent.getStringArrayListExtra(IMAGE_PATH_KEY);
                int position = intent.getIntExtra(IMAGE_POSITION_KEY, 0);
                if (mPathList != null && mPathList.size() != 0) {
                    showImages(position);
                } else {

                }
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

    //每次在聊天界面点击图片或者滑动图片自动下载大图
    private void downloadImage() {
        ImageContent imgContent = (ImageContent) mMsg.getContent();
        if (imgContent.getLocalPath() == null) {
            //如果不存在进度条Callback，重新注册
            if (!mMsg.isContentDownloadProgressCallbackExists()) {
//                mProgressDialog = new ProgressDialog(this);
//                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                mProgressDialog.setCanceledOnTouchOutside(false);
//                mProgressDialog.setIndeterminate(false);
//                mProgressDialog.setMessage(mContext.getString(R.string.downloading_hint));
//                mDownloading = true;
//                mProgressDialog.show();
                // 显示下载进度条
                mMsg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {

                    @Override
                    public void onProgressUpdate(double progress) {
//                        android.os.Message msg = mUIHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        if (!(progress < 1.0)) {

                        }
                    }
                });
                // msg.setContent(imgContent);
                imgContent.downloadOriginImage(mMsg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int status, String desc, File file) {
                        if (status == 0) {
                            Message msg = handler.obtainMessage();
                            msg.what = DOWNLOAD_ORIGIN_IMAGE_SUCCEED;
                            Bundle bundle = new Bundle();
                            bundle.putString("path", file.getAbsolutePath());
                            bundle.putInt(POSITION, mPager.getCurrentItem() % mPathList.size());
                            msg.setData(bundle);
                            msg.sendToTarget();
                        }
                    }
                });
            }
        }
    }

    private void showImages(int position) {
        ImagePagerAdapter adapter = new ImagePagerAdapter(this,
                mPathList);
        mPager.setAdapter(adapter);
//        pointContent.setViewPager(mPager, mPathList.size(), position,
//                true, null);
        pageNumView.setViewPager(mPager, mPathList.size(), position);
        if(mPathList.size()==1){
            pageNumView.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化会话中的所有图片路径
     */
    private void initImgPathList() {
        mPathList = new ArrayList<>();
        mMsgIdList = this.getIntent().getIntegerArrayListExtra(CHAT_MSG_LIST_ID);
        cn.jpush.im.android.api.model.Message msg;
        ImageContent ic;
        for (int i = 0; i < mMsgIdList.size(); i++) {
            int msgID = mMsgIdList.get(i);
            if (msgID == mMessageId) {
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

    @OnClick(R.id.iv_back)
    public void pageBack() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        public Object instantiateItem(final ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_image_viewer, null);
            final View loadState = view.findViewById(R.id.load_state);
            final PhotoView imageView = (PhotoView) view.findViewById(R.id.drag_image_viewer);
            final SubsamplingScaleImageView hugeImageView = (SubsamplingScaleImageView) view.findViewById(R.id.hurge_image_viewer);

            if (mFromChat) {
                Glide.with(mContext)
                        .load(new File(mList.get(position % mList.size())))
                        .asBitmap()
                        //.placeholder(R.drawable.ic_home_picture)
                        .error(R.drawable.ic_home_picture)
                        .into(imageView);
            }else if(isLocalImg){
                /*Glide.with(mContext)
                        .load(new File(mList.get(position % mList.size())))
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.placeholder(R.drawable.ic_home_picture)
                        .error(R.drawable.ic_home_picture)
                        .into(imageView);*/
                imageView.setImageURI(Uri.fromFile(new File(mList.get(position % mList.size()))));
            }else {
//                Glide.with(mContext)
//                        .load(Social.getDrawing(mList.get(position % mList.size()),false))
//                        //.placeholder(R.drawable.ic_home_picture)
//                        .fitCenter()
//                        .error(R.drawable.ic_home_picture)
//                        .into(imageView);
                Glide.with(mContext)
                        .load(Social.getDrawing(mList.get(position % mList.size()), false))
                        //.placeholder(R.drawable.ic_home_picture)
                        .downloadOnly(new SimpleTarget<File>() {
                            @Override
                            public void onResourceReady(final File resource, GlideAnimation<? super File> glideAnimation) {
                                Logger.d("----glide imageviewer-----path=" + resource.getPath());
                                Point point = BitmapUtils.getImageSize(resource.getPath());
                                int screenH = ScreenUtils.getScreenHeight(ImageViewActivity.this);
                                int screenW = ScreenUtils.getScreenWidth(ImageViewActivity.this);
                                if (point.x > screenW * 2 || point.y > screenH * 2 || point.y > 2500 || point.x > 2500) {
                                    imageView.setVisibility(View.GONE);
                                    hugeImageView.setVisibility(View.VISIBLE);
//                                    if(point.x>point.y){
//                                        hugeImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_90);
//                                    }else {
//                                        hugeImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_0);
//                                    }
                                    float scale = 1f;
                                    if (screenH < point.y || screenW < point.x) {
                                        scale = 0.9f * screenW / (float) point.x;
                                    }
                                    hugeImageView.setImage(ImageSource.uri(resource.getPath()), new ImageViewState(
                                            scale,
                                            new PointF(0, 0),
                                            point.x > point.y ? SubsamplingScaleImageView.ORIENTATION_0 : SubsamplingScaleImageView.ORIENTATION_0));
                                    hugeImageView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            saveHugeImage(resource);
                                            return true;
                                        }
                                    });
                                    hugeImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                           showTitleBar();
                                        }
                                    });
                                    loadState.setVisibility(View.GONE);
                                } else {
                                    imageView.setVisibility(View.VISIBLE);
                                    hugeImageView.setVisibility(View.GONE);
                                    Glide.with(mContext)
                                            .load(Social.getDrawing(mList.get(position % mList.size()), false))
                                            //.placeholder(R.drawable.ic_home_picture)
                                            .fitCenter()
                                            .error(R.drawable.ic_home_picture)
                                            .into(imageView);
                                }
                            }
                        });

            }

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    ListBottomDialog dialog = new ListBottomDialog(mContext);
                    String[] items = new String[]{"保存图片"};
                    dialog.setItems(items);
                    dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                        @Override
                        public void onItemClick(int position) {
                            if (PermissionUtil.isOverMarshmallow() && ActivityCompat.checkSelfPermission(ImageViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                PermissionGen.needPermission(ImageViewActivity.this, PERMISSION_SAVE_NORMAL_IMAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            } else {
                                doSaveNormalImage(imageView.getDrawable());
                            }
                        }
                    });
                    dialog.show();

                    return false;
                }
            });

            imageView.setClickListener(new PhotoViewAttacher.onClickListener() {
                @Override
                public void onClick() {
                    showTitleBar();
                }
            });
            ((ViewPager) container).addView(view, 0);
            return view;
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj == null) {
                    ToastUtil.showToast(getApplicationContext(), "下载失败!");
                } else {
                    ToastUtil.showToast(getApplicationContext(), "下载成功!保存路径为:" + msg.obj.toString());
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
//            if (mList != null && mList.size() > 1) {
//                return Integer.MAX_VALUE;
//            } else {
//                return mList == null ? 0 : mList.size();
//            }
            return mList == null ? 0 : mList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PICTURE_PATH_LOADED:
                    showImages(mOriginPosition);
                    break;
                case DOWNLOAD_ORIGIN_IMAGE_SUCCEED:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        mPathList.set(bundle.getInt(POSITION), bundle.getString("path"));
                        mPager.getAdapter().notifyDataSetChanged();
                    }
                    break;
                case SAVE_SUCCEED:
                    ToastUtil.showToast(getApplicationContext(), "保存图片到" + msg.obj);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void saveHugeImage(final File file) {
        ListBottomDialog dialog = new ListBottomDialog(this);
        String[] items = new String[]{"保存图片"};
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                if (PermissionUtil.isOverMarshmallow() && ActivityCompat.checkSelfPermission(ImageViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    PermissionGen.needPermission(ImageViewActivity.this, PERMISSION_SAVE_HUGE_IMAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    doSaveHugeImage(file);
                }
            }
        });
        dialog.show();
    }


    void doSaveNormalImage(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Logger.i("bitmap = " + bitmap);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = BitmapUtils.savePngImageToGallery(ImageViewActivity.this, bitmap);
                    Message msg = Message.obtain();
                    msg.obj = path;
                    msg.what = SAVE_SUCCEED;
                    handler.sendMessage(msg);
                }
            }).start();
        } else if (drawable instanceof GlideBitmapDrawable) {
            final Bitmap bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            Logger.i("bitmap = " + bitmap);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = BitmapUtils.savePngImageToGallery(ImageViewActivity.this, bitmap);
                    Message msg = Message.obtain();
                    msg.obj = path;
                    msg.what = SAVE_SUCCEED;
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }


    void doSaveHugeImage(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = BitmapUtils.copyImgFileToGallery(ImageViewActivity.this, file);
                if (!TextUtils.isEmpty(path)) {
                    Message msg = Message.obtain();
                    msg.obj = path;
                    msg.what = SAVE_SUCCEED;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    @PermissionRationale(requestCode = PERMISSION_SAVE_NORMAL_IMAGE)
    void normalRationale() {
        PermissionHelper.showRationaleDialog(this, getString(R.string.permission_rationale_storage_tip));
    }

    @PermissionRationale(requestCode = PERMISSION_SAVE_HUGE_IMAGE)
    void hugeRationale() {
        PermissionHelper.showRationaleDialog(this, getString(R.string.permission_rationale_storage_tip));
    }


    Animation animFade;
    Animation animShow;

    private void showTitleBar() {

        if(titleBar.getVisibility()==View.VISIBLE){
            setHideAnimation(titleBar);
        }else {
            setShowAnimation(titleBar);
        }

    }

    public  void setHideAnimation(final View view) {

        if (null != animFade) {
            animFade.cancel();
        }
        // 监听动画结束的操作
        animFade = new AlphaAnimation(1.0f, 0.0f);
        animFade.setDuration(500);
//        animFade.setFillAfter(true);
        animFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.clearAnimation();
        view.startAnimation(animFade);
    }

    public  void setShowAnimation(final View view) {

        if (null != animShow) {
            animShow.cancel();
        }
        animShow = new AlphaAnimation(0.0f, 1.0f);
        animShow.setDuration(500);
//        animShow.setFillAfter(true);
        animShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.clearAnimation();
        view.startAnimation(animShow);
    }


    public static void invoke(Context context,String title,ArrayList<String> imgs,boolean isLocalImg){
        Intent intent=new Intent(context,ImageViewActivity.class);
        intent.putExtra(IMAGE_TITLE_KEY,title);
        intent.putExtra(EXTRA_IS_LOCAL,isLocalImg);
        if(!ArrayUtil.isEmpty(imgs)){
            intent.putExtra(IMAGE_PATH_KEY,imgs);
        }
        context.startActivity(intent);

    }

}
