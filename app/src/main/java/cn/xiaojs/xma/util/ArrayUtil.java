package cn.xiaojs.xma.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Paul Z on 2017/5/12.
 */

public class ArrayUtil {

    public static boolean isEmpty(Collection c){
        return c==null||c.isEmpty();
    }

    public static boolean isEmpty(Set s){
        return s==null||s.isEmpty();
    }

    public static <T> boolean isEmpty(T[] o){
        return o==null||o.length==0;
    }

    public static boolean isEmpty(Map m){
        return m==null||m.isEmpty();
    }


}
