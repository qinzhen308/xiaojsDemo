package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.order.Orderp;
import cn.xiaojs.xma.model.order.PaymentCharge;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2017/2/21.
 */

public class OrderRequest extends ServiceRequest{

    public OrderRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }


    public void createOrder(Orderp orderp) {
        Call<ResponseBody> call = getService().createOrder(orderp);
        enqueueRequest(APIType.CREATE_ORDER,call);
    }

    public void createPaymentCharge(String orderId, String channel) {
        Call<PaymentCharge> call = getService().createPaymentCharge(orderId, channel);
        enqueueRequest(APIType.CREATE_PAYMENT_CHARGE,call);
    }

}
