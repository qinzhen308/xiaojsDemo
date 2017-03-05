package cn.xiaojs.xma.data;

import android.content.Context;

import java.util.List;

import cn.xiaojs.xma.data.api.OrderRequest;
import cn.xiaojs.xma.data.api.PayRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.order.EnrollOrder;
import cn.xiaojs.xma.model.order.Orderp;
import cn.xiaojs.xma.model.order.PaymentCharge;
import cn.xiaojs.xma.model.order.PaymentOrder;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/2/21.
 */

public class OrderManager {

    public static void createOrder(Context context,
                                   Orderp orderp,
                                   APIServiceCallback<PaymentOrder> callback) {
        OrderRequest request = new OrderRequest(context,callback);
        request.createOrder(orderp);
    }


    public static void createPaymentCharge(Context context,
                                           String orderId,
                                           String channel,
                                           APIServiceCallback<ResponseBody> callback){

        OrderRequest request = new OrderRequest(context,callback);
        request.createPaymentCharge(orderId, channel);
    }

    public static void toPay(Context context,
                             String orderId,
                             String channel,
                             Orderp orderp,
                             APIServiceCallback<String> callback){

        PayRequest payRequest = new PayRequest(context, orderId, channel);
        payRequest.toPay(orderp,callback);

    }

    public static void getOrders(Context context, Pagination pagination, APIServiceCallback<List<EnrollOrder>> callback) {

        int page = 1;
        int limit = 10;

        if (pagination!=null){
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }

        OrderRequest request = new OrderRequest(context,callback);
        request.getOrders(page,limit);
    }
}
