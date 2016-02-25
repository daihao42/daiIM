package com.example.dai.baiduyunpush.component;

import java.io.Serializable;

public class myMessage implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String userId;
	private String channelId;
	private String nickname;
	private long timeSamp;
	private String message;


	public myMessage(long time_samp, String message, String userId)
	{
		super();
		this.timeSamp = time_samp;
		this.message = message;
		this.userId = userId;
	}

	public myMessage(){}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getChannelId()
	{
		return channelId;
	}

	public void setChannelId(String channelId)
	{
		this.channelId = channelId;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public long getTimeSamp()
	{
		return timeSamp;
	}

	public void setTimeSamp(long timeSamp)
	{
		this.timeSamp = timeSamp;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

}
