package com.example.user.lovewall;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChatActivity extends AppCompatActivity {
    private int myChoice = -1;
    private String yourNumber = null;
    private String myNumber = null;
    private static int index = 0;
    private static int imageResourceIndex = 0;
    private static String [] text = new String[]{"备忘录","聊天室"};
    private static int[] imageResources = new int[]{
            R.drawable.notebook,
            R.drawable.talk};
    private ActivityManager am;  //声明管理活动类
    private BoomMenuButton boomMenuButton;  //菜单按钮
    private EditText contentEdt;
    private Button addBtn;
    private RecyclerView msgRecyclerView;
    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter msgAdapter;
    private SqliteDBConnect sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        sd = new SqliteDBConnect(ChatActivity.this);
        SQLiteDatabase sdb = sd.getReadableDatabase();
        Cursor cursor = sdb.query("user", new String[] {"sex"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            myChoice = cursor.getInt(cursor.getColumnIndex("sex"));
        }
        cursor.close();
        sdb.close();

        am = ActivityManager.getInstance();  //获取ActivityManager的实例
        am.addActivity(this);  //添加Activity到ActivityManager里面
        //初始化控件
        boomMenuButton = (BoomMenuButton)findViewById(R.id.bmb);
        addBtn = (Button)findViewById(R.id.btnAdd);
        contentEdt = (EditText)findViewById(R.id.content);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_resycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        msgAdapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(msgAdapter);

        IMSDKInit();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = contentEdt.getText().toString();
                try{
                    // 组建一个待发送的ECMessage
                    ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
                    // 设置消息接收者
                    if(myChoice == 0) {
                        yourNumber = "521888";
                    }else {
                        yourNumber = "521666";
                    }
                    msg.setTo(yourNumber);
                    // 创建一个文本消息体，并添加到消息对象中
                    final ECTextMessageBody msgBody = new ECTextMessageBody(content);
                    // 将消息体存放到ECMessage中
                    msg.setBody(msgBody);
                    // 调用SDK发送接口发送消息到服务器
                    ECChatManager manager = ECDevice.getECChatManager();
                    manager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
                        @Override
                        public void onSendMessageComplete(ECError error, ECMessage message) {
                            // 处理消息发送结果
                            if(message == null) {
                                Toast.makeText(ChatActivity.this,"message内容为空！",Toast.LENGTH_SHORT).show();
                            }else {
                                // 将发送的消息更新到本地数据库并刷新UI
                                Msg msg1 = new Msg(content, Msg.TYPE_SEND);
                                msgList.add(msg1);
                                msgAdapter.notifyItemInserted(msgList.size() - 1);  //当有消息时，刷新ListView显示
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);  //将ListView定位到最后一行
                                contentEdt.setText("");  //清空输入框中的内容
                            }
                        }
                        @Override
                        public void onProgress(String msgId, int totalByte, int progressByte) {
                            // 处理文件发送上传进度（上传文件、图片时候SDK回调该方法）
                        }
                    });
                } catch (Exception e) {
                    // 处理发送异常
                    Log.e("ECSDK_Demo", "send message fail , e=" + e.getMessage());
                }
            }
        });


        for (int i = 0; i < boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
            TextOutsideCircleButton.Builder builder = new TextOutsideCircleButton.Builder()
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            //Toast.makeText(MainActivity.this, "Clicked " + index, Toast.LENGTH_SHORT).show();
                            if(index == 0) {
                                Toast.makeText(ChatActivity.this, "备忘录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(ChatActivity.this, "聊天室", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .normalImageRes(getImageResource())
                    .normalText(getText());
            boomMenuButton.addBuilder(builder);
        }
    }

    private static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }
    private static String getText() {
        if (index >= text.length) index = 0;
        return text[index++];
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果用户按下了back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder adb = new AlertDialog.Builder(ChatActivity.this);
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

    private void IMSDKInit() {
        //判断SDK是否已经初始化
        if(!ECDevice.isInitialized()) {
 /*  initial: ECSDK 初始化接口
            * 参数：
            *     inContext - Android应用上下文对象
            *     inListener - SDK初始化结果回调接口，ECDevice.InitListener
            *
            * 说明：示例在应用程序创建时初始化 SDK引用的是Application的上下文，
            *       开发者可根据开发需要调整。
            */
            ECDevice.initial(getApplicationContext(), new ECDevice.InitListener() {
                @Override
                public void onInitialized() {
                    // SDK已经初始化成功
                    Log.i("","初始化SDK成功");
                    //设置登录参数，可分为自定义方式和VoIP验密方式
                    ECInitParams params = ECInitParams.createParams();
                    if(myChoice == 0) {
                        myNumber = "521666";
                    }
                    else {
                        myNumber = "521888";
                    }
                    params.setUserid(myNumber);
                    params.setAppKey("8a216da86058d6fa016067aaf2fe0321");
                    params.setToken("d57dbda973fd57dc232994d41b42a940");
                    //设置登陆验证模式：自定义登录方式
                    params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
                    //LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO。使用方式详见注意事项）
                    params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
                    //设置登录回调监听
                    ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
                        public void onConnect() {
                            //兼容旧版本的方法，不必处理
                        }

                        @Override
                        public void onDisconnect(ECError error) {
                            //兼容旧版本的方法，不必处理
                        }

                        @Override
                        public void onConnectState(ECDevice.ECConnectState state, ECError error) {
                            if(state == ECDevice.ECConnectState.CONNECT_FAILED ){
                                if(error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                                    Log.i("","==帐号异地登陆");
                                }
                                else
                                {
                                    Log.i("","==其他登录失败,错误码："+ error.errorCode);
                                }
                                return ;
                            }
                            else if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                                Log.i("","==登陆成功");
                            }
                        }
                    });

                    //IM接收消息监听
                    ECDevice.setOnChatReceiveListener(new OnChatReceiveListener() {
                        @Override
                        public void OnReceivedMessage(ECMessage msg) {
                            Log.i("","==收到新消息");
                            if(msg == null) {
                                return ;
                            }
                            // 接收到的IM消息，根据IM消息类型做不同的处理(IM消息类型：ECMessage.Type)
                            ECMessage.Type type = msg.getType();
                            if(type == ECMessage.Type.TXT) {
                                // 在这里处理文本消息
                                ECTextMessageBody textMessageBody = (ECTextMessageBody) msg.getBody();
                                Msg msg2 = new Msg(textMessageBody.getMessage(), Msg.TYPE_RECEIVED);
                                msgList.add(msg2);
                                msgAdapter.notifyItemInserted(msgList.size() - 1);  //当有消息时，刷新ListView显示
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);  //将ListView定位到最后一行
                            }  else {
                                Log.e("ECSDK_Demo" , "Can't handle msgType=" + type.name()
                                        + " , then ignore.");
                            }
                            // 根据不同类型处理完消息之后，将消息序列化到本地存储（sqlite）
                            // 通知UI有新消息到达
                        }

                        @Override
                        public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage notice) {
                            //收到群组通知消息,可以根据ECGroupNoticeMessage.ECGroupMessageType类型区分不同消息类型
                            Log.i("","==收到群组通知消息（有人加入、退出...）");
                        }

                        @Override
                        public void onOfflineMessageCount(int count) {
                            // 登陆成功之后SDK回调该接口通知帐号离线消息数
                        }

                        @Override
                        public int onGetOfflineMessage() {
                            return 0;
                        }

                        @Override
                        public void onSoftVersion(String s, int i) {

                        }

                        @Override
                        public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {

                        }

                        @Override
                        public void onReceiveDeskMessage(ECMessage message) {

                        }

                        @Override
                        public void onReceiveOfflineMessage(List msgs) {
                            // SDK根据应用设置的离线消息拉取规则通知应用离线消息
                        }

                        @Override
                        public void onReceiveOfflineMessageCompletion() {
                            // SDK通知应用离线消息拉取完成
                        }

                        @Override
                        public void onServicePersonVersion(int version) {
                            // SDK通知应用当前帐号的个人信息版本号
                        }
                    });

                    //验证参数是否正确
                    if(params.validate()) {
                        // 登录函数
                        ECDevice.login(params);
                    }
                }
                @Override
                public void onError(Exception exception) {
                    //在初始化错误的方法中打印错误原因
                    Log.i("","初始化SDK失败"+exception.getMessage());
                }
            });
        }
        // 已经初始化成功，后续开发业务代码。
        Log.i("", "初始化SDK及登陆代码完成");
    }

}
