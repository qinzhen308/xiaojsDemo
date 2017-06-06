package com.jeek.calendar.widget.calendar;

import android.graphics.Canvas;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Paulz on 2017/6/5.
 * 用于保存红点提示的相关数据，以及提供相应方法
 */

public class HintBox {


    private Map<String,Integer> monthDates=new HashMap<>();



    public boolean contains(DateTime date){
        int year=date.getYear();
        int month=date.getMonthOfYear();
        int day=date.getDayOfMonth();
        String str1=year+"-"+(month<10?("0"+month):month)+"-"+(day<10?("0"+day):day);
        String str2=year+"-"+month+"-"+day;
        return monthDates.containsKey(str1)||monthDates.containsKey(str2);
    }

    public boolean contains(int year,int month,int day){
        month=month+1;
        String str1=year+"-"+(month<10?("0"+month):month)+"-"+(day<10?("0"+day):day);
        String str2=year+"-"+month+"-"+day;
        return monthDates.containsKey(str1)||monthDates.containsKey(str2);
    }


    public void add(String date,int color){
        monthDates.put(date,color);
    }


    public void setMonthDates(Map<String,Integer>  monthDates){
        if(monthDates!=null){
            this.monthDates=monthDates;
        }else {
            this.monthDates.clear();
        }
    }

    public Map<String,Integer>  getMonthDates(){
        return monthDates;
    }

    public int getColor(DateTime date){
        int year=date.getYear();
        int month=date.getMonthOfYear();
        int day=date.getDayOfMonth();
        String str1=year+"-"+(month<10?("0"+month):month)+"-"+(day<10?("0"+day):day);
        String str2=year+"-"+month+"-"+day;
        if(monthDates.containsKey(str1)){
            return monthDates.get(str1);
        }else if(monthDates.containsKey(str2)){
            return monthDates.get(str2);
        }
        return 0;
    }
    public int getColor(int year,int month,int day){
        month=month+1;
        String str1=year+"-"+(month<10?("0"+month):month)+"-"+(day<10?("0"+day):day);
        String str2=year+"-"+month+"-"+day;
        if(monthDates.containsKey(str1)){
            return monthDates.get(str1);
        }else if(monthDates.containsKey(str2)){
            return monthDates.get(str2);
        }
        return 0;
    }

}
