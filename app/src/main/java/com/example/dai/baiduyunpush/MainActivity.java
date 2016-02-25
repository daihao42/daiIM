package com.example.dai.baiduyunpush;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.example.dai.baiduyunpush.adapter.myFragmentPagerAdapter;
import com.example.dai.baiduyunpush.client.PushTestReceiver;
import com.example.dai.baiduyunpush.component.MyApplication;
import com.example.dai.baiduyunpush.component.myMessage;
import com.example.dai.baiduyunpush.myFragment.RecentContactFragment;
import com.example.dai.baiduyunpush.myFragment.FriendsFragment;
import com.example.dai.baiduyunpush.mysqlite.FriendDB;
import com.example.dai.baiduyunpush.someUser.CurrentUser;
import com.example.dai.baiduyunpush.someUser.User;
import com.readystatesoftware.viewbadger.BadgeView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements
                    PushTestReceiver.onNewMessageListener {
    //声明变量以获取全局Application
    protected MyApplication app;

    //滑动页卡
    private View recon_view,friends_view;

    //viewPager指示器
    private PagerTabStrip pagerTabStrip;

    //存放滑动页卡的list
    private List<View> viewList;
    private List<Fragment> fragmentList;

    //存放滑动页卡的标题
    private List<String> titleList;

    //viewpager
    private ViewPager viewPager;

    private TextView all_unread;

    //
    private myFragmentPagerAdapter fragmentPagerAdapter;

    private BadgeView badgeView;

    private boolean Is_Friends;

    //扫一扫需要的
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(Is_Friends){
                all_unread.setText(app.getAll_unread().toString());
            }
//            badgeView = new BadgeView(MainActivity.this,all_unread);;
//            badgeView.setText(app.getAll_unread().toString());
//            badgeView.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //获取全局Application
        this.app = (MyApplication) getApplication();

        //启动云推送
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                getString(R.string.api_key));

        //启动service
        this.startService(new Intent(this,SaveService.class));

        initViews();

        PushTestReceiver.msgListeners.add(this);
        app.setDo_it_once(0);
    }

    private void initViews(){
        all_unread = (TextView)findViewById(R.id.all_unread);


        LayoutInflater lf = getLayoutInflater().from(this);
        recon_view = lf.inflate(R.layout.recentcontact_layout,null);
        friends_view = lf.inflate(R.layout.friends_layout,null);

        //使用fragment而不是直接添加view，是因为可以在fragment的start方法中调用一些初始化界面的方法，
        // 而不用担心会因为不存在控件而报错
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new RecentContactFragment());
        fragmentList.add(new FriendsFragment());

        titleList = new ArrayList<String>();
        titleList.add("最近");
        titleList.add("联系人");

        myFragmentPagerAdapter.fragmentList = fragmentList;
        myFragmentPagerAdapter.titleList = titleList;
        fragmentPagerAdapter = new myFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        viewPager.setAdapter(fragmentPagerAdapter);

        //设置滑动监听器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
//                    case 1: badgeView = new BadgeView(MainActivity.this,all_unread);;
//                            badgeView.setText(app.getAll_unread().toString());
//                            badgeView.show();
                    case 0:
                        Is_Friends = false;
                        all_unread.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        Is_Friends = true;
                        all_unread.setText(app.getAll_unread().toString());
                        all_unread.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置所有未读数
        all_unread.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //添加新朋友
        if (id == R.id.action_add_friend){
            //跳转添加朋友界面
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AddFriendActivity.class);
            startActivity(intent);
        }

        //我的名片
        if(id == R.id.action_mycard){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MyCardActivity.class);
            startActivity(intent);
        }

        //扫一扫
        if(id == R.id.action_saoyicao){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 程序退出时记录当前用户信息到SharedPreferences
     */
    @Override
    protected void onStop(){
        PushTestReceiver.msgListeners.remove(this);
        SharedPreferences savecurrentuser = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        CurrentUser c = app.getCurrentUser();
        savecurrentuser.edit()
                .putString("userID", c.getUserId())
                .putString("password",c.getPassword())
                .putString("channelID",c.getChannelId())
                .putString("nickname",c.getNick())
                .commit();
        super.onStop();
    }

    @Override
    public void onNewMessage(myMessage myMessage) {
        //调用handler处理ui
        Message msg = new Message();
        mhandler.sendMessage(msg);
    }

    /**
     * 确认添加好友并设置昵称
     */
    private void confirmAddFriend(final User u){
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);//提示框
        final View view = factory.inflate(R.layout.edit_dialog, null);//这里必须是final的
        final EditText edit=(EditText)view.findViewById(R.id.editText);//获得输入框对象
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("确认添加好友："+u.getNick()+" ?\n设置昵称：")//提示框标题
                .setView(view)
                .setPositiveButton("确定",//提示框的两个按钮
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确认，对话框消失，执行函数
                                String nick = edit.getText().toString();
                                if(TextUtils.isEmpty(nick)){
                                    edit.setError("请输入昵称");
                                    try {
                                        //不关闭
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, false);
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                    u.setNick(nick);
                                    FriendDB db = new FriendDB(MainActivity.this);
                                    db.addFriend(u);
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        })
                .setCancelable(false).create().show();
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SCANNIN_GREQUEST_CODE :
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示结果
                    try{
                        User friend = JSON.parseObject(bundle.getString("result"),User.class);
                        confirmAddFriend(friend);
                    } catch (Exception e){

                    }
                }
        }
    }
}




