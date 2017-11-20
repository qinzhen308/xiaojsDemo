package cn.xiaojs.xma.ui.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.common.ImageViewActivity;
import cn.xiaojs.xma.ui.share.sharetemplate.BaseShareTemplateView;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareRLessonTemplateView;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareTemplate1View;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareTemplate2View;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareTemplate3View;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.ShareUtil;

/**
 * Created by Paul Z on 2017/8/1.
 */

public class ShareQrcodeRLessonActivity extends BaseShareQrcodeActivity {

    public static final String EXTRA_QRCODE_TYPE = "qr_type";
    public static final String EXTRA_KEY_TITLE = "extra_key_title";
    public static final String EXTRA_KEY_ID = "extra_key_id";
    public static final String EXTRA_KEY_TEACHER = "extra_key_teacher";
    public static final String EXTRA_KEY_OTHER_INFO_PREFIX = "extra_key_other_info_";

    public static final int TYPE_CLASS = 0;
    public static final int TYPE_STANDALONG_LESSON = 1;
    public static final int TYPE_RECORDED_LESSON = 2;

    String title;
    String id;
    Account teacher;
    @BindView(R.id.layout_content)
    ShareRLessonTemplateView layoutContent;


    String[] shareTypes = {
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.CLASS_LESSON,
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.STAND_ALONE_LESSON,
            Social.SearchType.COURSE};
    String shareUrl;

    @Override
    protected void addViewContent() {
        setExtra();
        addView(R.layout.activity_share_recorded_lesson);
        setMiddleTitle(getString(R.string.share));
        setRightImage(R.drawable.ic_class_database_share_1);
        shareUrl = ApiManager.getShareLessonUrl(id, Social.SearchType.COURSE);
    }

    boolean setViewHeight = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && setViewHeight) {
            setViewHeight = false;
            initView();
        }
    }


    private void bindInfo(){
        layoutContent.setClassName(title);
        layoutContent.setTeacherName(getAdviserName());
        layoutContent.setExpireDate("有效期："+getIntent().getStringExtra(EXTRA_KEY_OTHER_INFO_PREFIX+0));
        Glide.with(this)
                .load(cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(getAdviserId(),300))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        layoutContent.setTeacherAvatar(resource);
                    }
                });
        layoutContent.setBackgroundResource(R.drawable.bg_share_recorded_lesson);
    }

    private String getAdviserName(){
        return teacher.getBasic()!=null&&!TextUtils.isEmpty(teacher.getBasic().getName())?teacher.getBasic().getName():teacher.name;
    }


    private String getAdviserId(){
        return teacher==null?null:teacher.getId();
    }


    @OnClick({R.id.left_image, R.id.right_image,R.id.layout_content})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image:
                sharePicture();
                break;
            case R.id.layout_content:
                enterViewer();
                break;
        }


    }

    private void setExtra() {
        Intent data = getIntent();
        title = data.getStringExtra(EXTRA_KEY_TITLE);
        id = data.getStringExtra(EXTRA_KEY_ID);

        teacher=(Account) data.getSerializableExtra(EXTRA_KEY_TEACHER);
    }

    public void initView() {

        bindInfo();
        new Thread() {
            @Override
            public void run() {
                final Bitmap bm = generateQRBitMap(shareUrl, layoutContent.getQrCodeSize());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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


    private void enterViewer() {

        layoutContent.setDrawingCacheEnabled(true);
        Bitmap bm = layoutContent.getDrawingCache();
        bm = bm.createBitmap(bm);
        layoutContent.setDrawingCacheEnabled(false);
        String previewFile=BitmapUtils.saveSharePreviewToFile(bm);
        ArrayList<String> imgs=new ArrayList<String>();
        imgs.add(previewFile);
        ImageViewActivity.invoke(this,"",imgs,true);
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


    public static void invoke(Context context, int type, String id, String title, Account teacher, Serializable... otherInfo) {
        Intent intent = new Intent(context, ShareQrcodeRLessonActivity.class);
        intent.putExtra(EXTRA_QRCODE_TYPE, type);
        intent.putExtra(EXTRA_KEY_TEACHER, teacher);
        intent.putExtra(EXTRA_KEY_TITLE, title);
        intent.putExtra(EXTRA_KEY_ID, id);
        for(int i=0;i<otherInfo.length;i++){
            intent.putExtra(EXTRA_KEY_OTHER_INFO_PREFIX+i, otherInfo[i]);
        }
        context.startActivity(intent);
    }


}
