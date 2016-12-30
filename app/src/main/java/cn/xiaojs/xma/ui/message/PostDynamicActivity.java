package cn.xiaojs.xma.ui.message;

import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.base.BaseActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class PostDynamicActivity extends BaseActivity {

    public static final int REQUEST_PIC_CODE = 0x3;
    public static final int REQUEST_SHARE_SCOPE_CODE = 0x4;
    public static final int REQUEST_AT_CODE = 0x5;

    @BindView(R.id.pic_thumbnail)
    ImageView thumbnailView;

    @BindView(R.id.input_edit)
    EditText editText;

    @BindView(R.id.btn_level)
    Button scopeButton;

    private DynPost.Audience audience;
    private ArrayList<String> mentioneds;
    private int checkedIndex = 0;
    private ArrayList<Contact> atContacts;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_post_dynamic);
        setMiddleTitle(R.string.post_dyn);
        setRightText(R.string.post);
    }

    @OnClick({R.id.left_image, R.id.chose_pic, R.id.chose_at, R.id.btn_level, R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.chose_pic:
                Intent i = new Intent(this, CropImageMainActivity.class);
                startActivityForResult(i, REQUEST_PIC_CODE);
                break;
            case R.id.btn_level:
                Intent intent = new Intent(this, ShareScopeActivity.class);
                intent.putExtra(ShareScopeActivity.CHOOSE_INDEX,checkedIndex);

                startActivityForResult(intent,REQUEST_SHARE_SCOPE_CODE);
                break;
            case R.id.chose_at:

                startActivityForResult(new Intent(this, ChoiceContactActivity.class),
                        REQUEST_AT_CODE);
                break;
            case R.id.right_image:
                postDynamic();
                break;

        }

    }

    private void showAt() {


        if (atContacts == null)
            return;

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        if (mentioneds == null){
            mentioneds = new ArrayList<>(atContacts.size());
        }

        //FIXME 如果用户删除了之前的@,需要将mentioneds中的account也要删除。

        for (Contact contact : atContacts) {
            ssb.append("@").append(contact.alias).append(" ");
            mentioneds.add(contact.account);
        }

        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.input_at));
        ssb.setSpan(span, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        editText.append(ssb);

    }


    private void postDynamic() {

        String postText = editText.getText().toString();
        if (TextUtils.isEmpty(postText)) {
            Toast.makeText(this, R.string.post_dyn_none, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        DynPost dynPost = new DynPost();
        dynPost.text = postText;

        if (audience !=null && audience.type != Social.ShareScope.PUBLIC){
            dynPost.audience = audience;
        }

        if (mentioneds != null){
            dynPost.mentioned = mentioneds;
        }

        SocialManager.postActivity(this, dynPost, new APIServiceCallback<Dynamic>() {
            @Override
            public void onSuccess(Dynamic object) {

                cancelProgress();

                Toast.makeText(PostDynamicActivity.this, R.string.post_dyn_ok, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();

                Toast.makeText(PostDynamicActivity.this, errorMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_PIC_CODE) {
                if (data != null) {
                    String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                    if (cropImgPath != null) {
                        Glide.with(this).load(cropImgPath).into(thumbnailView);
                    }
                }

            } else if (requestCode == REQUEST_SHARE_SCOPE_CODE) {

                audience = data.getParcelableExtra(ShareScopeActivity.CHOOSE_DATA);
                checkedIndex = data.getIntExtra(ShareScopeActivity.CHOOSE_INDEX,0);

                updateScope();
            } else if (requestCode == REQUEST_AT_CODE) {
                atContacts = data.getParcelableArrayListExtra(
                        ChoiceContactActivity.CHOOSE_CONTACT_EXTRA);
                showAt();
            }
        }

    }

    private void updateScope() {
        String name = getScopeName();
        scopeButton.setText(name);
    }

    private String getScopeName() {

        int type = audience.type;

        switch (type) {
            case Social.ShareScope.FRIENDS:
                return "仅好友可见";
            case Social.ShareScope.CLASSES:
                return "班级圈可见";
            case Social.ShareScope.SPECIFIC:

                ArrayList<Doc> choosen = audience.chosen;
                int num = choosen == null ? 0 : choosen.size();

                return String.format(getResources().getString(R.string.num_can_see), num);
            default:
                return "所有人可见";
        }

    }
}
