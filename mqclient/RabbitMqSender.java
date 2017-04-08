package com.demo.mqclient;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by tiger on 2016/3/22.
 */
public class RabbitMqSender
{
	private Channel channel = null;
	private String exchangeName = null;

	public RabbitMqSender() throws IOException
	{
		this.channel = RabbitMqClient.getInstance().getNewChannelForSend();
		this.exchangeName = RabbitMqClient.getInstance().getExchangeName();
	}

	public void publishMsg(String routingKey, String msg) throws IOException
	{
		channel.basicPublish(exchangeName, routingKey,
				MessageProperties.PERSISTENT_TEXT_PLAIN,
				msg.getBytes());
	}

	public void closeChannel() throws IOException, TimeoutException
	{
		if (null != channel)
		{
			channel.close();
		}
	}
}
