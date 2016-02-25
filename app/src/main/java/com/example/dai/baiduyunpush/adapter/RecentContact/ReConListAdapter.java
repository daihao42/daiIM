package com.example.dai.baiduyunpush.adapter.RecentContact;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dai.baiduyunpush.R;
import com.example.dai.baiduyunpush.component.MyApplication;
import com.example.dai.baiduyunpush.mysqlite.MessageDB;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dai on 2016/1/16.
 */
public class ReConListAdapter extends ArrayAdapter<ReConItem> {

    private Activity activity;

    private ListView listView;

    private MessageDB messageDB;

    private Map<String,Integer> unread_msg;

    public ReConListAdapter(Activity activity, List<ReConItem> itemList,ListView listView) {
        super(activity, 0 , itemList);
        this.activity = activity;
        this.listView = listView;
        //查询未读消息
        messageDB = new MessageDB(activity);
        List<String> userIds = getUserList(itemList);
        unread_msg = messageDB.getUserUnReadMsgs(userIds);
    }

    private List<String> getUserList(List<ReConItem> list){
        List<String> li = new ArrayList<String>();
        for(ReConItem i : list){
            li.add(i.getUserId());
        }
        return li;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        LayoutInflater inflater = activity.getLayoutInflater();
        rowView = inflater.inflate(R.layout.recentcontact_item,null);
        ReConItem item = getItem(position);
        TextView nickName = (TextView)rowView.findViewById(R.id.rec_nickname);
        TextView rec_date = (TextView)rowView.findViewById(R.id.rec_createDate);
        TextView rec_message = (TextView)rowView.findViewById(R.id.rec_message);
        TextView userId = (TextView)rowView.findViewById(R.id.rec_userId);
        nickName.setText(item.getNickName());
        rec_date.setText(item.getDateStr());
        rec_message.setText(item.getMessage());
        userId.setText(item.getUserId());

        //未读标志
        TextView unread = (TextView)rowView.findViewById(R.id.unread_msg);
        BadgeView badgeView = new BadgeView(activity,unread);
        Integer i = unread_msg.get(item.getUserId());
        if(i != 0){
            badgeView.setText(i.toString());
            badgeView.show();
            //将未读消息统计进全局未读消息总数
            if(MyApplication.getInstance().getDo_it_once() == 1){
                MyApplication.getInstance().addAll_unread(i);
            }
        }

        return rowView;
    }
}
