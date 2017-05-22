package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
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

        qrcodeType = getIntent().getIntExtra(EXTRA_QRCODE_TYPE,CLASS_QRCODE);
        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
            setMiddleTitle(getString(R.string.client_download_qrcode));
        }else{
            setMiddleTitle(getString(R.string.qr_code));
        }

        initView();

    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.save_btn:
                //保存到本地
                break;
            case R.id.share_btn:
                //ShareUtil.shareImage(this,);
                break;
        }


    }

    public void initView() {
        if (qrcodeType == CLIENT_DOWNLOAD_QRCODE) {
            tipsView.setText(R.string.client_download_qrcode_tips);
            qrcodeView.setImageResource(R.drawable.xjs_xma_app_qrcode);
        }
    }
}
