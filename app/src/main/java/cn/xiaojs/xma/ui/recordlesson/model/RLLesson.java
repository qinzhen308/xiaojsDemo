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
        //优化全选后，再取消一个的情况
        if(parent.isChecked()&&isChecked==false){
            parent.isChecked_native=false;
            return;
        }
        for(RLLesson bro:parent.children){
            if(!bro.isChecked_native){
                parent.isChecked_native=false;
                return;
            }
        }
        parent.isChecked_native=true;
    }

    public boolean isChecked(){
        return isChecked_native;
    }
}
