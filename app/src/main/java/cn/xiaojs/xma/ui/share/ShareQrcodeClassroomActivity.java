package cn.xiaojs.xma.ui.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.common.ImageViewActivity;
import cn.xiaojs.xma.ui.share.sharetemplate.BaseShareTemplateView;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareTemplate1View;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareTemplate2View;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareTemplate3View;
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
    Contact teacher;

    BaseShareTemplateView layoutContent;
    BaseShareTemplateView[] templateViews=null;

    private int curTemplate=TEMPLATE_style_1;
    private static final int TEMPLATE_style_1=0;
    private static final int TEMPLATE_style_2=1;
    private static final int TEMPLATE_style_3=2;

    Bitmap bmQrCode;

    private int[] style1PicRes = {R.drawable.bg_share_class_1, R.drawable.bg_share_class_2, R.drawable.bg_share_class_3, R.drawable.bg_share_class_4};
    private int curStyle1PicResIndex=0;
    private Bitmap bmTemplate2Pic;
    private Bitmap bmTemplate3Pic;

    String[] shareTypes = {
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.CLASS_LESSON,
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.STAND_ALONE_LESSON,
            Social.SearchType.COURSE};
    String shareUrl;

    @Override
    protected void addViewContent() {
        setExtra();
        addView(R.layout.activity_share_classroom);
        setMiddleTitle(getString(R.string.qr_code));
        setRightImage(R.drawable.ic_class_database_share_1);
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
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        layoutContent.setTeacherAvatar(resource);
                    }
                });

        layoutContent.setQRCodeImage(bmQrCode);

        if(curTemplate==TEMPLATE_style_1){
            layoutContent.setBackgroundResource(style1PicRes[curStyle1PicResIndex]);
        }else if(curTemplate==TEMPLATE_style_2){
            if(bmTemplate2Pic==null){
                layoutContent.setBackgroundResource(R.drawable.template1_picture);
            }else {
                layoutContent.setBackgroundDrawable(new BitmapDrawable(bmTemplate2Pic));
            }
        }else if(curTemplate==TEMPLATE_style_3){
            if(bmTemplate3Pic==null){
                layoutContent.setBackgroundResource(R.drawable.template2_picture);
            }else {
                layoutContent.setBackgroundDrawable(new BitmapDrawable(bmTemplate3Pic));
            }
        }

    }

    private String getAdviserName(){
        return teacher==null?null:teacher.owner;
    }

    private String getAdviserDescrib(){
        return teacher==null?null:teacher.title;
    }

    private String getAdviserId(){
        return teacher==null?null:teacher.ownerId;
    }


    @OnClick({R.id.left_image, R.id.btn_change_img,R.id.btn_change_img0, R.id.btn_change_style, R.id.btn_change_style0, R.id.right_image,R.id.vp_style})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;

            case R.id.btn_change_img0:
            case R.id.btn_change_img:
                showChangePic();
                break;
            case R.id.btn_change_style0:
            case R.id.btn_change_style:
                showChangeStyleDialog();
                break;
            case R.id.right_image:
                sharePicture();
                break;
            case R.id.vp_style:
                enterViewer();
                break;
        }


    }

    private void setExtra() {
        Intent data = getIntent();
        title = data.getStringExtra(EXTRA_KEY_TITLE);
        id = data.getStringExtra(EXTRA_KEY_ID);
        teacher=DataProvider.getProvider(this).getClassAdviser(id);
    }

    public void initView() {

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

    private void showChangeStyleDialog(){
        final TemplatePickBoard dialog=new TemplatePickBoard(this);
        dialog.setPosition(curTemplate);
        dialog.setOnTemplateChangeListener(new TemplatePickBoard.OnTemplateChangeListener() {
            @Override
            public void onChange(int position) {
                changeTemplate(position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showChangeStyle1BgDialog(){
        final Template1BgPickBoard dialog=new Template1BgPickBoard(this);
        dialog.setPosition(curStyle1PicResIndex);
        dialog.setOnTemplateBgChangeListener(new Template1BgPickBoard.OnTemplateBgChangeListener() {
            @Override
            public void onChange(int position) {
                curStyle1PicResIndex=position;
                layoutContent.setBackgroundResource(style1PicRes[curStyle1PicResIndex]);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showChangePic(){
        if(curTemplate==TEMPLATE_style_1){
            showChangeStyle1BgDialog();
        }else {

        }
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


    public static void invoke(Context context, String id, String title, Serializable... otherInfo) {
        Intent intent = new Intent(context, ShareQrcodeClassroomActivity.class);
        intent.putExtra(EXTRA_KEY_TITLE, title);
        intent.putExtra(EXTRA_KEY_ID, id);
        for (int i = 0; i < otherInfo.length; i++) {
            intent.putExtra(EXTRA_KEY_OTHER_INFO_PREFIX + i, otherInfo[i]);
        }
        context.startActivity(intent);
    }


}
