package cn.xiaojs.xma.ui.lesson.xclass.model;

/**
 * Created by Paul Z on 2017/5/26.
 */

public class LessonLabelModel {

    public String date;
    public int lessonCount;

    public boolean hasData;


    public LessonLabelModel(String date,int lessonCount,boolean hasData) {
        this.date=date;
        this.lessonCount=lessonCount;
        this.hasData=hasData;
    }
}
