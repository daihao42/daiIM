package com.example.dai.baiduyunpush;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dai.baiduyunpush.component.MyApplication;
import com.example.dai.baiduyunpush.someUser.CurrentUser;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dai on 2016/1/13.
 * 使用九宫格锁屏
 */
public class LoginActivity extends Activity{

    //声明变量以获取全局Application
    protected MyApplication app;

    private Button login_confirm;
    private TextView title;
    private EditText input;
    private EditText register_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //获取全局Application
        this.app = (MyApplication) getApplication();
        initView();

        if(checkSharedPerences()){
            confirm();
        }
        else{
            register();
        }
    }

    private void initView(){
        title = (TextView)findViewById(R.id.login_title);
        input = (EditText)findViewById(R.id.login_editText);
        register_text = (EditText)findViewById(R.id.register_editText2);
        login_confirm = (Button)findViewById(R.id.confirm_button);
    }

    /**
     * 检查SharedPreferences是否存在记录的用户
     * 存在则将用户保存到Application并置为登录
     * 不存在则需要设置密码
     */
    private boolean checkSharedPerences(){
        SharedPreferences savecurrentuser = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        //检查密码是否存在，存在则读入所有信息
        if(savecurrentuser.contains("password")){
            Map<String, String> m = (Map<String,String>) savecurrentuser.getAll();
            CurrentUser c = new CurrentUser();
            c.setUserId(m.get("userID"));
            c.setPassword(m.get("password"));
            c.setChannelId(m.get("channelID"));
            c.setNick(m.get("nickname"));
            app.setCurrentUser(c);
            app.setLogin(true);
            return true;
        }
        else{
            return false;
        }
    }

    //验证密码是否正确
    private void confirm(){
        title.setText("请登录");
        register_text.setVisibility(View.INVISIBLE);

        //自动弹出键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 300);

        login_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = input.getText().toString();
                if(TextUtils.isEmpty(password)){
                    input.setError("请输入密码");
                    input.setFocusable(true);
                    return;
                } else{
                    if(password.equals(app.getCurrentUser().getPassword())){
                        //密码正确，跳转主activity
                        jumpToMain();
                    }else{
                        input.setText("");
                        input.setError("密码不正确！！");
                        return;
                    }
                }
            }
        });
    }

    private void jumpToMain(){
        //将当前Activity移出栈
        this.finish();
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        //清除所有activity栈
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void register(){
        title.setText("输入昵称和新密码");
        //input.setVisibility(View.INVISIBLE);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        //自动弹出键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) register_text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 300);

        login_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = input.getText().toString();
                String password = register_text.getText().toString();
                if(TextUtils.isEmpty(password)){
                    register_text.setError("请输入密码");
                    register_text.setFocusable(true);
                    return;
                } else if(TextUtils.isEmpty(nickname)){
                    input.setError("请输入昵称");
                    input.setFocusable(true);
                }
                else{
                    CurrentUser u = new CurrentUser();
                    u.setPassword(password);
                    u.setNick(nickname);
                    app.setCurrentUser(u);
                    jumpToMain();
                }
            }
        });
    }
}
