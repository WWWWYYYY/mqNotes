package org.apache.activemq;

import javax.jms.*;

public class JmsProducer {
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
     * 5、通过session创建 消息生产者
     * 6、使用消息生产者 发送消息
     *
     * @param args
     */
    public static void main(String[] args) {

        //1、创建连接工厂并启动
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);
        //2、通过连接工厂创建一个连接并启动这个连接
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();

            connection.start();
            //3、通过连接创建Session(第一个参数表示是否使用事务，第二次参数表示是否自动确认)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //4、通过session创建 消息目的地
            //destination = session.createTopic("HelloActiveMq2");//一个消息同时被多个在线的消费者消费掉，消息信息存放在内存中，服务重启后就没了。
            Destination destination = session.createQueue("HelloActiveMq");//一个消息只能被一个消费者消费，消息被持久化保存直到有消费者消费掉。
            //5、通过session创建 消息生产者
            MessageProducer producer = session.createProducer(destination);
            //6、使用消息生产者 发送消息;5种消息类型：text、object、byte、map、stream
            TextMessage message = session.createTextMessage("msg content...");
            producer.send(message);
            System.out.println("发送信息："+message.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
