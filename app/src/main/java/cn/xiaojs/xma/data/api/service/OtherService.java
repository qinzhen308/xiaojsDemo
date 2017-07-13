package cn.xiaojs.xma.data.api.service;

import java.util.Map;

import cn.xiaojs.xma.model.QRCodeData;
import cn.xiaojs.xma.model.QRImgRequst;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Paul Z on 2017/6/9.
 */

public interface OtherService {
    @FormUrlEncoded
    @POST("/web/public/getimgcode.html")
    Call<QRCodeData> getQRImg(@FieldMap Map<String,String> params);
}
