package cn.xiaojs.xma.ui.recordlesson.model;

import java.io.Serializable;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RLLesson implements Serializable{
    public String id;
    public RLDirectory parent;
    public String name;


    public boolean isChecked_native;

    public RLLesson(String name,String id){
        this.name=name;
        this.id=id;
    }


    public void setChecked(boolean isChecked){
        isChecked_native=isChecked;
        if(parent==null)return;
        boolean parentChecked=isChecked;
        for(RLLesson bro:parent.children){
            if(bro.isChecked_native!=isChecked){
                parentChecked=!parentChecked;
                break;
            }
        }
        parent.isChecked_native=parentChecked;
    }

    public boolean isChecked(){
        return isChecked_native;
    }
}
