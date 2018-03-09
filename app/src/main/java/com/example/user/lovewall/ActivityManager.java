package com.example.user.lovewall;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/1/27.
 */
public class ActivityManager {
    private static ActivityManager instance;  //用静态变量储存实例
    private List<Activity> list;

    public static ActivityManager getInstance() {  //创建实例前先判断实例是否存在
        if (instance == null)
            instance = new ActivityManager();
        return instance;
    }

    public void addActivity(Activity av) {
        if(list==null)  //如果列表为空，则新建列表
            list=new ArrayList<Activity>();
        if (av != null) {  //如果Activity不为空，则添加Activity进列表
            list.add(av);
        }
    }

    public void exitAllProgress() {  //退出所有程序
        for (int i = 0; i < list.size(); i++) {
            Activity av = list.get(i);
            av.finish();
        }
    }

    //更新文件
    public void saveNote(SQLiteDatabase sdb, String name, String content, String noteId, String time){
        ContentValues cv=new ContentValues();
        cv.put("noteName", name);
        cv.put("noteContent", content);
        cv.put("noteTime", time);
        sdb.update("note", cv, "noteId=?", new String[]{noteId});
    }
    //添加文件
    public void addNote(SQLiteDatabase sdb,String name,String content,String time){
        ContentValues cv=new ContentValues();
        cv.put("noteName", name);
        cv.put("noteContent", content);
        cv.put("noteTime", time);
        sdb.insert("note", null, cv);
    }
    //返回当前的时间
    public String returnTime(){
        Date d=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=sdf.format(d);
        return time;
    }
}
