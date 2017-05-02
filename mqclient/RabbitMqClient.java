package com.demo.mqclient;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Created by tiger on 2016/3/18.
 */
public class RabbitMqClient
{
	private Connection conn;
	private static RabbitMqClient instance = new RabbitMqClient();
	private String exchangeName;

	public static RabbitMqClient getInstance()
	{
		return instance;
	}

	public Channel getNewChannelForRcv(String queue, String routingKey) throws IOException
	{
		Channel nChannel = conn.createChannel();
		nChannel.exchangeDeclare(exchangeName, "direct", true);
		nChannel.queueDeclare(queue, true, false, false, null);
		nChannel.queueBind(queue, exchangeName, routingKey);

		//process the message one by one
		nChannel.basicQos(1);

		return nChannel;
	}

	public Channel getNewChannelForSend() throws IOException
	{
		Channel nChannel = null;
		nChannel = conn.createChannel();
		nChannel.exchangeDeclare(exchangeName, "direct", true);

		//process the message one by one
		nChannel.basicQos(1);
		return nChannel;
	}

	public String getExchangeName()
	{
		return exchangeName;
	}

	public void init(String userName, String password, String hostName, int portNumber, String exchange, int poolSize) throws IOException, TimeoutException
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(userName);
		factory.setPassword(password);
		factory.setHost(hostName);
		factory.setPort(portNumber);
		factory.setAutomaticRecoveryEnabled(true);
		ExecutorService es = Executors.newFixedThreadPool(poolSize);
		conn = factory.newConnection(es);

		this.exchangeName = exchange;
	}

	public void registReceiver(RabbitMqReceiver receiver) throws IOException
	{
		receiver.getChannel().basicConsume(receiver.getQueue(), receiver.isAutoAck(), receiver);
	}

	public void close() throws IOException, TimeoutException
	{
		conn.close();
	}
}
