package cn.xiaojs.xma.ui.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.ShareUtil;

/**
 * Created by maxiaobao on 2017/5/19.
 */

public class ShareBeautifulQrcodeActivity extends BaseActivity {

    public static final String EXTRA_QRCODE_TYPE = "qr_type";
    public static final String EXTRA_KEY_TITLE = "extra_key_title";
    public static final String EXTRA_KEY_ID = "extra_key_id";
    public static final String EXTRA_KEY_TEACHER = "extra_key_teacher";
    public static final String EXTRA_KEY_OTHER_INFO_PREFIX = "extra_key_other_info_";
    public static final int CLIENT_DOWNLOAD_QRCODE = 0x1;
    public static final int CLASS_QRCODE = 0x2;

    public static final int TYPE_CLASS = 0;
    public static final int TYPE_STANDALONG_LESSON = 1;
    public static final int TYPE_RECORDED_LESSON = 2;

    @BindView(R.id.iv_label)
    ImageView ivLabel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_expired_day)
    TextView tvExpiredDay;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_teacher_name)
    TextView tvTeacherName;
    @BindView(R.id.tv_welcom_tip)
    TextView tvWelcomTip;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tips1)
    TextView tips1;
    @BindView(R.id.tips2)
    TextView tips2;
    @BindView(R.id.layout_content)
    RelativeLayout layoutContent;
    @BindView(R.id.save_btn)
    Button saveBtn;
    @BindView(R.id.share_btn)
    Button shareBtn;


    private int qrcodeType = -1;
    String imgUrl;

    String title;
    String id;
    Account teacher;

    int[] bgs = {R.drawable.bg_qrcode_class, R.drawable.bg_qrcode_lesson, R.drawable.bg_qrcode_recorded_lesson};
    int[] labels = {R.drawable.ic_qrcode_label_class, R.drawable.ic_qrcode_label_lesson, R.drawable.ic_qrcode_label_recorded_lesson};
    String[] shareTypes={
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.CLASS_LESSON,
            cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.STAND_ALONE_LESSON,
            Social.SearchType.COURSE};
    String shareUrl;

    @Override
    protected void addViewContent() {
        setExtra();

        addView(R.layout.activity_share_beautiful_qrcode);

        setMiddleTitle(getString(R.string.qr_code));
        shareUrl = ApiManager.getShareLessonUrl(id, shareTypes[qrcodeType]);
    }

    boolean setViewHeight = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && setViewHeight) {
            setViewHeight = false;
            initView();
        }
    }


    @OnClick({R.id.left_image, R.id.save_btn, R.id.share_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.save_btn:
                //保存到本地
                savePicture();
                break;
            case R.id.share_btn:
                sharePicture();
                break;
        }


    }
    private void setExtra(){
        Intent data=getIntent();
        qrcodeType = getIntent().getIntExtra(EXTRA_QRCODE_TYPE, -1);
        title=data.getStringExtra(EXTRA_KEY_TITLE);
        id=data.getStringExtra(EXTRA_KEY_ID);
        teacher=(Account) data.getSerializableExtra(EXTRA_KEY_TEACHER);
    }

    public void initView() {
        ViewGroup.LayoutParams lp = layoutContent.getLayoutParams();
        lp.width = (int)(layoutContent.getHeight()/1.775f);
        layoutContent.setLayoutParams(lp);

        ivLabel.setImageResource(labels[qrcodeType]);
        layoutContent.setBackgroundResource(bgs[qrcodeType]);
        tvTitle.setText(title);
        tvTeacherName.setText("班主任："+(teacher.getBasic()!=null&&!TextUtils.isEmpty(teacher.getBasic().getName())?teacher.getBasic().getName():teacher.name));
        Glide.with(this)
                .load(cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(teacher.getId(),300))
                .transform(new CircleTransform(this))
                .error(R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .into(ivAvatar);

        if (qrcodeType == TYPE_CLASS) {
            tvDate.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
            tvExpiredDay.setVisibility(View.GONE);
            tips2.setText(R.string.qrcode_tips_class);
            tvWelcomTip.setText(R.string.qrcode_tips_welcom_class);

        } else if (qrcodeType == TYPE_STANDALONG_LESSON) {

            tvDate.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            tvExpiredDay.setVisibility(View.GONE);
            tips2.setText(R.string.qrcode_tips_lesson);
            tvWelcomTip.setText(R.string.qrcode_tips_welcom_lesson);
            tvDate.setText(getIntent().getStringExtra(EXTRA_KEY_OTHER_INFO_PREFIX+0));
            tvTime.setText(getIntent().getStringExtra(EXTRA_KEY_OTHER_INFO_PREFIX+1));
        } else if (qrcodeType == TYPE_RECORDED_LESSON) {
            tvDate.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
            tvExpiredDay.setVisibility(View.VISIBLE);
            tips2.setText(R.string.qrcode_tips_recorded_lesson);
            tvWelcomTip.setText(R.string.qrcode_tips_welcom_lesson);
            tvExpiredDay.setText("有效期："+getIntent().getStringExtra(EXTRA_KEY_OTHER_INFO_PREFIX+0));

        }
        new Thread(){
            @Override
            public void run() {
                final Bitmap bm=generateQRBitMap(shareUrl,ivQrCode.getWidth());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivQrCode.setImageBitmap(bm);
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
        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
            AnalyticEvents.onEvent(this, 28);
        } else if (qrcodeType == CLASS_QRCODE) {

        }
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


    private Bitmap generateQRBitMap(final String content,int size) {

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


    public static void invoke(Context context,int type,String id, String title,Account teacher,String... otherInfo) {
        Intent intent = new Intent(context, ShareBeautifulQrcodeActivity.class);
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
