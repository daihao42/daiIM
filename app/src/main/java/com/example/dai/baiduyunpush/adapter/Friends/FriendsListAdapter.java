package com.example.dai.baiduyunpush.adapter.Friends;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dai.baiduyunpush.R;
import com.example.dai.baiduyunpush.someUser.User;

import java.util.List;

/**
 * Created by dai on 2016/1/16.
 */
public class FriendsListAdapter extends ArrayAdapter<User>{

    private ListView listView;
    private Activity activity;

    public FriendsListAdapter(Activity activity,List<User> friends_list, ListView listView) {
        super(activity, 0,friends_list);
        this.listView = listView;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        LayoutInflater inflater = activity.getLayoutInflater();
        rowView = inflater.inflate(R.layout.friends_item,null);
        User user = getItem(position);
        TextView text = (TextView)rowView.findViewById(R.id.friend_item);
        text.setText(user.getNick());
        TextView userinfo = (TextView)rowView.findViewById(R.id.user_info);
        //JSON序列化user方便传值
        userinfo.setText(JSON.toJSONString(user));
        return rowView;
    }
}
