package cn.xiaojs.xma.ui.classroom.page;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.main.PlayFragment;
import cn.xiaojs.xma.ui.classroom.whiteboard.OnImeBackListener;
import cn.xiaojs.xma.ui.classroom2.chat.ChatSessionFragment;
import cn.xiaojs.xma.ui.classroom2.chat.input.InputPanel;
import cn.xiaojs.xma.ui.classroom2.chat.input.InputPoxy;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.widget.ClosableEditDialog;
import cn.xiaojs.xma.ui.widget.SpecialEditText;
import cn.xiaojs.xma.util.XjsUtils;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/5/4
 * Desc:
 *
 * ======================================================================================== */

public class MsgInputFragment extends DialogFragment implements View.OnClickListener, InputPoxy {
    private View mMsgInputLayout;
    private SpecialEditText mMsgInputEdt;
    private TextView mMsgSendBtn;

    private Context mContext;
    private Handler mHandler;
    private int mFrom;

    private LinearLayout msgInputLayout;
    private LinearLayout lotLayout;

    private InputPanel inputPanel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                onHandleMessage(msg);
            }
        };

        Bundle data = getArguments();
        if (data != null) {
            mFrom = data.getInt(CTLConstant.EXTRA_INPUT_FROM, PlayFragment.MSG_MODE_INPUT);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mFrom == PlayFragment.FULL_SCREEN_MODE_INPUT) {
            lotLayout.setVisibility(View.GONE);
            msgInputLayout.setVisibility(View.VISIBLE);

        }else {
            lotLayout.setVisibility(View.VISIBLE);
            msgInputLayout.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMsgInputLayout = createView();
        Dialog dialog = new ClosableEditDialog(mContext, R.style.CommonDialog, mMsgInputEdt);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        //dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //set attributes
        dialogWindow.setWindowAnimations(R.style.BottomSheetAnim);
        dialogWindow.setContentView(mMsgInputLayout);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //set params
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0;
        dialogWindow.setAttributes(params);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mHandler.sendEmptyMessageDelayed(0, 100);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    MsgInputFragment.this.dismiss();
                }
                return false;
            }
        });

        mMsgInputEdt.setOnImeBackListener(new OnImeBackListener() {
            @Override
            public void onImeBackPressed() {
                MsgInputFragment.this.dismiss();
            }
        });

        return dialog;
    }

    protected View createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_talk_input, null);
        mMsgInputEdt = (SpecialEditText) view.findViewById(R.id.msg_input);
        mMsgSendBtn = (TextView) view.findViewById(R.id.msg_send);
        mMsgSendBtn.setOnClickListener(this);

        msgInputLayout = view.findViewById(R.id.msg_input_layout);
        lotLayout = view.findViewById(R.id.bottom_bar);

        inputPanel = new InputPanel(getContext(), view, this);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    private void onHandleMessage(Message msg) {

        if (mFrom == PlayFragment.FULL_SCREEN_MODE_INPUT) {
            mMsgInputEdt.requestFocus();
            XjsUtils.showIMM(mContext, mMsgInputEdt);
        }else {
            inputPanel.showInputMethod();
        }


    }

    @Override
    public void onClick(View v) {

        Fragment target = getTargetFragment();
        if (target != null) {
            Intent intent = new Intent();
            intent.putExtra(CTLConstant.EXTRA_INPUT_MESSAGE, mMsgInputEdt.getText().toString());
            intent.putExtra(CTLConstant.EXTRA_INPUT_FROM, mFrom);
            target.onActivityResult(CTLConstant.REQUEST_INPUT_MESSAGE, Activity.RESULT_OK, intent);
        }

        XjsUtils.hideIMM(mContext, mMsgInputEdt.getWindowToken());
        MsgInputFragment.this.dismiss();


    }

    @Override
    public void onSendText(String text) {
        Fragment target = getTargetFragment();
        if (target != null && target instanceof ChatSessionFragment) {
            ChatSessionFragment sessionFragment = (ChatSessionFragment) target;
            sessionFragment.onSendText(text);
        }
        inputPanel.hideInputMethod();
        MsgInputFragment.this.dismiss();
    }

    @Override
    public void onTakeCamera() {
        Fragment target = getTargetFragment();
        if (target != null && target instanceof ChatSessionFragment) {
            ChatSessionFragment sessionFragment = (ChatSessionFragment) target;
            sessionFragment.onTakeCamera();
        }
        inputPanel.hideInputMethod();
        MsgInputFragment.this.dismiss();
    }

    @Override
    public void onPickPhotos() {

        Fragment target = getTargetFragment();
        if (target != null && target instanceof ChatSessionFragment) {
            ChatSessionFragment sessionFragment = (ChatSessionFragment) target;
            sessionFragment.onPickPhotos();
        }
        inputPanel.hideInputMethod();
        MsgInputFragment.this.dismiss();

    }
}
