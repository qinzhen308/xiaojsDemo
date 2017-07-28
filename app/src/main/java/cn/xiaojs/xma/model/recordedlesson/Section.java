package cn.xiaojs.xma.model.recordedlesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;

import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by Paul Z on 2017/7/26.
 * 录播课目录节点---录播课详情返回的
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Section implements Serializable{
    public static final String TYPE_CHAPTER ="CourseChapter";
    public static final String TYPE_LESSON="RecordedLesson";

    public String title;
    public int index;
    public String resource;
    public String name;
    public String root;
    public String parent;
    public String path;
    public String typeName;
    public String id;
    public ArrayList<Section> sections;
    public SectionDoc document;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SectionDoc implements Serializable{
        public String resource;
        public String name;
        public String mimeType;
        public String typeName;
        public String key;
        public Duration duration;
        public String id;

        public LibDoc buildLibDoc(){
            LibDoc doc=new LibDoc();
            doc.mimeType=mimeType;
            doc.name=name;
            doc.key=key;
            doc.id=id;
            return doc;
        }

        public String getTime(){
            if(duration==null)return "未知";
            return TimeUtil.differenceForTime(duration.getStart(),duration.getEnd());
        }
    }

    public int getChildrenCount(){
        return sections==null?0:sections.size();
    }

    public Section getChild(int position){
        if(sections==null)return null;
        return sections.get(position);
    }

    public boolean isLib(){
        return TYPE_CHAPTER.equals(typeName);
    }

    public boolean isLesson(){
        return TYPE_LESSON.equals(typeName);
    }


}
