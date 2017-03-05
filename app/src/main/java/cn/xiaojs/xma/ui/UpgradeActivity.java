package cn.xiaojs.xma.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.model.Upgrade;
import cn.xiaojs.xma.util.APPUtils;

public class UpgradeActivity extends Activity {

    @BindView(R.id.tv_title)
    TextView titleView;

    @BindView(R.id.tv_ver)
    TextView verView;

    @BindView(R.id.tv_tips_title)
    TextView tip_titleView;
    @BindView(R.id.tv_update_detail)
    TextView detailView;

    @BindView(R.id.left_btn)
    Button nextBtn;

    @BindView(R.id.right_btn)
    Button updateBtn;

    @BindView(R.id.activity_upgrade)
    RelativeLayout layout;

    private Upgrade upgrade;

    private String filePath;

    UpgradeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        ButterKnife.bind(this);

        upgrade = getIntent().getParcelableExtra(UpgradeManager.EXTRA_UPGRADE);
        if (upgrade == null) {
            finish();
            return;
        }

        if (upgrade.action == Platform.AvailableAction.UPGRADE) {
            setFinishOnTouchOutside(false);

            receiver = new UpgradeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(UpgradeManager.ACTION_UPGRADE_DOWNLOAD_COMPLETED);
            filter.addAction(UpgradeManager.ACTION_UPGRADE_DOWNLOAD_ERROR);
            registerReceiver(receiver,filter);
        }

        bindView();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(filePath)){
                    APPUtils.openPkg(UpgradeActivity.this,filePath);
                    return;
                }

                UpgradeManager.startUpdate(UpgradeActivity.this,upgrade.uri);
                if (upgrade.action == Platform.AvailableAction.UPGRADE) {
                    updateBtn.setEnabled(false);
                    updateBtn.setText(R.string.upgrade_downloading);
                    return;
                }

                finish();


            }
        });



    }

    @Override
    protected void onDestroy() {

        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        super.onDestroy();
    }

    private void bindView() {

        int action = upgrade.action;

        String verFormat = getResources().getString(R.string.u_version);
        verView.setText(String.format(verFormat, upgrade.verStr));

        String details = upgrade.remarks;
        if (!TextUtils.isEmpty(details)) {
            detailView.setText(details);
            tip_titleView.setVisibility(View.VISIBLE);
            tip_titleView.setVisibility(View.VISIBLE);
        }

        if (action == Platform.AvailableAction.UPGRADE) {
            nextBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (upgrade.action != Platform.AvailableAction.UPGRADE) {
            super.onBackPressed();
        }
    }

    private class UpgradeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UpgradeManager.ACTION_UPGRADE_DOWNLOAD_COMPLETED)){

                filePath = intent.getStringExtra(UpgradeManager.EXTRA_OPEN_PATH);
                if (!TextUtils.isEmpty(filePath)){
                    updateBtn.setEnabled(true);
                    updateBtn.setText(R.string.click_install);
                }

            }else if(action.equals(UpgradeManager.ACTION_UPGRADE_DOWNLOAD_ERROR)) {
                updateBtn.setEnabled(true);
                updateBtn.setText(R.string.upgrade_error_reclick);
            }
        }
    }
}
