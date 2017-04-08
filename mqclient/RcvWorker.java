package com.demo.mqclient;

import com.rabbitmq.client.Channel;

/**
 * Created by tiger on 2016/3/25.
 */
public abstract class RcvWorker
{
	public abstract void delivery(long deliveryTag, Channel channel, String body, boolean autoAck);

	public void delivery(long deliveryTag, Channel channel, byte[] body, boolean autoAck)
	{
		this.delivery(deliveryTag, channel, new String(body), autoAck);
	}
}
