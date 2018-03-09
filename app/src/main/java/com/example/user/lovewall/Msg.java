package com.example.user.lovewall;

import java.lang.reflect.Type;

/**
 *
 *@作者： Bob Du
 *@创建时间：2017/12/19 11:20
 *@文件名：Msg.java
 *@功能：消息实体类
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;
    private String content;
    private int type;

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}