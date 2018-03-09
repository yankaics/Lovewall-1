package com.example.user.lovewall;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int index = 0;
    private static int imageResourceIndex = 0;
    private static String [] text = new String[]{"备忘录","聊天室"};
    private static int[] imageResources = new int[]{
            R.drawable.notebook,
            R.drawable.talk};
    private ActivityManager am;  //声明管理活动类
    private Button btnAdd;  //添加按钮
    private BoomMenuButton boomMenuButton;  //菜单按钮
    private ListView lv;  //声明列表（用于显示备忘录文件）
    private SqliteDBConnect sd;  //声明数据库帮助类

    @Override
    protected void onCreate(Bundle savedInstanceState) {  //Activity一个生命周期
        super.onCreate(savedInstanceState);
        setProgressBarVisibility(true);  //显示进度条
        setContentView(R.layout.home);

        am = ActivityManager.getInstance();  //获取ActivityManager的实例
        am.addActivity(this);  //添加Activity到ActivityManager里面
        //初始化控件
        btnAdd = (Button) findViewById(R.id.btnAdd);
        boomMenuButton = (BoomMenuButton)findViewById(R.id.bmb);
        lv = (ListView) findViewById(R.id.listview);
        //初始化数据库
        sd = new SqliteDBConnect(MainActivity.this);
        showData();
        //初始化按钮菜单
        for (int i = 0; i < boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
            TextOutsideCircleButton.Builder builder = new TextOutsideCircleButton.Builder()
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            //Toast.makeText(MainActivity.this, "Clicked " + index, Toast.LENGTH_SHORT).show();
                            if(index == 0) {
                                Toast.makeText(MainActivity.this, "备忘录", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "聊天室", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .normalImageRes(getImageResource())
                    .normalText(getText());
            boomMenuButton.addBuilder(builder);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //设置ListView按键监听器
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) arg0
                        .getItemAtPosition(arg2);
                Intent intent = new Intent();
                //传递备忘录的noteId
                intent.putExtra("noteId", map.get("noteId").toString());
                intent.setClass(MainActivity.this, Lookover.class);
                //查看备忘录
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {  //设置ListView长按监听器
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> map = (Map<String, Object>) arg0
                        .getItemAtPosition(arg2);
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle(map.get("noteName").toString());
                //设置弹出选项
                adb.setItems(new String[] { "删除", "修改"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    //删除
                                    case 0:
                                        SQLiteDatabase sdb = sd
                                                .getReadableDatabase();
                                        sdb.delete("note", "noteId=?",
                                                new String[] { map.get("noteId")
                                                        .toString() });
                                        Toast.makeText(MainActivity.this, "删除成功",
                                                Toast.LENGTH_SHORT).show();
                                        sdb.close();
                                        showData();
                                        break;
                                    //修改
                                    case 1:
                                        Intent intent = new Intent();
                                        intent.putExtra("noteId", map.get("noteId")
                                                .toString());
                                        intent.setClass(MainActivity.this, AddActivity.class);
                                        //进入编辑页面
                                        startActivity(intent);
                                        finish();
                                        break;
                                }
                            }
                        });
                //显示对话框
                adb.show();
                return true;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {  //设置添加按钮监听器
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AddActivity.class);
                //进入添加页面
                startActivity(intent);
            }
        });

    }

    private static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }
    private static String getText() {
        if (index >= text.length) index = 0;
        return text[index++];

    }
    public void showData() {
        SQLiteDatabase sdb = sd.getReadableDatabase();
        Cursor c = sdb.query("note", new String[] { "noteId", "noteName",
                "noteTime" }, null, null, null, null, "noteId asc");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //遍历循环，取得所有数据，并存储到list中
        while (c.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            //取得备忘录的名字
            String strName = c.getString(c.getColumnIndex("noteName"));
            //如果字数超过12个则去掉后面的字符用...代替
            if (strName.length() > 20) {
                map.put("noteName", strName.substring(0, 20) + "...");
            } else {
                map.put("noteName", strName);
            }
            //取得时间和id信息，存储到map中
            map.put("noteTime", c.getString(c.getColumnIndex("noteTime")));
            map.put("noteId", c.getInt(c.getColumnIndex("noteId")));
            //将map添加到list中
            list.add(map);
        }
        c.close();
        sdb.close();
            //新建适配器
        SimpleAdapter sa = new SimpleAdapter(MainActivity.this, list, R.layout.items,
                new String[]{"noteName", "noteTime"}, new int[]{
                R.id.noteName, R.id.noteTime});
        //设置适配器
        lv.setAdapter(sa);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果用户按下了back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
            adb.setTitle("消息");
            adb.setMessage("真的要退出？");
            adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    am.exitAllProgress();
                }
            });
            adb.setNegativeButton("取消", null);
            //显示对话框询问用户是否确定要退出
            adb.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
