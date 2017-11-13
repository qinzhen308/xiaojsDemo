package cn.xiaojs.xma.ui.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.view.sharetemplate.BaseShareTemplateView;
import cn.xiaojs.xma.ui.view.sharetemplate.ShareTemplate1View;
import cn.xiaojs.xma.ui.view.sharetemplate.ShareTemplate2View;
import cn.xiaojs.xma.ui.view.sharetemplate.ShareTemplate3View;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.ShareUtil;

/**
 * Created by Paul Z on 2017/8/1.
 */

public class ShareQrcodeClassroomActivity extends BaseActivity {

    public static final String EXTRA_QRCODE_TYPE = "qr_type";
    public static final String EXTRA_KEY_TITLE = "extra_key_title";
    public static final String EXTRA_KEY_ID = "extra_key_id";
    public static final String EXTRA_KEY_TEACHER = "extra_key_teacher";
    public static final String EXTRA_KEY_OTHER_INFO_PREFIX = "extra_key_other_info_";

    public static final int TYPE_CLASS = 0;
    public static final int TYPE_STANDALONG_LESSON = 1;
    public static final int TYPE_RECORDED_LESSON = 2;
    /*  @BindView(R.id.vp_style)
      ViewPager vpStyle;*/
    @BindView(R.id.vp_style)
    LinearLayout layoutStyle;
    String imgUrl;

    String title;
    String id;
    Attendee teacher;

    BaseShareTemplateView layoutContent;
    BaseShareTemplateView[] templateViews=null;

    private int curTemplate=TEMPLATE_style_1;
    private static final int TEMPLATE_style_1=0;
    private static final int TEMPLATE_style_2=1;
    private static final int TEMPLATE_style_3=2;

    Bitmap bmQrCode;

    private int[] style1PicRes = {R.drawable.bg_share_class_1, R.drawable.bg_share_class_2, R.drawable.bg_share_class_2, R.drawable.bg_share_class_3, R.drawable.bg_share_class_4};


    String[] shareTypes = {
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.CLASS_LESSON,
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.STAND_ALONE_LESSON,
            Social.SearchType.COURSE};
    String shareUrl;

    @Override
    protected void addViewContent() {
        setExtra();
        addView(R.layout.activity_share_classroom);
        setMiddleTitle(getString(R.string.share_class));
        setRightText(R.string.share_mode_url);
        shareUrl = ApiManager.getShareLessonUrl(id, cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.CLASS_LESSON);
        templateViews=new BaseShareTemplateView[]{new ShareTemplate1View(this),new ShareTemplate2View(this),new ShareTemplate3View(this)};
        changeTemplate(curTemplate);
    }

    boolean setViewHeight = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && setViewHeight) {
            setViewHeight = false;
            initView();
        }
    }

    private void changeTemplate(int template){
        curTemplate=template;
        layoutStyle.removeAllViews();
        layoutContent=templateViews[curTemplate];
        layoutStyle.addView(layoutContent);
        bindInfo();
    }

    private void bindInfo(){
        layoutContent.setClassName(title);
        layoutContent.setTeacherName(getAdviserName());
        layoutContent.setTeacherDescrib(getAdviserDescrib());
        Glide.with(this)
                .load(cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(getAdviserId(),300))
                .asBitmap()
                .error(R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        layoutContent.setTeacherAvatar(resource);
                    }
                });

        layoutContent.setQRCodeImage(bmQrCode);

    }

    private String getAdviserName(){
        return "";
    }

    private String getAdviserDescrib(){
        return "";
    }

    private String getAdviserId(){
        return "";
    }


    @OnClick({R.id.left_image, R.id.btn_change_img, R.id.btn_change_style, R.id.right_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;

            case R.id.btn_change_img:
                break;
            case R.id.btn_change_style:
                break;
            case R.id.right_view:
                break;
        }


    }

    private void setExtra() {
        Intent data = getIntent();
        title = data.getStringExtra(EXTRA_KEY_TITLE);
        id = data.getStringExtra(EXTRA_KEY_ID);
        teacher= ClassroomEngine.getEngine().getClassAdviser();
    }

    public void initView() {

/*//        layoutContent.setBackgroundResource(style1PicRes[0]);
//        layoutContent.setBackgroundResource(R.drawable.template1_picture);
        layoutContent.setBackgroundResource(R.drawable.template2_picture);
        layoutContent.setTeacherAvatar(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_avator));
        layoutContent.setQRCodeImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_avator));
        layoutContent.setClassName("大撒但是睡得速度闪躲睡得睡得睡得是");
        layoutContent.setTeacherName("飘飘龙");
        layoutContent.setTeacherDescrib("飘飘龙阿红撒了；按时拉伸了阿斯顿");*/

        new Thread() {
            @Override
            public void run() {
                final Bitmap bm = generateQRBitMap(shareUrl, layoutContent.getQrCodeSize());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bmQrCode=bm;
                        layoutContent.setQRCodeImage(bm);
                    }
                });
            }
        }.start();
    }

    private void sharePicture() {
        layoutContent.setDrawingCacheEnabled(true);
        Bitmap bm = layoutContent.getDrawingCache();
        bm = bm.createBitmap(bm);
        layoutContent.setDrawingCacheEnabled(false);
        if (bm != null) {
            ShareUtil.shareByUmeng(this, bm, "");
        }
    }


    private void savePicture() {

        layoutContent.setDrawingCacheEnabled(true);
        Bitmap bm = layoutContent.getDrawingCache();
        bm = bm.createBitmap(bm);
        layoutContent.setDrawingCacheEnabled(false);
        if (bm != null) {
            String fileName = new StringBuilder("xjs_download_")
                    .append(System.currentTimeMillis())
                    .append(".png")
                    .toString();
            toSave(bm, fileName);
        }

//        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
//
//            Bitmap shareBitmap = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.xjs_app_qrcode);
//
//            String fileName = new StringBuilder("xjs_download_")
//                    .append(System.currentTimeMillis())
//                    .append(".jpg")
//                    .toString();
//
//            toSave(shareBitmap, fileName);
//        } else {
//
//            Glide.with(this).load(imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    if (resource != null) {
//                        String fileName = new StringBuilder("xjs_download_")
//                                .append(System.currentTimeMillis())
//                                .append(".jpg")
//                                .toString();
//                        toSave(resource, fileName);
//                    } else {
//                        ToastUtil.showToast(getApplicationContext(), "保存失败");
//                    }
//                }
//            });
//
//        }
    }


    private void toSave(Bitmap bitmap, String fileName) {

        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (BitmapUtils.savePngImageToGallery(this, bitmap, fileDir, fileName, 100, true)) {

            String tips = getString(R.string.save_picture_ok_tips,
                    fileDir.getAbsolutePath(),
                    fileName);
            Toast.makeText(this, tips, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this,
                    getString(R.string.save_picture_error_tips),
                    Toast.LENGTH_SHORT)
                    .show();
        }

    }


    private Bitmap generateQRBitMap(final String content, int size) {

        Map<EncodeHintType, Object> hints = new HashMap<>();

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void invoke(Context context, String id, String title, Account teacher, Serializable... otherInfo) {
        Intent intent = new Intent(context, ShareQrcodeClassroomActivity.class);
        intent.putExtra(EXTRA_KEY_TEACHER, teacher);
        intent.putExtra(EXTRA_KEY_TITLE, title);
        intent.putExtra(EXTRA_KEY_ID, id);
        for (int i = 0; i < otherInfo.length; i++) {
            intent.putExtra(EXTRA_KEY_OTHER_INFO_PREFIX + i, otherInfo[i]);
        }
        context.startActivity(intent);
    }


}
