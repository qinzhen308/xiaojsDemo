package cn.xiaojs.xma.ui.recordlesson.model;

import java.io.Serializable;

import cn.xiaojs.xma.model.material.LibDoc;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RLLesson implements Serializable{
    public String id;
    public RLDirectory parent;
    public String name;
    public String videoName;
    public String videoId;
    public String videoKey;
    public String videoMimeType;


    public boolean isChecked_native;


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


    public void setLibDoc(LibDoc doc){
        videoMimeType=doc.mimeType;
        videoId=doc.id;
        videoName=doc.name;
        videoKey=doc.key;
    }

    public LibDoc buildLibDoc(){
        LibDoc doc=new LibDoc();
        doc.mimeType=videoMimeType;
        doc.id =videoId;
        doc.name =videoName;
        doc.key =videoKey;
        return doc;
    }
}
