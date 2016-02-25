package com.example.dai.baiduyunpush.mysqlite;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dai.baiduyunpush.component.ChatMessage;


public class MessageDB
{
    /**
     * 存储聊天信息
     */
    private static final String COL_MESSAGE = "message";
    //接收或者发送标志位
    private static final String COL_IS_COMING = "is_coming";
    private static final String COL_USER_ID = "user_id";
    // 1:readed ; 0 unreaded ;
    private static final String COL_READED = "readed";
    private static final String COL_DATE = "date";

    private static final String DB_NAME = "daiIM";
    private SQLiteDatabase mDb;

    public MessageDB(Context context)
    {
        mDb = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    /**
     * 增加一条记录
     * @param userId
     * @param chatMessage
     */
    public void add(String userId, ChatMessage chatMessage)
    {
        createTable(userId);

        int isComing = chatMessage.isComing() ? 1 : 0;
        int readed = chatMessage.isReaded() ? 1 : 0;
        mDb.execSQL(
                "insert into _" + userId + " (" + COL_USER_ID + ","
                         + COL_IS_COMING + "," + COL_MESSAGE + ","
                         + COL_READED + "," + COL_DATE
                        + ") values(?,?,?,?,?)",
                new Object[] { chatMessage.getUserId(),
                        isComing, chatMessage.getMessage(), readed,
                        chatMessage.getDateStr() });
    }

    public List<ChatMessage> find(String userId, int currentPage, int pageSize)
    {
        /**
         * 注:这里出现了sql语法错误，select * from limit 6,12 并不等同于：
         *                       select * from limit 6 offset 12
         * 后面的12并不代表end，而是代表偏移量，所以会取出12个数据。
         * 所以应该用pageSize作为偏移量。
         * 使用逗号，前一个为基准点，后一个为偏移量
         * 使用offset则前一个为偏移量，后一个为基准点
         */
        List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
        createTable(userId);
        int start = (currentPage - 1) * pageSize;
        //修改end为偏移量offset
        //int end = start + pageSize;
        int offset = pageSize;
        // 按页数取数据
        //String sql = "select * from _" + userId + " order by _id  desc limit  "
        //        + start + " offset " + end;
        String sql = "select * from _" + userId + " order by _id  desc limit  "
                + start + " , " + offset;
        Cursor c = mDb.rawQuery(sql, null);
        ChatMessage chatMessage = null;
        while (c.moveToNext())
        {
            String dateStr = c.getString(c.getColumnIndex(COL_DATE));
            int isComingVal = c.getInt(c.getColumnIndex(COL_IS_COMING));
            String message = c.getString(c.getColumnIndex(COL_MESSAGE));
            int readedVal = c.getInt(c.getColumnIndex(COL_READED));
            String _userId = c.getString(c.getColumnIndex(COL_USER_ID));

            chatMessage = new ChatMessage(message, isComingVal == 1, _userId,
                     readedVal == 1, dateStr);

            chatMessages.add(chatMessage);
        }
        Collections.reverse(chatMessages);
        return chatMessages;
    }

    public Map<String, Integer> getUserUnReadMsgs(List<String> userIds)
    {

        Map<String, Integer> userUnReadMsgs = new HashMap<String, Integer>();
        for (String userId : userIds)
        {
            int count = getUnreadedMsgsCountByUserId(userId);
            userUnReadMsgs.put(userId, count);
        }

        return userUnReadMsgs;
    }

    public int getUnreadedMsgsCountByUserId(String userId)
    {
        createTable(userId);
        String sql = "select count(*) as count from _" + userId + " where "
                + COL_IS_COMING + " = 1 and " + COL_READED + " = 0";
        Cursor c = mDb.rawQuery(sql, null);
        int count = 0;
        if (c.moveToNext())
            count = c.getInt(c.getColumnIndex("count"));
        c.close();
        return count;
    }

    /**
     * 清除所有未读标志
     */
    public void updateReaded(String userId)
    {
        createTable(userId);
        mDb.execSQL("update  _" + userId + " set " + COL_READED + " = 1 where "
                + COL_READED + " = 0 ", new Object[]{});
    }

    private void createTable(String userId)
    {
        mDb.execSQL("CREATE table IF NOT EXISTS _" + userId
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " //
                + COL_USER_ID + " TEXT, " //
                + COL_IS_COMING + " integer ,"//
                + COL_MESSAGE + " text , " //
                + COL_DATE + " text , "//
                + COL_READED + " integer); ");//
    }

    public void close()
    {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }

    /**
     * 查询最新的一条信息
     * param String userId
     */
    public ChatMessage getNewOne(String userId){
        try {
            String sql = "select * from _" + userId + " order by _id  desc limit 1";
            Cursor c = mDb.rawQuery(sql, null);
            ChatMessage chatMessage = null;
            if(c.moveToFirst()) {
                String dateStr = c.getString(c.getColumnIndex(COL_DATE));
                int isComingVal = c.getInt(c.getColumnIndex(COL_IS_COMING));
                String message = c.getString(c.getColumnIndex(COL_MESSAGE));
                int readedVal = c.getInt(c.getColumnIndex(COL_READED));
                String _userId = c.getString(c.getColumnIndex(COL_USER_ID));

                chatMessage = new ChatMessage(message, isComingVal == 1, _userId,
                        readedVal == 1, dateStr);
                return chatMessage;
            } else{
                return chatMessage;
            }
        } catch (Exception e){
            return null;
        }
    }

}
