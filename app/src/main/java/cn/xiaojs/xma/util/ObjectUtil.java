package cn.xiaojs.xma.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Paul Z on 2017/7/19.
 */

public class ObjectUtil {

    public static <T extends Serializable>  T deepClone(T src){
        T dest=null;
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        ObjectOutputStream stream;
        try {
            stream = new ObjectOutputStream(byteout);
            stream.writeObject(src);
            ByteArrayInputStream bytein = new ByteArrayInputStream(
                    byteout.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bytein);

            dest = (T) in.readObject();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dest;
    }

}
