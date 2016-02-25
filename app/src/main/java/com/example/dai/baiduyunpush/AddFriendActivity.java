package com.example.dai.baiduyunpush;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dai.baiduyunpush.mysqlite.FriendDB;
import com.example.dai.baiduyunpush.someUser.User;

/**
 * Created by dai on 2016/1/20.
 */
public class AddFriendActivity extends Activity{
    private EditText userId;
    private EditText nick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);
        initView();
    }

    private void initView(){
        userId = (EditText)findViewById(R.id.add_user_id);
        nick = (EditText)findViewById(R.id.add_user_nick);
        Button confirm = (Button) findViewById(R.id.add_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userId.getText().toString())) {
                    userId.setError("请输入用户号！");
                    return;
                }
                if (TextUtils.isEmpty(nick.getText().toString())) {
                    nick.setError("请输入用户备注");
                    return;
                }
                addFriend(userId.getText().toString(),nick.getText().toString());
                //处理二维码
            }
        });
    }

    private void addFriend(String userId,String nick){
        User u = new User();
        u.setUserId(userId);
        u.setNick(nick);
        FriendDB db = new FriendDB(AddFriendActivity.this);
        db.addFriend(u);
        //跳转主界面
        AddFriendActivity.this.finish();
        Intent intent = new Intent();
        intent.setClass(AddFriendActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
