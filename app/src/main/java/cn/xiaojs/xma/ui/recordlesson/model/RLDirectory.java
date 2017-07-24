package cn.xiaojs.xma.ui.recordlesson.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RLDirectory implements Serializable{
    public String name;
    public ArrayList<RLLesson> children;

    public boolean isChecked_native;

    public RLDirectory(String name){
        this.name=name;
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

    public void replace(int position , RLLesson child){
        if(children==null){
            return;
        }
        children.get(position).parent=null;
        child.parent=this;
        children.set(position,child);
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


    public void removeChecked(){
        if(children==null)return;
        Iterator<RLLesson> iteratorChild=children.iterator();
        while (iteratorChild.hasNext()){
            RLLesson lesson=iteratorChild.next();
            if(lesson.isChecked()){
                lesson.parent=null;
                iteratorChild.remove();
            }
        }
    }

}
