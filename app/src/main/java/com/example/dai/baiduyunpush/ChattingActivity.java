package com.example.dai.baiduyunpush;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dai.baiduyunpush.client.PushTestReceiver;
import com.example.dai.baiduyunpush.component.ChatMessage;
import com.example.dai.baiduyunpush.component.ChatMessageAdapter;
import com.example.dai.baiduyunpush.component.MyApplication;
import com.example.dai.baiduyunpush.component.myMessage;
import com.example.dai.baiduyunpush.mysqlite.MessageDB;
import com.example.dai.baiduyunpush.server.SendMsgAsyncTask;
import com.example.dai.baiduyunpush.someUser.User;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by dai on 2016/1/12.
 */
public class ChattingActivity extends Activity implements PushTestReceiver.onNewMessageListener,
        SwipeRefreshLayout.OnRefreshListener{

    private ListView mChatMessagesListView;
    private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    private ChatMessageAdapter mAdapter;

    private EditText mMsgInput;

    //数据库当前页数
    private int page_count;
    //数据库分页大小
    private int page_size;

    //好友信息
    private User friend;

    //下拉刷新控件
    private SwipeRefreshLayout refresh_layout;

    //信息存储数据库
    private MessageDB messageDB;

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.d("push","call back");
            mAdapter.notifyDataSetChanged();
            mChatMessagesListView.setSelection(mDatas.size() - 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chatting);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        friend = (User)bundle.getSerializable("user");

        //数据库初始化
        messageDB = new MessageDB(this);
        initView();
    }

    @Override
    protected void onDestroy(){
        //activity结束时销毁监听器
        PushTestReceiver.msgListeners.remove(this);
        super.onDestroy();
    }

    private void initView(){
        //设置标题栏好友名
        TextView title = (TextView)findViewById(R.id.id_nickname);
        title.setText(friend.getNick());

        //获取下拉刷新控件
        refresh_layout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        //设置跑动的颜色值
        refresh_layout.setColorSchemeResources(R.color.colorGreen, R.color.colorGray, R.color.colorSkyblue, R.color.colorPrimary);
        //监听下拉刷新时间
        refresh_layout.setOnRefreshListener(ChattingActivity.this);

        //设置分页页数
        page_count = 1;
        //设置分页大小
        page_size = 6;

        mChatMessagesListView = (ListView) findViewById(R.id.id_chat_listView);
        mDatas = new ArrayList();

        //从数据库读出数据生成listview
        mDatas.addAll(messageDB.find(friend.getUserId(),page_count,page_size));

        mAdapter = new ChatMessageAdapter(this, mDatas);
        mChatMessagesListView.setAdapter(mAdapter);
        mChatMessagesListView.setSelection(mDatas.size() - 1);
        //将这个实现接口的监听器存入PushTestReceiver的监听器列表中
        PushTestReceiver.msgListeners.add(this);
        //输入框
        mMsgInput = (EditText) findViewById(R.id.id_chat_msg);
        //绑定发送按钮事件
        Button send_btn = (Button)findViewById(R.id.id_chat_send);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsgInput.getText().toString();
                if (TextUtils.isEmpty(msg))
                {
                    return;
                }
                myMessage mymessage = new myMessage(System.currentTimeMillis(), msg,
                        MyApplication.getInstance().getCurrentUser().getUserId());
                new SendMsgAsyncTask(JSON.toJSONString(mymessage),friend.getUserId()).send();
                //处理时间
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setDate(new Date());
                chatMessage.setComing(false);
                chatMessage.setReaded(true);
                chatMessage.setMessage(msg);
                mDatas.add(chatMessage);
                mAdapter.notifyDataSetChanged();
                mChatMessagesListView.setSelection(mDatas.size() - 1);
                //写入数据库
                messageDB.add(friend.getUserId(),chatMessage);
                mMsgInput.setText("");
            }
        });

        //将该好友的所有未读消息从全局未读消息总数中去掉
        MyApplication.getInstance().delAll_uread(
                        messageDB.getUnreadedMsgsCountByUserId(friend.getUserId()));
        //将该friend的数据库表中所有未读信息置为已读
        messageDB.updateReaded(friend.getUserId());
    }

    @Override
    public void onNewMessage(myMessage message) {
        //如果message的发信人id是这个界面的friend_id，则处理，否则不处理
        if(!friend.getUserId().equals(message.getUserId())){
            return;
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setComing(true);
        chatMessage.setDate(new Date(message.getTimeSamp()));
        chatMessage.setMessage(message.getMessage());
        chatMessage.setUserId(message.getUserId());
        chatMessage.setReaded(true);
        mDatas.add(chatMessage);
        //将该friend的数据库表中所有未读信息置为已读
        messageDB.updateReaded(friend.getUserId());
        //全局未读消息减1
        MyApplication.getInstance().delAll_uread(1);
        Log.d("thread", Thread.currentThread().getName());
        //调用handler处理ui
        Message msg = new Message();
        mhandler.sendMessage(msg);
    }

    @Override
    public void onRefresh() {
        page_count++;
        //遍历数据库取出的数组，并插入到当前数组前面
        //注意：数据库返回按_id从大到小，但由于再次遍历向前插入，导致顺序混乱了
        List<ChatMessage> li = messageDB.find(friend.getUserId(),page_count,page_size);
        if(li.size() != 0){
            //所以需要先将取出的数组逆序。
            Collections.reverse(li);
            for(ChatMessage c : li){
                mDatas.add(0,c);
            }
            mAdapter.notifyDataSetChanged();
            //设置偏移量,即刷新前信息的位置
            mChatMessagesListView.setSelection(li.size());
        } else{
            //没有更多信息，去掉加载更多
            TextView get_more = (TextView) findViewById(R.id.chat_get_more);
            //两种方法都可以去掉get_more,但是使用remove后如果再次刷新，会因为对象已经移除而报错
            //使用GONE则控件仍然存在，但是不影响布局
            get_more.setVisibility(View.GONE);
            //ViewGroup parent = (ViewGroup)get_more.getParent();
            //parent.removeView(get_more);
        }
        refresh_layout.setRefreshing(false);
    }

    /**
     * 监听键盘返回键事件
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            this.finish();
            startActivity(intent);
        }
        return true;
    }
}
