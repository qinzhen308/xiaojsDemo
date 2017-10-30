package cn.xiaojs.xma.data.api.socket.xms;

/**
 * Created by maxiaobao on 2017/10/27.
 */

public class SocketStatus {
    public final static int CONNECT_BEGIN = 1;                               //开始连接
    public final static int CONNECTING = 2;                                  //连接中
    public final static int CONNECT_SUCCESS = 3;                             //连接成功
    public final static int CONNECT_FAILED = 4;                              //连接失败
    public final static int CONNECT_TIMEOUT = 5;                             //连接超时
    public final static int DISCONNECTED = 6;                                //断开连接
    public final static int RECONNECT = 7;                                   //重新连接
    public final static int RECONNECT_ARRIVED_MAX_COUNT = 8;                 //重连次数达到上限
}
