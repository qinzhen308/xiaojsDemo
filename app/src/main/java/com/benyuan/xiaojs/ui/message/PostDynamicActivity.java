package com.benyuan.xiaojs.ui.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.crop.CropImageMainActivity;
import com.benyuan.xiaojs.common.crop.CropImagePath;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;

public class PostDynamicActivity extends BaseActivity {

    @BindView(R.id.pic_thumbnail)
    ImageView thumbnailView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_post_dynamic);
        setMiddleTitle(R.string.post_dyn);
        setRightText(R.string.post);
    }

    @OnClick({R.id.left_image,R.id.chose_pic,R.id.chose_at,R.id.btn_level})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.chose_pic:
                Intent i = new Intent(this, CropImageMainActivity.class);
                startActivityForResult(i, 1);
                break;
            case R.id.btn_level:
                startActivity(new Intent(this, SecurityLevelActivity.class));
                break;
            case R.id.chose_at:
                startActivity(new Intent(this, ChoiceContactActivity.class));
                break;

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (data != null) {
                    String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                    if (cropImgPath != null){
                       Glide.with(this).load(cropImgPath).into(thumbnailView);

                    }
                }
                break;
        }
    }
}
