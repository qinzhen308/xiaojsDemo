package com.jeek.calendar.widget.calendar;

import java.util.HashMap;

/**
 * Created by Paulz on 2017/6/6.
 */

public class HintBoxPool {
    private final HashMap<String ,HintBox> boxes=new HashMap<>();
    private static HintBoxPool instance;

    public static final String TAG_FIRST_BOX="first_box";
    public static final String TAG_SENCOND_BOX="2nd_box";
    public static final String TAG_THIRD_BOX="3th_box";

    private HintBoxPool(){
        boxes.put(TAG_FIRST_BOX,new HintBox());
        boxes.put(TAG_SENCOND_BOX,new HintBox());
        boxes.put(TAG_THIRD_BOX,new HintBox());
    }

    public static HintBoxPool getInstance(){
        if(instance==null){
            synchronized (HintBoxPool.class){
                if(instance==null){
                    instance=new HintBoxPool();
                }
            }
        }
        return instance;
    }

    public static HintBox box(String tag){
        HintBox box=getInstance().getBox(tag);
        if(box==null){
            box=new HintBox();
            getInstance().addBox(tag,box);
        }
        return box;
    }

    private HintBox getBox(String tag){
        return boxes.get(tag);
    }

    private void addBox(String tag,HintBox box){
        boxes.put(tag,box);
    }

}
