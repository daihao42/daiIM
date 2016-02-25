package com.example.dai.baiduyunpush.component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dai on 2016/1/12.
 */
public class ChatMessage {
    private String message;
    private boolean isComing;
    private Date date;
    private String userId;
    public void setDateStr(String dateStr)
    {
        this.dateStr = dateStr;
    }

    private boolean readed;
    private String dateStr;

    public ChatMessage()
    {
    }

    public ChatMessage(String message, boolean isComing,
                       String userId, boolean readed,
                       String dateStr)
    {
        super();
        this.message = message;
        this.isComing = isComing;
        this.userId = userId;
        this.readed = readed;
        this.dateStr = dateStr;
    }

    public String getDateStr()
    {
        return dateStr;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean isComing()
    {
        return isComing;
    }

    public void setComing(boolean isComing)
    {
        this.isComing = isComing;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateStr = df.format(date);
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public boolean isReaded()
    {
        return readed;
    }

    public void setReaded(boolean readed)
    {
        this.readed = readed;
    }
}
