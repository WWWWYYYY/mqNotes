package org.apache.durable;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

public class DurableTopicConsumer {
    /*默认连接用户名*/
    private static final String USERNAME
            = ActiveMQConnection.DEFAULT_USER;
    /* 默认连接密码*/
    private static final String PASSWORD
            = ActiveMQConnection.DEFAULT_PASSWORD;
    /* 默认连接地址*/
    private static final String BROKEURL
            = ActiveMQConnection.DEFAULT_BROKER_URL;


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
            //2、通过连接工厂创建一个连接并启动这个连接；设置连接ID
            connection = connectionFactory.createConnection();
            connection.setClientID("wang");
            connection.start();
            //3、通过连接创建Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //4、通过session创建 消息目的地 必须
            Topic destination = session.createTopic("durableTopic");
            //5、通过session创建 消息消费者
            TopicSubscriber consumer = session.createDurableSubscriber(destination,"xxx");
            //6、消息消费者接收信息

//            (异步方式接收信息)推荐
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
            System.in.read();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
