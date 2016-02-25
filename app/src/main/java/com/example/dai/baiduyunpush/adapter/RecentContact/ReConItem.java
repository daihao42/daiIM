package com.example.dai.baiduyunpush.adapter.RecentContact;

import com.example.dai.baiduyunpush.component.ChatMessage;

/**
 * Created by dai on 2016/1/19.
 */
public class ReConItem extends ChatMessage implements Comparable{
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    //实现Comparable接口来完成自定义排序
    @Override
    public int compareTo(Object another) {
        ReConItem rec = (ReConItem)another;
        String otherDate = rec.getDateStr();
        return this.getDateStr().compareTo(otherDate);
    }
}
