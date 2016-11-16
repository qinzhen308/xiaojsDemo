package com.benyuan.xiaojs.data;


/**
 * Created by maxiaobao on 2016/10/25.
 */

public class DataManager {


    public static String generateUploadKey(String fileName) {

        return fileName+System.currentTimeMillis();

    }

}
