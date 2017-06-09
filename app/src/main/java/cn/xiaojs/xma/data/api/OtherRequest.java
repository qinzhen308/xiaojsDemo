package cn.xiaojs.xma.data.api;

import android.content.Context;

import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.OtherService;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.QRCodeData;
import cn.xiaojs.xma.model.QRImgRequst;
import cn.xiaojs.xma.model.social.DynamicAcc;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by Paul Z on 2017/6/9.
 */

public class OtherRequest extends ServiceRequest{


    public OtherRequest(Context acontext, APIServiceCallback callback) {
        super(acontext, callback);
    }

    public void getQRCodeImg(String classid, String size) {
        QRImgRequst requst=new QRImgRequst();
        requst.code=classid;
        requst.size=size;
        Call<QRCodeData> call = getApiManager().createOtherService(XiaojsConfig.APP_QRCODE_IMG_BASE_URL).getQRImg(requst);
        enqueueRequest(APIType.GET_ACCOUNT_ACTIVITIES,call);
    }


}
