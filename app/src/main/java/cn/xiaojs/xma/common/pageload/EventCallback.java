package cn.xiaojs.xma.common.pageload;

/**
 * Created by Paul Z on 2017/5/16.
 *
 * 通用callback ，不指定业务
 * 使用时需要清楚调用者和实例化者之间参数
 *
 */

public interface EventCallback {
    //随意提供几种事件区分,9个还不够我吞粪
    int EVENT_1=0x1;
    int EVENT_2=0x2;
    int EVENT_3=0x3;
    int EVENT_4=0x4;
    int EVENT_5=0x5;
    int EVENT_6=0x6;
    int EVENT_7=0x7;
    int EVENT_8=0x8;
    int EVENT_9=0x9;

    /**
     *
     * @param what 区分同一EventCallback实例多个事件中调用
     * @param object 自定义传入参数类型和数量（建议非必要下不传）
     */
    public void onEvent(int what, Object... object);
}
