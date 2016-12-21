package cn.xiaojs.xma;

/**
 * Created by maxiaobao on 2016/11/11.
 */

public class Haha<T> {

    private String id;
    private T t;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public static class No{
        private int num;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}
