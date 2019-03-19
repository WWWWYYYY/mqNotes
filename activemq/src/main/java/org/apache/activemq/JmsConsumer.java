package org.apache.activemq;

import javax.jms.*;

public class JmsConsumer {
    /*默认连接用户名*/
    private static final String USERNAME
            = ActiveMQConnection.DEFAULT_USER;
    /* 默认连接密码*/
    private static final String PASSWORD
            = ActiveMQConnection.DEFAULT_PASSWORD;
    /* 默认连接地址*/
//    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final String BROKEURL="failover:(tcp://localhost:61616,tcp://localhost:61617,tcp://localhost:61618)?randomize=false";

    /**
     * 1、创建连接工厂
     * 2、通过连接工厂创建一个连接并启动这个连接
     * 3、通过连接创建Session
     * 4、通过session创建 消息目的地
     * 5、通过session创建 消息消费者
     * 6、使用消息消费者 接收消息
     *
     * @param args
     */
    public static void main(String[] args) {
        //1、创建连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);

        Connection connection = null;
        try {
            //2、通过连接工厂创建一个连接并启动这个连接
            connection = connectionFactory.createConnection();
            connection.start();
            /*
第一个参数是否使用事务:当消息发送者向消息提供者（即消息代理）发送消息时，消息发送者等待消息代理的确认，没有回应则抛出异常，消息发送程序负责处理这个错误。
第二个参数消息的确认模式：
AUTO_ACKNOWLEDGE ： 指定消息接收者在每次收到消息时自动发送确认。消息只向目标发送一次，但传输过程中可能因为错误而丢失消息。
CLIENT_ACKNOWLEDGE ： 由消息接收者确认收到消息，通过调用消息的acknowledge()方法（会通知消息提供者收到了消息）
DUPS_OK_ACKNOWLEDGE ： 指定消息提供者在消息接收者没有确认发送时重新发送消息（这种确认模式不在乎接收者收到重复的消息）。
            * */
            //3、通过连接创建Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //4、通过session创建 消息目的地
            //destination = session.createTopic("HelloActiveMq2");//一个消息同时被多个在线的消费者消费掉，消息信息存放在内存中，服务重启后就没了。
            Destination destination = session.createQueue("HelloActiveMq");//一个消息只能被一个消费者消费，消息被持久化保存直到有消费者消费掉。
            //5、通过session创建 消息消费者
            MessageConsumer consumer = session.createConsumer(destination);
            //6、消息消费者接收信息(阻塞方式)
            TextMessage message = (TextMessage) consumer.receive();//消息队列没有消息时则阻塞
            System.out.println("接收到信息：" + message.getText());

            /*(异步方式接收信息)推荐
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("接收到信息：" + ((TextMessage)message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            */
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
