package com.example.dai.baiduyunpush.myFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dai.baiduyunpush.ChattingActivity;
import com.example.dai.baiduyunpush.R;
import com.example.dai.baiduyunpush.adapter.Friends.FriendsListAdapter;
import com.example.dai.baiduyunpush.someUser.User;
import com.example.dai.baiduyunpush.mysqlite.FriendDB;

import java.util.List;

/**
 * Created by dai on 2016/1/18.
 */
public class FriendsFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friends_layout,container,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = (ListView)view.findViewById(R.id.friends_list);
        List<User> users; //= new ArrayList<User>();
        FriendDB db = new FriendDB(getActivity());
        users = db.getFriend();
        //users.add(new User("829568832181121768","channelID1","代浩1"));
        //users.add(new User("userID2","channelID2","代浩2"));
        //users.add(new User("userID3","channelID3","代浩3"));
        FriendsListAdapter adapter = new FriendsListAdapter(getActivity(),users,listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView userinfo = (TextView)view.findViewById(R.id.user_info);
                //JSON反序列化提取User
                User uu = JSON.parseObject(userinfo.getText().toString(),User.class);
                Intent intent = new Intent();
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user", uu);
                intent.putExtras(mBundle);
                intent.setClass(getActivity(), ChattingActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });
    }
}
