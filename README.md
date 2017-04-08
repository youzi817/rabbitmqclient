# Consumer
The most efficient way to receive messages is to set up a subscription using the Consumer interface. The messages will then be delivered automatically as they arrive, rather than having to be explicitly requested.
Distinct Consumers on the same Channel must have distinct consumer tags.
The easiest way to implement a Consumer is to subclass the convenience class DefaultConsumer. An object of this subclass can be passed on a basicConsume call to set up the subscription:
``` java
boolean autoAck = false;
channel.basicConsume(queueName, autoAck, "myConsumerTag",
     new DefaultConsumer(channel) {
         @Override
         public void handleDelivery(String consumerTag,
                                    Envelope envelope,
                                    AMQP.BasicProperties properties,
                                    byte[] body)
             throws IOException
         {
             String routingKey = envelope.getRoutingKey();
             String contentType = properties.getContentType();
             long deliveryTag = envelope.getDeliveryTag();
             // (process the message components here ...)
             channel.basicAck(deliveryTag, false);
         }
     });
```
Each Channel has its own dispatch thread. For the most common use case of one Consumer per Channel, this means Consumers do not hold up other Consumers.

**Consumer threads are automatically allocated in a new ExecutorService thread pool by default.** If greater control is required supply an ExecutorService on the newConnection() method, so that this pool of threads is used instead. Here is an example where a larger thread pool is supplied than is normally allocated:
``` java
ExecutorService es = Executors.newFixedThreadPool(20);
Connection conn = factory.newConnection(es);
```
**With the default blocking IO mode, each connection uses a thread to read from the network socket.** With the NIO mode, you can control the number of threads that read and write from/to the network socket.
Use the NIO mode if your Java process uses many connections (dozens or hundreds). You should use fewer threads than with the default blocking mode. With the appropriate number of threads set, you shouldn't experiment any decrease in performance, especially if the connections are not so busy.
ectionFactory.useNio();
``` java
ConnectionFactory connectionFactory = new ConnectionFactory();
connectionFactory.useNio();
```
The NIO mode uses reasonable defaults, but you may need to change them according to your own workload. Some of the settings are: the total number of IO threads used, the size of buffers, a service executor to use for the IO loops, parameters for the in-memory write queue (write requests are enqueued before being sent on the network). 

单独拉取消息：
To explicitly retrieve messages, use Channel.basicGet. The returned value is an instance of GetResponse, from which the header information (properties) and message body can be extracted:
``` java
boolean autoAck = false;
GetResponse response = channel.basicGet(queueName, autoAck);
if (response == null) {
    // No message retrieved.
} else {
    AMQP.BasicProperties props = response.getProps();
    byte[] body = response.getBody();
    long deliveryTag = response.getEnvelope().getDeliveryTag();
    ...
```
and since the autoAck = false above, you must also call Channel.basicAck to acknowledge that you have successfully received the message:
``` java
...
    channel.basicAck(method.deliveryTag, false); // acknowledge receipt of the message
}
```
