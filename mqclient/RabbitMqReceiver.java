package com.demo.mqclient;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by tiger on 2016/3/22.
 */
public class RabbitMqReceiver extends DefaultConsumer
{
	private boolean autoAck;
	private Channel channel = null;
	private RcvWorker rcvWorker = null;
	private String queue = null;

	public RabbitMqReceiver(Channel channel, String queue, boolean autoAck, RcvWorker rcvWorker)
	{
		super(channel);
		this.channel = channel;
		this.queue = queue;
		this.autoAck = autoAck;
		this.rcvWorker = rcvWorker;
	}

	@Override public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) throws IOException
	{
		rcvWorker.delivery(envelope.getDeliveryTag(), channel, body, autoAck);
	}

	public void closeChannel() throws IOException, TimeoutException
	{
		channel.close();
	}

	public String getQueue()
	{
		return queue;
	}
}
