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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.R;
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
    private List<String> mPathList;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_image_viewer);
        needHeader(false);
        init();
    }

    private void init() {
        PointIndicateView pointContent = (PointIndicateView) findViewById(R.id.image_pager_points);
        ScaleViewPager mPager = (ScaleViewPager) findViewById(R.id.image_view_pager);

        Intent intent = getIntent();
        if (intent != null) {
            mPathList = intent.getStringArrayListExtra(IMAGE_PATH_KEY);
            int position = intent.getIntExtra(IMAGE_POSITION_KEY, 0);
            if (mPathList != null && mPathList.size() != 0) {
                ImagePagerAdapter adapter = new ImagePagerAdapter(this,
                        mPathList);
                mPager.setAdapter(adapter);
                pointContent.setViewPager(mPager, mPathList.size(), position,
                        true, null);
            } else {
//				showCancelableMsg(getString(R.string.image_view_error),
//						R.string.error_tip_title);
            }
        } else {
//			showCancelableMsg(getString(R.string.image_view_error),
//					R.string.error_tip_title);
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

            Glide.with(mContext)
                    .load(mList.get(position % mList.size()))
                    .asBitmap()
                    .error(R.drawable.default_lesson_cover)
                    .into(imageView);


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
                    ToastUtil.showToast(ImageViewActivity.this,"下载失败!");
                }else {
                    ToastUtil.showToast(ImageViewActivity.this,"下载成功!保存路径为:" + msg.obj.toString());
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
}
