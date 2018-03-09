package com.example.user.lovewall;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2017/1/27.
 */

public class SqliteDBConnect extends SQLiteOpenHelper {  //创建一个数据库帮助类，用于创建、打开和管理数据库
    public SqliteDBConnect(Context context) {  //创建数据库，第一次调用的时候执行，之后不再执行
        super(context, "Lovewall", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {  //创建数据库，第一次调用的时候执行，之后不再执行
        System.out.println("Table before Create");
        String sql1 = "create table note(noteId Integer primary key,noteName varchar(20),noteTime varchar(20),noteContent varchar(400))";
        String sql2 = "create table user(sex Integer)";
        db.execSQL(sql1);
        db.execSQL(sql2);
        System.out.println("Table after Create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  //数据库升级的时候调用
    }
}
