package cn.xiaojs.xma.ui.message;

import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.base.BaseActivity;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;

public class PostDynamicActivity extends BaseActivity {

    @BindView(R.id.pic_thumbnail)
    ImageView thumbnailView;

    @BindView(R.id.input_edit)
    EditText editText;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_post_dynamic);
        setMiddleTitle(R.string.post_dyn);
        setRightText(R.string.post);
    }

    @OnClick({R.id.left_image,R.id.chose_pic,R.id.chose_at,R.id.btn_level,R.id.right_image})
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
                testAddAt();
                break;
            case R.id.right_image:
                postDynamic();
                break;

        }

    }

    private void testAddAt() {

        String str = "@李小萌";

        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.input_at));
        ssb.setSpan(span,0,str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        editText.append(ssb.append(" "));
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

    private void postDynamic() {

        String postText = editText.getText().toString();
        if(TextUtils.isEmpty(postText)) {
            Toast.makeText(this,R.string.post_dyn_none,Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        DynPost dynPost = new DynPost();
        dynPost.text = postText;
        SocialManager.postActivity(this, dynPost, new APIServiceCallback<Dynamic>() {
            @Override
            public void onSuccess(Dynamic object) {

                cancelProgress();

                Toast.makeText(PostDynamicActivity.this,R.string.post_dyn_ok, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();

                Toast.makeText(PostDynamicActivity.this,errorMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
