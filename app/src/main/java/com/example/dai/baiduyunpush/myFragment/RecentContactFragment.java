package com.example.dai.baiduyunpush.myFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dai.baiduyunpush.ChattingActivity;
import com.example.dai.baiduyunpush.R;
import com.example.dai.baiduyunpush.adapter.RecentContact.ReConItem;
import com.example.dai.baiduyunpush.adapter.RecentContact.ReConListAdapter;
import com.example.dai.baiduyunpush.client.PushTestReceiver;
import com.example.dai.baiduyunpush.component.ChatMessage;
import com.example.dai.baiduyunpush.component.MyApplication;
import com.example.dai.baiduyunpush.component.myMessage;
import com.example.dai.baiduyunpush.mysqlite.MessageDB;
import com.example.dai.baiduyunpush.mysqlite.FriendDB;
import com.example.dai.baiduyunpush.someUser.User;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dai on 2016/1/18.
 */
public class RecentContactFragment extends Fragment implements PushTestReceiver.onNewMessageListener{
    private View view;

    private ListView listView;

    //信息数据库
    private MessageDB messageDB;

    //好友数据库
    private FriendDB friendDB;

    //ListView适配器
    private ReConListAdapter adapter;

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            List<ReConItem> reConItems = getRecent();
            try{
                adapter = new ReConListAdapter(getActivity(),reConItems,listView);
                listView.setAdapter(adapter);
            }catch (Exception e){

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recentcontact_layout,container,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //生成DB
        messageDB = new MessageDB(getActivity());
        friendDB = new FriendDB(getActivity());

        listView = (ListView) view.findViewById(R.id.recentContact_list);

        List<ReConItem> reConItems = getRecent();
        adapter = new ReConListAdapter(getActivity(),reConItems,listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView nickname = (TextView) view.findViewById(R.id.rec_nickname);
                TextView userId = (TextView) view.findViewById(R.id.rec_userId);
                User uu = new User();
                uu.setNick(nickname.getText().toString());
                uu.setUserId(userId.getText().toString());
                Intent intent = new Intent();
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user", uu);
                intent.putExtras(mBundle);
                intent.setClass(getActivity(), ChattingActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });

        //添加onNewMessage监听器
        PushTestReceiver.msgListeners.add(this);
    }

    private List<ReConItem> getRecent(){
        List<ReConItem> list = new ArrayList<ReConItem>();

        //遍历好友列表，每个好友从中获取最新一条信息填入ReConItem
        for(User u : friendDB.getFriend()){
            ChatMessage c = messageDB.getNewOne(u.getUserId());
            if(c == null){
                continue;
            } else {
                ReConItem r = new ReConItem();
                r.setNickName(u.getNick());
                r.setUserId(u.getUserId());
                r.setMessage(c.getMessage());
                r.setDateStr(c.getDateStr());
                list.add(r);
            }
        }
        Collections.sort(list);
        Collections.reverse(list);
        return list;
    }

    @Override
    public void onDestroy() {
        PushTestReceiver.msgListeners.remove(this);
        super.onDestroy();
    }

    @Override
    public void onNewMessage(myMessage myMessage) {
        //更新listview
        //调用handler处理ui
        Message msg = new Message();
        mhandler.sendMessage(msg);
    }
}
