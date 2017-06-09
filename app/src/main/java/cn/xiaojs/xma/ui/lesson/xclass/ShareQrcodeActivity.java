package cn.xiaojs.xma.ui.lesson.xclass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.OtherDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.QRCodeData;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by maxiaobao on 2017/5/19.
 */

public class ShareQrcodeActivity extends BaseActivity {

    public static final String EXTRA_QRCODE_TYPE = "qr_type";
    public static final String EXTRA_CLASS_NAME = "extra_title";
    public static final int CLIENT_DOWNLOAD_QRCODE = 0x1;
    public static final int CLASS_QRCODE = 0x2;


    @BindView(R.id.qrcode_img)
    ImageView qrcodeView;
    @BindView(R.id.qrcode_tips)
    TextView tipsView;
    @BindView(R.id.qrcode_title)
    TextView qrcodeTitle;

    private int qrcodeType;
    String imgUrl;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_qrcode);

        qrcodeType = getIntent().getIntExtra(EXTRA_QRCODE_TYPE, CLASS_QRCODE);
        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
            setMiddleTitle(getString(R.string.client_download_qrcode));
        } else {
            setMiddleTitle(getString(R.string.qr_code));
        }

        initView();

    }

    boolean setViewHeight = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && setViewHeight) {
            setViewHeight = false;

            if (qrcodeType == CLASS_QRCODE) {
                getQR(getIntent().getStringExtra(ClassInfoActivity.EXTRA_CLASSID), 10);
            }
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

    public void initView() {
        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
            tipsView.setText(R.string.client_download_qrcode_tips);
            qrcodeView.setImageResource(R.drawable.xjs_app_qrcode);
        } else {
            ViewGroup.LayoutParams lp = qrcodeView.getLayoutParams();
            int with = ScreenUtils.getScreenWidth(this) / 2;
            lp.height = lp.width = with;
            qrcodeView.setLayoutParams(lp);
            tipsView.setText(R.string.class_qrcode_tips);
            String title=getIntent().getStringExtra(EXTRA_CLASS_NAME);
            if(!TextUtils.isEmpty(title)){
                qrcodeTitle.setVisibility(View.VISIBLE);
                qrcodeTitle.setText(title);
            }
            //Bitmap bitmap = generateQRBitMap("class");
            //qrcodeView.setImageBitmap(bitmap);
//            Glide.with(this).load(XiaojsConfig.APP_QRCODE_IMG_BASE_URL+imgUrl).into(qrcodeView);
        }
    }

    private void sharePicture() {

        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
            Bitmap shareBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.xjs_app_qrcode);

            ShareUtil.shareImage(this, shareBitmap, XiaojsConfig.APP_QRCODE_URL, getString(R.string.client_download_qrcode));
        } else if (qrcodeType == CLASS_QRCODE) {
            ShareUtil.shareByUmeng(this, XiaojsConfig.APP_QRCODE_IMG_BASE_URL + imgUrl, getString(R.string.client_download_qrcode));
        }


    }


    private void savePicture() {
        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {

            Bitmap shareBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.xjs_app_qrcode);

            String fileName = new StringBuilder("xjs_download_")
                    .append(System.currentTimeMillis())
                    .append(".jpg")
                    .toString();

            toSave(shareBitmap, fileName);
        } else {

            Glide.with(this).load(XiaojsConfig.APP_QRCODE_IMG_BASE_URL + imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        String fileName = new StringBuilder("xjs_download_")
                                .append(System.currentTimeMillis())
                                .append(".jpg")
                                .toString();
                        toSave(resource, fileName);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), "保存失败");
                    }
                }
            });

        }
    }


    private void toSave(Bitmap bitmap, String fileName) {

        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (BitmapUtils.saveImageToGallery(this, bitmap, fileDir, fileName, 100, true)) {

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


    private Bitmap generateQRBitMap(final String content) {

        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints);

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

    private void getQR(final String classid) {
        OtherDataManager.getQRCodeImg(this, classid, new APIServiceCallback<QRCodeData>() {
            @Override
            public void onSuccess(QRCodeData object) {
                Glide.with(ShareQrcodeActivity.this).load(XiaojsConfig.APP_QRCODE_IMG_BASE_URL + object.url).into(qrcodeView);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }


    private void getQR(final String classid, final int size) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://myxjs.tunnel.2bdata.com/xjsweixin/web/index.php?r=public/getimgcode");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setUseCaches(false);
                    DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                    String p1 = "code=" + classid;
                    String p2 = "size=" + size;
                    Logger.d("----qz----QR---id=" + classid);
                    dos.writeBytes(p1 + "&" + p2);
//                    dos.writeBytes(p2);
                    dos.flush();
                    dos.close();
                    int resultCode = connection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == resultCode) {
                        StringBuffer sb = new StringBuffer();
                        String readLine = new String();
                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        while ((readLine = responseReader.readLine()) != null) {
                            sb.append(readLine).append("\n");
                        }
                        responseReader.close();
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        imgUrl = jsonObject.getString("url");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(ShareQrcodeActivity.this).load(XiaojsConfig.APP_QRCODE_IMG_BASE_URL + imgUrl).into(qrcodeView);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
