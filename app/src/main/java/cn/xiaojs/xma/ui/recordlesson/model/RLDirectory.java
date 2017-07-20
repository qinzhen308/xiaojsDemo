package cn.xiaojs.xma.ui.recordlesson.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RLDirectory implements Serializable{
    public String id;
    public String name;
    public ArrayList<RLLesson> children;

    public boolean isChecked_native;

    public RLDirectory(String name,String id){
        this.name=name;
        this.id=id;
    }


    public void addChild(RLLesson child){
        if(children==null){
            children=new ArrayList<>();
        }
        child.parent=this;
        children.add(child);
    }

    public int getChildrenCount(){
        return children==null?0:children.size();
    }


    public boolean remove(RLLesson child){
        if(children==null){
            return false;
        }
        child.parent=null;
        return children.remove(child);
    }

    public RLLesson remove(int position){
        if(children==null){
            return null;
        }
        RLLesson child=children.remove(position);
        child.parent=null;
        return child;
    }

    public void insert(int position , RLLesson child){
        if(children==null){
            return;
        }
        child.parent=this;
        children.add(position,child);
    }

    public RLLesson getChild(int position){
        if(children==null){
            return null;
        }
        return children.get(position);
    }


    public void setChecked(boolean isChecked){
        isChecked_native=isChecked;
        if(children==null)return;
        for(RLLesson child:children){
            child.isChecked_native=isChecked;
        }
    }

    public boolean isChecked(){
        return isChecked_native;
    }

}
