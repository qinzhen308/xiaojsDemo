package cn.xiaojs.xma.data.api.service;

import java.util.Map;

import cn.xiaojs.xma.model.QRCodeData;
import cn.xiaojs.xma.model.QRImgRequst;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Paul Z on 2017/6/9.
 */

public interface OtherService {

    @POST("/xjsweixin/web/index.php?r=public/getimgcode")
    Call<QRCodeData> getQRImg(@Body QRImgRequst params);
}
