package cn.xiaojs.xma.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.util.DeviceUtil;

/**
 * Created by maxiaobao on 2017/4/17.
 */

public class GuideDialog extends Dialog {


    public GuideDialog(@NonNull Context context) {
        super(context, R.style.CommonDialog);
        init();
    }

    public GuideDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, R.style.CommonDialog);
        init();
    }

    private void init() {

        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        int width = DeviceUtil.getScreenWidth(getContext()) - 2 * getContext().getResources().getDimensionPixelSize(R.dimen.px60);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);

        dialogWindow.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);

        setContentView(R.layout.layout_dlg_guide);
        ButterKnife.bind(this);

        setCancelable(false);
    }

    @OnClick({R.id.btn_student, R.id.btn_teacher, R.id.btn_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_student:

                Intent istud = new Intent(getContext(),GuideBrowseActivity.class);
                istud.putExtra(GuideBrowseActivity.EXTRA_TEACHER,false);
                getContext().startActivity(istud);

                dismiss();
                break;
            case R.id.btn_teacher:

                Intent itea = new Intent(getContext(),GuideBrowseActivity.class);
                itea.putExtra(GuideBrowseActivity.EXTRA_TEACHER,true);
                getContext().startActivity(itea);

                dismiss();
                break;
        }
    }


    @Override
    public void dismiss() {

        DataManager.setShowGuide(getContext(),true);
        super.dismiss();
    }
}
