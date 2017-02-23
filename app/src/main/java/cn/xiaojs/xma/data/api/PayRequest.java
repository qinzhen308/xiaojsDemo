package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import java.io.IOException;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.OrderManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.data.api.service.XiaojsService;
import cn.xiaojs.xma.model.order.Orderp;
import cn.xiaojs.xma.model.order.PaymentCharge;
import cn.xiaojs.xma.model.order.PaymentOrder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/2/23.
 */

public class PayRequest {

    private Context context;
    private String orderId;
    private String channel;
    private APIServiceCallback<String> callback;

    public PayRequest(Context context, String orderId, String channel) {
        this.context = context.getApplicationContext();
        this.orderId = orderId;
        this.channel = channel;
    }

    public void toPay(Orderp orderp, APIServiceCallback<String> callback){
        this.callback = callback;

        if (TextUtils.isEmpty(orderId)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the orderid is null,so create order first");
            }
            newOrder(orderp);
        }else{
           paymentCharge();
        }

    }

    private void newOrder(Orderp orderp) {

        OrderManager.createOrder(context, orderp, new APIServiceCallback<PaymentOrder>() {
            @Override
            public void onSuccess(PaymentOrder order) {

                if (order == null || TextUtils.isEmpty(order.order_no)){
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("the PaymentOrder is unvalid");
                    }

                    if (callback != null) {
                        callback.onFailure("", "");
                    }
                    return;
                }

                orderId = order.id;
                paymentCharge();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                if(XiaojsConfig.DEBUG) {
                    Logger.d("the pay exception error code:%s, errorMsg:%s", errorCode,errorMessage);
                }

                if (callback !=null){
                    callback.onFailure("", "");
                }
            }
        });
    }

    private void paymentCharge() {

        OrderManager.createPaymentCharge(context,
                orderId, channel, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {

                if (responseBody == null){

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("the responseBody is unvalid");
                    }

                    if (callback != null) {
                        callback.onFailure("", "");
                    }

                    return;
                }

                String data = null;
                try {
                    data = responseBody.string();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure("", "");
                    }
                }

                if (callback != null) {
                    callback.onSuccess(data);
                }

//                String url;
//
//                if (paymentCharge == null
//                        || paymentCharge.refunds == null
//                        || TextUtils.isEmpty(url = paymentCharge.refunds.url)){
//
//                    if (XiaojsConfig.DEBUG) {
//                        Logger.d("the paymentCharge is unvalid");
//                    }
//
//                    if (callback != null) {
//                        callback.onFailure("", "");
//                    }
//
//                    return;
//                }
//
//
//                String rurl = new StringBuilder(XiaojsConfig.BASE_URL).append(":")
//                        .append(XiaojsService.SERVICE_PORT).append(url).toString();
//                requestPay(rurl,paymentCharge.amount);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                if(XiaojsConfig.DEBUG) {
                    Logger.d("the pay exception error code:%s, errorMsg:%s", errorCode,errorMessage);
                }

                if (callback !=null){
                    callback.onFailure("", "");
                }
            }
        });

    }

    private void requestPay(String url, float amount) {

        PayParam payParam = new PayParam(channel,amount);
        String json = ServiceRequest.objectToJsonString(payParam);

        MediaType type = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(type, json);
        Request request = new Request.Builder().url(url).post(body).build();
        OkHttpClient okHttpClient = ApiManager.getPayClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if(XiaojsConfig.DEBUG) {
                    Logger.d("the pay exception:" + e.getMessage());
                }

                if (callback !=null){
                    callback.onFailure("", "");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();

                if (callback != null) {
                    callback.onSuccess(data);
                }
            }
        });
    }


    public class PayParam {
        String channel;
        float amount;

        public PayParam(String channel, float amount) {
            this.channel = channel;
            this.amount = amount;
        }
    }

}
