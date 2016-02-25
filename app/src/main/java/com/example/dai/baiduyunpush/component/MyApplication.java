package com.example.dai.baiduyunpush.component;

import android.app.Application;
import android.util.Log;

import com.example.dai.baiduyunpush.mysqlite.MessageDB;
import com.example.dai.baiduyunpush.server.BaiduPush;
import com.example.dai.baiduyunpush.someUser.CurrentUser;

/**
 * Created by dai on 2016/1/14.
 */
public class MyApplication extends Application {

    /**
     * API_KEY
     */
    public final static String API_KEY = "18bG9V4cZc2rNUh02ea3o1VQ";
    /**
     * SECRET_KEY
     */
    public final static String SECRIT_KEY = "fw8I8KREHj4UboflAMGvAZVBQxE1ZTdm";

    //百度云推送
    private BaiduPush mBaiduPushServer;

    private static MyApplication mApplication;

    //所有未读消息
    private int all_unread = 0;

    private boolean Is_Login;
    private CurrentUser currentUser = new CurrentUser();

    //
    private MessageDB messageDB;

    //只做一次的任务
    private int do_it_once = 1;

    @Override
    public void onCreate(){
        //调用父类onCreate完成基本初始化
        super.onCreate();
        //默认程序开始处于未登录状态
        setLogin(false);

        mApplication = this;

    }

    //保证获取application单件实例
    public synchronized static MyApplication getInstance()
    {
        return mApplication;
    }

    //保证获取百度云推送单件实例
    public synchronized BaiduPush getBaiduPush()
    {
        if (mBaiduPushServer == null)
            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
                    SECRIT_KEY, API_KEY);
        return mBaiduPushServer;
    }


    /**
     * 设置登录状态
     * @param is_login 表示登录状态
     */
    public void setLogin(boolean is_login){
        this.Is_Login = is_login;
    }

    /**
     * 获取登录状态
     * @return 返回boolean
     */
    public boolean getLogin(){
        return this.Is_Login;
    }

    /**
     * 设置当前用户
     */
    public void setCurrentUser(CurrentUser c){
        currentUser = c;
    }

    /**
     * 获取当前用户
     */
    public CurrentUser getCurrentUser(){
        return currentUser;
    }

    /**
     * 清理当前用户
     */
    public void clearCurrentUser(){
        Log.d("call", "clear");
        currentUser = new CurrentUser();
    }


    /**
     * 未读消息的设置
     */
    public Integer getAll_unread() {
        return all_unread;
    }

    public void setAll_unread(Integer all_unread) {
        this.all_unread = all_unread;
    }

    //增加未读消息
    public void addAll_unread(int i){
        this.all_unread = this.all_unread + i;
    }

    //减去未读消息
    public void delAll_uread(int i){
        this.all_unread = this.all_unread - i;
    }

    public int getDo_it_once() {
        return this.do_it_once;
    }

    public void setDo_it_once(int do_it_once) {
        this.do_it_once = do_it_once;
    }
}
