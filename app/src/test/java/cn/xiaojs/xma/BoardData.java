package cn.xiaojs.xma;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2017/9/5.
 */

public class BoardData {

    public String temp;

    @JsonDeserialize(using=CustomS.class)
    public ArrayList<P> data;
}
