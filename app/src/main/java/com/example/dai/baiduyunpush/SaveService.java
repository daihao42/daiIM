package com.example.dai.baiduyunpush;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.support.annotation.Nullable;

import com.example.dai.baiduyunpush.client.PushTestReceiver;
import com.example.dai.baiduyunpush.component.ChatMessage;
import com.example.dai.baiduyunpush.component.MyApplication;
import com.example.dai.baiduyunpush.component.myMessage;
import com.example.dai.baiduyunpush.mysqlite.MessageDB;

import java.util.Date;

/**
 * Created by dai on 2016/1/20.
 * 将所有新信息存储都放到service中
 */
public class SaveService extends Service implements PushTestReceiver.onNewMessageListener {
    private MessageDB messageDB;

    @Override
    public void onCreate() {
        super.onCreate();
        messageDB = new MessageDB(this);
        //将这个实现接口的监听器存入PushTestReceiver的监听器列表中
        PushTestReceiver.msgListeners.add(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //activity结束时销毁监听器
        PushTestReceiver.msgListeners.remove(this);
        super.onDestroy();
    }

    //所有信息都设置为未读，根据信息中带的发信人id存入对应数据库
    @Override
    public void onNewMessage(myMessage message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setComing(true);
        chatMessage.setDate(new Date(message.getTimeSamp()));
        chatMessage.setMessage(message.getMessage());
        chatMessage.setUserId(message.getUserId());
        chatMessage.setReaded(false);
        Log.d("Save","call");
        //写入数据库
        messageDB.add(message.getUserId(),chatMessage);
        //全局未读消息加1
        MyApplication.getInstance().addAll_unread(1);
    }
}
