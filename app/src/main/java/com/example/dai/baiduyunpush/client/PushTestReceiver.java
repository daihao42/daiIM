package com.example.dai.baiduyunpush.client;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.example.dai.baiduyunpush.component.MyApplication;
import com.example.dai.baiduyunpush.component.myMessage;
import com.example.dai.baiduyunpush.someUser.CurrentUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dai on 2016/1/11.
 */
public class PushTestReceiver extends PushMessageReceiver{

    //定义一个接口作为回调函数以供activity使用
    public interface onNewMessageListener{
        void onNewMessage(myMessage myMessage);
    }

    //定义内部实现回调函数
    public static ArrayList<onNewMessageListener> msgListeners = new ArrayList<onNewMessageListener>();

    public static int test = 0;

    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        Log.d("push", i + " " + s + " " + s1 + " " + s2 + " " + s3);
        Log.d("thread", Thread.currentThread().getName());
        //在onBind中更新当前用户的userID和channelID
        if(s1 != null && s2 != null){
            MyApplication.getInstance().getCurrentUser().setUserId(s1);
            MyApplication.getInstance().getCurrentUser().setChannelId(s2);
        }
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {
        //透传消息，主要的处理逻辑都用透传
        Log.d("push", "透传" + s + " " + s1);
        myMessage msg = JSON.parseObject(s, myMessage.class);
        if (msgListeners.size() > 0)
        {
            for (int i = 0; i < msgListeners.size(); i++)
                msgListeners.get(i).onNewMessage(msg);
        }
        Log.d("push","call");
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Log.d("push","通知"+" "+s+" "+s1+" "+s2+" ");
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }

}
