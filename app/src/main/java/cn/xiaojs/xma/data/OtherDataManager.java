package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.OtherRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.QRCodeData;

/**
 * Created by Paul Z on 2017/6/9.
 */

public class OtherDataManager {

    public static void getQRCodeImg(Context context, String classId,APIServiceCallback<QRCodeData> callback) {
        OtherRequest request=new OtherRequest(context,callback);
        request.getQRCodeImg(classId,"10");
    }


}
