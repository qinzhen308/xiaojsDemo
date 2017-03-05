package cn.xiaojs.xma.data;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.download.DConstants;
import cn.xiaojs.xma.data.download.UpdateService;
import cn.xiaojs.xma.data.preference.DataPref;
import cn.xiaojs.xma.model.Upgrade;
import cn.xiaojs.xma.ui.UpgradeActivity;
import cn.xiaojs.xma.util.APPUtils;

/**
 * Created by maxiaobao on 2017/3/1.
 */

public class UpgradeManager {

    public static final String EXTRA_UPGRADE = "upgrade";
    public static final String EXTRA_OPEN_PATH = "open_path";
    public static final String ACTION_UPGRADE_DOWNLOAD_COMPLETED = "cn.xiaojs.xma.udcompleted";
    public static final String ACTION_UPGRADE_DOWNLOAD_ERROR = "cn.xiaojs.xma.uderror";

    /**
     * 启动更新客户端下载服务
     * @param context
     * @param url
     */
    public static void startUpdate(Context context, String url) {
        Intent i = new Intent(context, UpdateService.class);
        i.putExtra(DConstants.EXTRA_URL,url);
        context.startService(i);
    }

    /**
     * 保存客户端更新信息
     * @param context
     * @param upgrade
     */
    public static void setUpgrade(final Context context, Upgrade upgrade) {
        DataPref.setUpgrade(context,upgrade);
    }

    /**
     * 获取客户端更新信息
     * @param context
     * @return
     */
    public static Upgrade getUpgrade(final Context context) {
        return DataPref.getUpgrade(context);
    }

    /**
     * 检测是否有升级
     * @param context
     */
    public static void checkUpgrade(Context context) {
        Upgrade upgrade = getUpgrade(context);
        if (upgrade != null
                && !TextUtils.isEmpty(upgrade.uri)
                && !TextUtils.isEmpty(upgrade.verStr)
                && APPUtils.comparisonCode(context,upgrade.verStr)
                && upgrade.app == Platform.AppType.MOBILE_ANDROID
                && upgrade.action != Platform.AvailableAction.IGNORE) {

            showDlg(context,upgrade);
        }
    }

   private static void showDlg(final Context context, final Upgrade upgrade){

       Logger.d("showDlg-------------------------");

       Intent i = new Intent(context, UpgradeActivity.class);
       i.putExtra(EXTRA_UPGRADE,upgrade);
       context.startActivity(i);

//        View view = LayoutInflater.from(context).inflate(R.layout.layout_upgrade_content,null);
//
//        TextView verView = (TextView) view.findViewById(R.id.tv_ver);
//        TextView titleView = (TextView) view.findViewById(R.id.tv_tips_title);
//        TextView detailview = (TextView) view.findViewById(R.id.tv_update_detail);
//
//        String verFormat = context.getResources().getString(R.string.u_version);
//        verView.setText(String.format(verFormat,upgrade.getVerStr()));
//
//        String details = upgrade.getRemarks();
//        if (!TextUtils.isEmpty(details)) {
//            detailview.setText(details);
//            titleView.setVisibility(View.VISIBLE);
//            detailview.setVisibility(View.VISIBLE);
//        }
//
//        final CommonDialog dialog = new CommonDialog(context);
//        dialog.setTitle(R.string.app_name);
//        dialog.setCustomView(view);
//        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
//            @Override
//            public void onClick() {
//                String url = upgrade.getUri();
//                DownloadManager.startUpdate(context,url);
//                dialog.dismiss();
//            }
//        });
//        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
//            @Override
//            public void onClick() {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
   }
}
