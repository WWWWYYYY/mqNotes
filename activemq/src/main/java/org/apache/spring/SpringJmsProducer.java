package org.apache.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 生产者
 */
public class SpringJmsProducer {
    public static final String QUEUE_NAME="test.queue";
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =new ClassPathXmlApplicationContext("classpath:application-p.xml");
        JmsTemplate jmsQueueTemplate = (JmsTemplate) context.getBean("jmsQueueTemplate");

        final String message ="text msg...";
        jmsQueueTemplate.send(QUEUE_NAME,new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("发送信息："+message);
                Message msg = session.createTextMessage(message);
                return msg;
            }
        });

        context.close();
    }
}
