package com.example.dai.baiduyunpush.mysqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dai.baiduyunpush.someUser.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dai on 2016/1/15.
 * 好友列表
 */
public class FriendDB extends SQLiteOpenHelper{
    //数据库名
    private static final String DATABASE_NAME = "daiIM";
    //版本号
    private static final int DATABASE_VERSION = 1;
    //表名
    private static final String TABLE_NAME = "friend";
    //表初始化
    private static final String TABLE_CREATE = "CREATE TABLE "+TABLE_NAME+" ("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "userID TEXT NOT NULL,"+
            "channelID TEXT,"+
            "nickName TEXT NOT NULL);";

    public FriendDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    //插入数据
    public long addFriend(User u){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        //根据ID判断记录是否已经存在
        Cursor cursor = db.query(TABLE_NAME,new String[]{"userID"},"userID=?",new String[]{u.getUserId()},null,null,null);
        //如果已经存在就不插入
        if(cursor.moveToFirst()){
            return 0;
        }
        values.put("userID",u.getUserId());
        values.put("channelID",u.getChannelId());
        values.put("nickName", u.getNick());
        long rowID = db.insert(TABLE_NAME,null,values);
        db.close();
        return rowID;
    }

    public User getFriend(String userId)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+TABLE_NAME+" where userID=?",
                new String[] { userId });
        User u = new User();
        if (c.moveToNext())
        {
            u.setUserId(c.getString(c.getColumnIndex("userID")));
            u.setNick(c.getString(c.getColumnIndex("nickName")));
            u.setChannelId(c.getString(c.getColumnIndex("channelID")));
        }
        return u;
    }

    public List<User> getFriend()
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+TABLE_NAME, null);
        List<User> list = new LinkedList<User>();
        while(c.moveToNext())
        {
            User u = new User();
            u.setUserId(c.getString(c.getColumnIndex("userID")));
            u.setNick(c.getString(c.getColumnIndex("nickName")));
            u.setChannelId(c.getString(c.getColumnIndex("channelID")));
            list.add(u);
        }
        return list;
    }
}
