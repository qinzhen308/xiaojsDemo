package cn.xiaojs.xma.ui.classroom.main;

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
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.whiteboard.OnImeBackListener;
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

public class MsgInputFragment extends DialogFragment implements View.OnClickListener {
    private View mMsgInputLayout;
    private SpecialEditText mMsgInputEdt;
    private TextView mMsgSendBtn;

    private Context mContext;
    private Handler mHandler;
    private int mFrom;

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
            mFrom = data.getInt(Constants.KEY_MSG_INPUT_FROM, PlayFragment.MSG_MODE_INPUT);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, R.style.CommonDialog);
        dialog.setCanceledOnTouchOutside(true);

        mMsgInputLayout = createView();
        Window dialogWindow = dialog.getWindow();

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

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                XjsUtils.hideIMM(mContext, mMsgInputEdt.getWindowToken());
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    mMsgInputLayout.setVisibility(View.GONE);
                    XjsUtils.hideIMM(mContext, mMsgInputEdt.getWindowToken());
                    MsgInputFragment.this.dismiss();
                }
                return false;
            }
        });

        mMsgInputEdt.setOnImeBackListener(new OnImeBackListener() {
            @Override
            public void onImeBackPressed() {
                mMsgInputLayout.setVisibility(View.GONE);
                XjsUtils.hideIMM(mContext, mMsgInputEdt.getWindowToken());
                MsgInputFragment.this.dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mMsgInputLayout != null) {
                    mMsgInputLayout.setVisibility(View.GONE);
                    XjsUtils.hideIMM(mContext, mMsgInputEdt.getWindowToken());
                } else {
                    XjsUtils.hideIMM(mContext);
                }
            }
        });

        return dialog;
    }

    protected View createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_talk_input, null);
        mMsgInputEdt = (SpecialEditText) view.findViewById(R.id.msg_input);
        mMsgSendBtn = (TextView) view.findViewById(R.id.msg_send);
        mMsgSendBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        XjsUtils.hideIMM(mContext, mMsgInputEdt.getWindowToken());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    private void onHandleMessage(Message msg) {
        mMsgInputEdt.requestFocus();
        XjsUtils.showIMM(mContext, mMsgInputEdt);
    }

    @Override
    public void onClick(View v) {
        Fragment target = getTargetFragment();
        if (target != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_MSG_INPUT_TXT, mMsgInputEdt.getText().toString());
            intent.putExtra(Constants.KEY_MSG_INPUT_FROM, mFrom);
            target.onActivityResult(ClassroomController.REQUEST_INPUT, Activity.RESULT_OK, intent);
        }

        XjsUtils.hideIMM(mContext, mMsgInputEdt.getWindowToken());
        MsgInputFragment.this.dismiss();
    }
}
