package com.example.user.lovewall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by user on 2017/1/19.
 */
public class AddActivity extends Activity {
    //标题、内容和时间
    private EditText etName, etMain, etTime;
    //保存按钮、取消按钮
    private Button btnCommit, btnCancel;
    //数据库操作类
    private SQLiteDatabase sdb;
    private ActivityManager am;
    //编辑模式标志
    private boolean EDIT = false;
    private String noteId;
    //初始化函数

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        //将当前Activity添加到Activity列表中
        am = ActivityManager.getInstance();
        am.addActivity(this);
        //初始化各个元素
        etName = (EditText) findViewById(R.id.noteName);
        etMain = (EditText) findViewById(R.id.noteMain);
        btnCommit = (Button) findViewById(R.id.btnCommit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etTime = (EditText) findViewById(R.id.noteTime);
        Intent intent = getIntent();
        noteId = intent.getStringExtra("noteId");
        //如果noteId值不为空，则进入编辑模式
        if (noteId != null)
            EDIT = true;
        else
            EDIT = false;
        //数据库连接类
        SqliteDBConnect sd = new SqliteDBConnect(AddActivity.this);
        //获得数据库操作类
        sdb = sd.getReadableDatabase();
        if (EDIT) {
            //通过noteId取得对应的信息
            Cursor c = sdb.query("note", new String[]{"noteId", "noteName",
                            "noteContent", "noteTime"}, "noteId=?",
                    new String[]{noteId}, null, null, null);
            //将获得的信息写入对应的EditText
            while (c.moveToNext()) {
                etName.setText(c.getString(c.getColumnIndex("noteName")));
                etMain.setText(c.getString(c.getColumnIndex("noteContent")));
                etTime.setText(c.getString(c.getColumnIndex("noteTime")));
            }
            c.close();
        } else {
            //设置默认时间为当前时间
            etTime.setText(am.returnTime());
        }

        //设置文本颜色为红色
        etTime.setTextColor(Color.RED);

        //保存按钮监听器
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(AddActivity.this);
                //设置标题和信息
                adb.setTitle("保存");
                adb.setMessage("确定要保存吗？");
                //设置按钮功能
                adb.setPositiveButton("保存",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //保存备忘录信息
                                saveNote();
                            }
                        });
                adb.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(AddActivity.this, "不保存",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //显示对话框
                adb.show();
            }
        });

        //设置取消按钮监听器
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(AddActivity.this);
                //设置标题和消息
                adb.setTitle("提示");
                adb.setMessage("确定不保存吗？");
                //设置按键监听器
                adb.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //进入主界面
                                Intent intent = new Intent();
                                intent.setClass(AddActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                            }
                        });
                adb.setNegativeButton("取消", null);
                //显示对话框
                adb.show();
            }
        });
    }

    //按键判断
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //当按键是返回键时
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder adb = new AlertDialog.Builder(AddActivity.this);
            adb.setTitle("消息");
            adb.setMessage("是否要保存？");
            adb.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //保存备忘录
                    saveNote();
                }
            });
            adb.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent2 = new Intent();
                    intent2.setClass(AddActivity.this, MainActivity.class);
                    //回到主页面
                    startActivity(intent2);
                }
            });
            //显示对话框
            adb.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    //保存爱情墙
    public void saveNote() {
        //取得输入的内容
        String name = etName.getText().toString().trim();
        String content = etMain.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        //内容和标题都不能为空
        if ("".equals(name) || "".equals(content)) {
            Toast.makeText(this, "名称和内容都不能为空", Toast.LENGTH_SHORT)
                    .show();
        } else {
            if(EDIT)
            {
                am.saveNote(sdb, name, content, noteId, time);
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT)
                        .show();
            }
            else
            {
                am.addNote(sdb, name, content, time);
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT)
                        .show();
            }

            Intent intent2 = new Intent();
            intent2.setClass(this, MainActivity.class);
            //回到主目录
            startActivity(intent2);
            AddActivity.this.finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        //关闭数据库连接
        sdb.close();
    }
}