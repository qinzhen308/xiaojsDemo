package cn.xiaojs.xma.data.api;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.QRCodeData;
import cn.xiaojs.xma.util.APPUtils;
import retrofit2.Call;

/**
 * Created by Paul Z on 2017/6/9.
 */

public class OtherRequest extends ServiceRequest{


    public OtherRequest(Context acontext, APIServiceCallback callback) {
        super(acontext, callback);
    }

    public void getQRCodeImg(String classid, String size) {
//        QRImgRequst requst=new QRImgRequst();
//        requst.code=classid;
//        requst.size=size;
        String baseUrl = null;
        if(APPUtils.isProEvn()){
            baseUrl = XiaojsConfig.SHARE_LESSON_BASE_URL;
        }else {
            baseUrl=XiaojsConfig.SHARE_LESSON_TEST_BASE_URL;
        }
        Map<String ,String > map=new HashMap<>();
        map.put("code",classid);
        map.put("size",size);
        map.put("type","classhome");
        Call<QRCodeData> call = getApiManager().createOtherService(baseUrl).getQRImg(map);
        enqueueRequest(APIType.GET_ACCOUNT_ACTIVITIES,call);
    }


}
