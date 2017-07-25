package cn.xiaojs.xma.ui.recordlesson;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/7/24.
 */

public class RecordLessonHelper {
    public static RLDirectory isDuplication(List<RLDirectory> src,RLDirectory target){
        String name=target.name;
        if(name==null)return null;
        if(ArrayUtil.isEmpty(src))return null;
        for(RLDirectory dir:src){
            if(dir.name.equals(target.name))return dir;
        }
        return null;
    }

    public static RLLesson isDuplication(List<RLDirectory> src,RLLesson target){
        String name=target.name;
        if(name==null)return null;
        if(ArrayUtil.isEmpty(src))return null;
        for(RLDirectory dir:src){
            RLLesson l=isDuplication(dir,target);
            if(l!=null)return l;
        }
        return null;
    }

    public static RLLesson isDuplication(RLDirectory src,RLLesson target){
        String name=target.name;
        if(name==null)return null;
        if(ArrayUtil.isEmpty(src.children))return null;
        for(RLLesson l:src.children){
            if(l.videoId.equals(target.videoId)||l.name.equals(target.name))return l;
        }
        return null;
    }

    public static RLLesson isDuplication(List<RLDirectory> src,LibDoc target){
        if(ArrayUtil.isEmpty(src))return null;
        for(RLDirectory dir:src){
            RLLesson l=isDuplication(dir,target);
            if(l!=null)return l;
        }
        return null;
    }

    public static RLLesson isDuplication(RLDirectory src,LibDoc target){
        if(ArrayUtil.isEmpty(src.children))return null;
        for(RLLesson l:src.children){
            if(l.videoId.equals(target.id))return l;
        }
        return null;
    }
}
