package cn.xiaojs.xma.ui.message;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.colla.UploadReponse;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class PostDynamicActivity extends BaseActivity {

    public static final int REQUEST_PIC_CODE = 0x3;
    public static final int REQUEST_SHARE_SCOPE_CODE = 0x4;
    public static final int REQUEST_AT_CODE = 0x5;
    public static final String KEY_POST_TYPE = "key_post_type";

    public static final String EXTRA_CLASS_POS = "cpos";

    @BindView(R.id.pic_thumbnail)
    ImageView thumbnailView;

    @BindView(R.id.input_edit)
    EditText editText;

    @BindView(R.id.who_can_see)
    TextView scope;
    @BindView(R.id.remind_somebody)
    TextView remind;
    @BindView(R.id.remind_wrapper)
    LinearLayout remindWrapper;

    private DynPost.Audience audience;
    private int checkedIndex = 0;
    private ArrayList<Contact> atContacts;

    private String photoKey;
    private Dimension photoDim;
    private long[] atCheckedPos;
    private int checkClasspos = -1;

    private int specifyScope;

    private ArrayList<Contact> chooseContacts;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_post_dynamic);
        setMiddleTitle(R.string.post_dyn);
        setRightText(R.string.post);
        setRightTextColor(getResources().getColor(R.color.font_orange));

        specifyScope = getIntent().getIntExtra(KEY_POST_TYPE, Social.ShareScope.PUBLIC);
        if (specifyScope == Social.ShareScope.CLASSES) {
            audience = new DynPost.Audience();
            audience.type = specifyScope;
            updateScope();
        }


    }

    @OnClick({R.id.left_image, R.id.post_dynamic_add_picture,
            R.id.remind_somebody_wrapper, R.id.who_can_see_wrapper, R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.post_dynamic_add_picture:
                Intent i = new Intent(this, CropImageMainActivity.class);
                i.putExtra(CropImagePath.CROP_NEVER, true);
                startActivityForResult(i, REQUEST_PIC_CODE);
                break;
            case R.id.who_can_see_wrapper://谁可以见

                if (specifyScope == Social.ShareScope.CLASSES) {
                    return;
                }

                Intent intent = new Intent(this, ShareScopeActivity.class);
                intent.putExtra(ShareScopeActivity.CHOOSE_INDEX, checkedIndex);
                intent.putExtra(EXTRA_CLASS_POS,checkClasspos);

                startActivityForResult(intent, REQUEST_SHARE_SCOPE_CODE);
                break;
            case R.id.remind_somebody_wrapper://提醒谁看

                //TODO when specifyScope equals Social.ShareScope.CLASSES ,start classes activity only

                Intent iat = new Intent(this, CloseFriendActivity.class);
                if (atCheckedPos != null && atCheckedPos.length>0) {
                    iat.putExtra(CloseFriendActivity.CHECKED_POS,atCheckedPos);
                }

                if (audience != null && audience.type == Social.ShareScope.SPECIFIC) {
                    iat.putExtra(CloseFriendActivity.EXTRA_CONTACT,
                            chooseContacts);
                }

                startActivityForResult(iat,REQUEST_AT_CODE);
                break;
            case R.id.right_image:
                postDynamic();
                break;

        }

    }

    private void showAt() {

        if (atContacts == null) {
            clearRemid();
            return;
        }

        StringBuilder builder = new StringBuilder();
        int size = atContacts.size();
        for (int i = 0; i < size; i++) {
//            String display = new StringBuilder(prefix).append(contact.alias).toString();
//
            //editText.addRecipient(contact.account,display);

            Contact c = atContacts.get(i);
            builder.append(c.alias);
            if (i != size-1){
                builder.append(", ");
            }
        }

        remind.setText(builder.toString());
    }

    private void clearRemid() {
        remind.setText("");
        atCheckedPos = null;
        //weizhi
    }


    private void postDynamic() {

        String postText = editText.getText().toString();
        if (TextUtils.isEmpty(postText) && TextUtils.isEmpty(photoKey)) {
            Toast.makeText(this, R.string.post_dyn_none, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        DynPost dynPost = new DynPost();


        if (!TextUtils.isEmpty(postText)) {
            dynPost.text = postText;
        }

        if (!TextUtils.isEmpty(photoKey)) {
            dynPost.drawing = photoKey;
        }

        if (photoDim != null) {
            dynPost.dimension = photoDim;
        }

        if (audience != null && audience.type != Social.ShareScope.PUBLIC) {
            dynPost.audience = audience;
        }

//        List<String> mentioneds = editText.getRecipientIds();
//        if (mentioneds != null) {
//            dynPost.mentioned = mentioneds;
//        }

        SocialManager.postActivity(this, dynPost, new APIServiceCallback<Dynamic>() {
            @Override
            public void onSuccess(Dynamic object) {

                cancelProgress();

                Toast.makeText(PostDynamicActivity.this, R.string.post_dyn_ok, Toast.LENGTH_SHORT)
                        .show();

                setResult(RESULT_OK);
                finish();
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

                        int width = data.getIntExtra(CropImagePath.CROP_IMAGE_WIDTH, 0);
                        int height = data.getIntExtra(CropImagePath.CROP_IMAGE_HEIGHT, 0);
                        uploadPic(cropImgPath, width, height);
                    }
                }

            } else if (requestCode == REQUEST_SHARE_SCOPE_CODE) {

                audience = data.getParcelableExtra(ShareScopeActivity.CHOOSE_DATA);
                checkedIndex = data.getIntExtra(ShareScopeActivity.CHOOSE_INDEX, 0);
                chooseContacts = (ArrayList<Contact>) data.getSerializableExtra(ShareScopeActivity.CHOOSE_C);
                checkClasspos = data.getIntExtra(EXTRA_CLASS_POS,-1);

                updateScope();
            } else if (requestCode == REQUEST_AT_CODE) {
                atContacts = (ArrayList<Contact>) data.getSerializableExtra(
                        ChoiceContactActivity.CHOOSE_CONTACT_EXTRA);
                atCheckedPos = data.getLongArrayExtra(CloseFriendActivity.CHECKED_POS);

                showAt();
            }
        }

    }

    private void uploadPic(final String filePath, final int photoWidth, final int photoHeight) {

        showProgress(false);

        SocialManager.uploadSocialPhoto(this, filePath, new QiniuService() {
            @Override
            public void uploadSuccess(String key, UploadReponse reponse) {

                photoKey = key;
                //Point point = BitmapUtils.getImageSize(filePath);
                if (photoDim == null) {
                    photoDim = new Dimension();
                }
                photoDim.width = photoWidth;
                photoDim.height = photoHeight;

                Glide.with(PostDynamicActivity.this)
                        .load(filePath)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .into(thumbnailView);

                cancelProgress();
            }

            @Override
            public void uploadProgress(String key, double percent) {

            }

            @Override
            public void uploadFailure(boolean cancel) {

                cancelProgress();
                Toast.makeText(PostDynamicActivity.this.getApplicationContext(),
                        R.string.upload_pic_error,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void updateScope() {
        String name = getScopeName(audience.type);
        scope.setText(name);

        if (audience.type == Social.ShareScope.CLASSES
                || audience.type == Social.ShareScope.PRIVATE) {
            remindWrapper.setVisibility(View.GONE);
        }else{
            clearRemid();
            remindWrapper.setVisibility(View.VISIBLE);
        }
    }

    private String getScopeName(int scopeType) {

        switch (scopeType) {
            case Social.ShareScope.FRIENDS:
                return "仅好友可见";
            case Social.ShareScope.CLASSES:
                return "班级圈可见";
            case Social.ShareScope.SPECIFIC:

                ArrayList<Doc> choosen = audience.chosen;
                int num = choosen == null ? 0 : choosen.size();

                return String.format(getResources().getString(R.string.num_can_see), num);
            case Social.ShareScope.PRIVATE:
                return "仅自己可见";
            default:
                return "所有人可见";
        }

    }
}
