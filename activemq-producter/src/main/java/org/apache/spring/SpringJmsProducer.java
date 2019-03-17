package org.apache.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.io.IOException;

/**
 *
 */
public class SpringJmsProducer {
    public static final String QUEUE_NAME = "test.queue";

    public static void main(String[] args) throws  IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:application-p.xml");
        JmsTemplate jmsQueueTemplate = (JmsTemplate) context.getBean("jmsQueueTemplate");
        MessageListener getResponse = (MessageListener) context.getBean("getResponse");

        final String message = "text msg123...";
        //发送文本类型
        jmsQueueTemplate.send(QUEUE_NAME, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("发送信息1：" + message);
                TextMessage msg = session.createTextMessage(message);

                //配置一个信息消费者回复消息
                Destination tempDst = session.createTemporaryQueue();
                MessageConsumer responseConsumer = session.createConsumer(tempDst);
                responseConsumer.setMessageListener(getResponse);
                msg.setJMSReplyTo(tempDst);

                //设置ID，让消费者通过id查询缓存redis或者数据库mysql查询有效数据
                String uid = System.currentTimeMillis()+"";
                msg.setJMSCorrelationID(uid);
                return msg;
            }
        });
//        context.close();
    }





}
