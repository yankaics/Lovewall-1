package com.example.user.lovewall;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private SqliteDBConnect sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sd = new SqliteDBConnect(LoginActivity.this);
        showListDialog();
    }
    private void showListDialog() {
        final String[] items = { "我是老公","我是老婆" };
        AlertDialog.Builder listDialog = new AlertDialog.Builder(LoginActivity.this);
        listDialog.setTitle("请选择你的身份");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase sdb = sd.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("sex", which);
                sdb.insert("user", null, values);
                sdb.close();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        listDialog.show();
    }
}
