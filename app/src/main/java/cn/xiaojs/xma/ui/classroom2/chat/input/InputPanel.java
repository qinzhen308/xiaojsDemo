package cn.xiaojs.xma.ui.classroom2.chat.input;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/11/24.
 */

public class InputPanel {

    @BindView(R.id.bottom_bar)
    LinearLayout chatbarLayout;

    @BindView(R.id.bar_panel)
    RelativeLayout barPanelLayout;
    @BindView(R.id.bar_input_text)
    EditText inputTextView;
    @BindView(R.id.bar_emoji_btn)
    ImageView emojiBtnView;
    @BindView(R.id.bar_sendLayout)
    FrameLayout sendLayout;
    @BindView(R.id.bar_more_btn)
    ImageView moreBtnView;
    @BindView(R.id.bar_send_btn)
    TextView sendBtnView;

    @BindView(R.id.bar_bottom_panel)
    FrameLayout bottomPanelLayout;

    private Context context;
    private boolean isKeyboardShowed = true; // 是否显示键盘

    private InputPoxy inputPoxy;


    @OnClick({R.id.bar_send_btn, R.id.bar_emoji_btn, R.id.bar_more_btn, R.id.bar_input_text,
            R.id.action_photo, R.id.action_camera, R.id.action_material})
    void onViewClick(View view) {
        switch (view.getId()){
            case R.id.bar_send_btn:
                handleSendTextClick();
                break;
            case R.id.bar_emoji_btn:
                break;
            case R.id.bar_more_btn:
                handleMoreClick();
                break;
            case R.id.bar_input_text:
                showInputMethod();
                break;
            case R.id.action_photo:
                break;
            case R.id.action_camera:
                break;
            case R.id.action_material:
                break;
        }
    }


    @OnTextChanged(value = R.id.bar_input_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void inputAfterTextChanged(Editable editable) {
        String text = editable.toString().trim();
        if (TextUtils.isEmpty(text)) {
            hideSend();
        }else {
            showSend();
        }
    }

    public InputPanel(Context context, View parentView, InputPoxy poxy) {
        this.context = context;
        this.inputPoxy = poxy;
        ButterKnife.bind(this, parentView);
        hideActionPanel();
        hideSend();
    }

    public void hideRootBar() {
        chatbarLayout.setVisibility(View.GONE);
    }

    private void handleSendTextClick() {
        String text = inputTextView.getText().toString().trim();
        if (TextUtils.isEmpty(text))
            return;
        inputTextView.setText("");
        inputPoxy.onSendText(text);
    }

    private void showSend() {
        sendBtnView.setVisibility(View.VISIBLE);
        moreBtnView.setVisibility(View.GONE);
    }

    private void hideSend() {
        sendBtnView.setVisibility(View.GONE);
        moreBtnView.setVisibility(View.VISIBLE);
    }

    private void handleMoreClick() {
        if (bottomPanelLayout.getVisibility() == View.VISIBLE) {
            hideActionPanel();
        }else {
            showActionPanel();
        }
    }


    private void showActionPanel() {

        hideInputMethod();

        bottomPanelLayout.setVisibility(View.VISIBLE);
        moreBtnView.setImageResource(R.drawable.ic_chatmoreclose);

    }


    private void hideActionPanel() {
        bottomPanelLayout.setVisibility(View.GONE);
        moreBtnView.setImageResource(R.drawable.ic_chatmore);
    }

    // 隐藏键盘布局
    private void hideInputMethod() {
        isKeyboardShowed = false;
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputTextView.getWindowToken(), 0);
        //inputTextView.clearFocus();
    }

    // 显示键盘布局
    private void showInputMethod() {

        hideActionPanel();

        inputTextView.requestFocus();
        //如果已经显示,则继续操作时不需要把光标定位到最后
        if (!isKeyboardShowed) {
            inputTextView.setSelection(inputTextView.getText().length());
            isKeyboardShowed = true;
        }

        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputTextView, 0);

    }



}
