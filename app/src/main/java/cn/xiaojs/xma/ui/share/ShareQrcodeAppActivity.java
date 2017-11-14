package cn.xiaojs.xma.ui.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.common.ImageViewActivity;
import cn.xiaojs.xma.ui.share.sharetemplate.ShareAppTemplateView;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.ShareUtil;

/**
 * Created by Paul Z on 2017/8/1.
 */

public class ShareQrcodeAppActivity extends BaseActivity {


    @BindView(R.id.layout_content)
    ShareAppTemplateView layoutContent;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_app);
        setMiddleTitle("分享App");
        setRightImage(R.drawable.ic_class_database_share_1);
        initView();
    }

    private void bindInfo(){

        layoutContent.setBackgroundResource(R.drawable.img_xiaojsapp);
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


    public void initView() {

        bindInfo();

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


    public static void invoke(Context context) {
        Intent intent = new Intent(context, ShareQrcodeAppActivity.class);
        context.startActivity(intent);
    }


}
