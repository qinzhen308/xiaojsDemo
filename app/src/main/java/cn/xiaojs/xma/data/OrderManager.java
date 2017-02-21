package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.OrderRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.order.Orderp;
import cn.xiaojs.xma.model.order.PaymentCharge;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/2/21.
 */

public class OrderManager {

    public static void createOrder(Context context,
                                   Orderp orderp,
                                   APIServiceCallback<ResponseBody> callback) {
        OrderRequest request = new OrderRequest(context,callback);
        request.createOrder(orderp);
    }


    public static void createPaymentCharge(Context context,
                                           String orderId,
                                           String channel,
                                           APIServiceCallback<PaymentCharge> callback){

        OrderRequest request = new OrderRequest(context,callback);
        request.createPaymentCharge(orderId, channel);
    }

}
