package com.example.dai.baiduyunpush.someUser;

import java.io.Serializable;

public class User implements Serializable
{
	private String UserId;//
	private String channelId;
	private String nick;//

	public User(String UserId, String channelId, String nick)
	{
		// TODO Auto-generated constructor stub
		this.UserId = UserId;
		this.channelId = channelId;
		this.nick = nick;
	}

	public User()
	{

	}

	public String getUserId()
	{
		return UserId;
	}

	public void setUserId(String userId)
	{
		UserId = userId;
	}

	public String getChannelId()
	{
		return channelId;
	}

	public void setChannelId(String channelId)
	{
		this.channelId = channelId;
	}

	public String getNick()
	{
		return nick;
	}

	public void setNick(String nick)
	{
		this.nick = nick;
	}

	@Override
	public String toString()
	{
		return "User [UserId=" + UserId + ", channelId=" + channelId
				+ ", nick=" + nick + "]";
	}

}
