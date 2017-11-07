package cn.xiaojs.xma.ui.base2;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.LoadingView;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class Base2Fragment extends Fragment {

    @Nullable
    @BindView(R.id.tips_layout)
    RelativeLayout tipsLayout;
    @Nullable
    @BindView(R.id.loading_progress)
    LoadingView loadingView;
    @Nullable
    @BindView(R.id.loading_desc)
    TextView loadingDescView;
    @Nullable
    @BindView(R.id.tips_icon)
    TextView tipsIconView;


    private ProgressHUD progress;

    public void showProgress(boolean cancelable) {
        if (progress == null) {
            progress = ProgressHUD.create(getContext());
        }
        progress.setCancellable(cancelable);
        progress.show();
    }

    public void cancelProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }


    public void showLoadingStatus() {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.VISIBLE);
            loadingView.resume();

            loadingDescView.setVisibility(View.VISIBLE);
            tipsIconView.setVisibility(View.GONE);

        }
    }

    public void showFinalTips(@DrawableRes int tipsIcon, @StringRes int tipsStr) {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            loadingView.pause();
            loadingDescView.setVisibility(View.GONE);
            tipsIconView.setVisibility(View.VISIBLE);
            tipsIconView.setCompoundDrawablesWithIntrinsicBounds(0, tipsIcon, 0, 0);
            tipsIconView.setText(tipsStr);
        }
    }

    public void showFinalTips() {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            loadingView.pause();
            loadingDescView.setVisibility(View.GONE);
            tipsIconView.setVisibility(View.VISIBLE);
        }
    }

    public void hiddenTips() {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            loadingView.pause();
            loadingDescView.setVisibility(View.GONE);
            tipsIconView.setVisibility(View.GONE);
        }
    }

}
