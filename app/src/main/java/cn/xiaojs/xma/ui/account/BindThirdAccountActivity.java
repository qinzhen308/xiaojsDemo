package cn.xiaojs.xma.ui.account;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Security;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.RegisterDataManager;
import cn.xiaojs.xma.data.api.SocialRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.VerifyCode;
import cn.xiaojs.xma.model.account.SocialRegisterInfo;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.ThirdLoginUtil;
import cn.xiaojs.xma.util.VerifyUtils;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * create by Paul Z on 2017/5/18.
 * 三方登录绑定
 */
public class BindThirdAccountActivity extends BaseActivity {
    private final static int MSG_RE_SEND = 0;
    private final int SMS_GETTING_TIME_OUT = 60;

    @BindView(R.id.phone_num)
    EditTextDel mPhoneNumEdt;
    @BindView(R.id.verify_code)
    EditTextDel mVerifyCodeEdt;
    @BindView(R.id.get_verify_code)
    TextView mGetVerifyCodeTv;

    private int mCurrVerifyTime = 0;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.bind_phone);
        addView(R.layout.activity_bind_third_account);
    }

    @OnClick({R.id.left_image, R.id.enter_next, R.id.get_verify_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.enter_next:
                verifyMobileCode();
                break;
            case R.id.get_verify_code:
                getVerifyCode();
                break;
            default:
                break;
        }
    }

    private void getVerifyCode() {
        String phoneTxt = mPhoneNumEdt.getEditableText().toString().trim();
        if (TextUtils.isEmpty(phoneTxt)) {
            Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!VerifyUtils.checkPhoneNum(phoneTxt)) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mGetVerifyCodeTv.setEnabled(false);
            showProgress(true);
            long phone = Long.parseLong(phoneTxt);
            RegisterDataManager.requestSendVerifyCode(this, phone, ThirdLoginUtil.getCaptchaTypeInApi(getIntent().getStringExtra(LoginActivity.INTENT_KEY_EA)), new APIServiceCallback<VerifyCode>() {

                @Override
                public void onSuccess(VerifyCode object) {
                    cancelProgress();
                    mCurrVerifyTime = SMS_GETTING_TIME_OUT;
                    mHandler.sendEmptyMessage(0);

                    if (!APPUtils.isProEvn()) {
                        mVerifyCodeEdt.setText(String.valueOf(object.getCode()));
                    }
                    Toast.makeText(getApplicationContext(), R.string.captcha_send_success, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    mGetVerifyCodeTv.setEnabled(true);
                    Toast.makeText(BindThirdAccountActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            cancelProgress();
            mGetVerifyCodeTv.setEnabled(true);
        }

        XjsUtils.hideIMM(this, getCurrentFocus().getWindowToken());
    }

    private boolean checkSubmitInfo() {
        String phoneNum = mPhoneNumEdt.getEditableText().toString();
        String verifyCode = mVerifyCodeEdt.getEditableText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(this, R.string.verify_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!VerifyUtils.checkPhoneNum(phoneNum)) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void verifyMobileCode() {
        if (!checkSubmitInfo()) {
            return;
        }
        showProgress(true);

        String phone = mPhoneNumEdt.getEditableText().toString();
        String verifyCode = mVerifyCodeEdt.getEditableText().toString();

        try {
            final long phoneNum = Long.parseLong(phone);
            final int code = Integer.parseInt(verifyCode);
            SocialRegisterInfo info=new SocialRegisterInfo();
            info.mobile=phoneNum;
            info.code=code;
            info.avatar=getIntent().getStringExtra(LoginActivity.INTENT_KEY_AVATAR);
            info.nickname=getIntent().getStringExtra(LoginActivity.INTENT_KEY_NAME);
            info.sex=getIntent().getStringExtra(LoginActivity.INTENT_KEY_SEX);
            AccountDataManager.socialAssociate(this, getIntent().getStringExtra(LoginActivity.INTENT_KEY_EA), getIntent().getStringExtra(LoginActivity.INTENT_KEY_UID), info, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    cancelProgress();
                    setResult(RESULT_OK,getIntent());
                    finish();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCurrVerifyTime > 0) {
                mGetVerifyCodeTv.setEnabled(false);
                mCurrVerifyTime--;
                mHandler.sendEmptyMessageDelayed(MSG_RE_SEND, 1000);
                mGetVerifyCodeTv.setText(Html.fromHtml(getString(R.string.verity_code_countdown,
                        mCurrVerifyTime)));
            } else {
                mGetVerifyCodeTv.setEnabled(true);
                mGetVerifyCodeTv.setText(R.string.verity_code_re_send);
            }
        }
    };
}
