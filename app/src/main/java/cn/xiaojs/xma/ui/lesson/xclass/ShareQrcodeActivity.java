package cn.xiaojs.xma.ui.lesson.xclass;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.ShareUtil;

/**
 * Created by maxiaobao on 2017/5/19.
 */

public class ShareQrcodeActivity extends BaseActivity {

    public static final String EXTRA_QRCODE_TYPE = "qr_type";
    public static final int CLIENT_DOWNLOAD_QRCODE = 0x1;
    public static final int CLASS_QRCODE = 0x2;


    @BindView(R.id.qrcode_img)
    ImageView qrcodeView;
    @BindView(R.id.qrcode_tips)
    TextView tipsView;

    private int qrcodeType;

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
        }else {
            //Bitmap bitmap = generateQRBitMap("class");
            //qrcodeView.setImageBitmap(bitmap);
        }
    }

    private void sharePicture() {

        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
            Bitmap shareBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.xjs_app_qrcode);

            ShareUtil.shareImage(this,shareBitmap, XiaojsConfig.APP_QRCODE_URL,getString(R.string.client_download_qrcode));
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

        }
    }


    private void toSave(Bitmap bitmap, String fileName) {

        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (BitmapUtils.saveImageToGallery(this, bitmap, fileDir, fileName, 100,true)) {

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

        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    bmp.setPixel(x , y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
