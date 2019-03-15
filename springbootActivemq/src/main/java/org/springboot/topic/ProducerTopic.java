package org.springboot.topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service
public class ProducerTopic {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "topic")
    private ActiveMQTopic topic;

    public void sendMsg(final String message){
        //通过 JmsMessagingTemplate 对象发送信息
        jmsMessagingTemplate.convertAndSend(topic,message);

        /*
        //通过 JmsTemplate 对象发送信息
        jmsTemplate.send(new ActiveMQTopic(destination), new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage msg =session.createTextMessage(message);
                return msg;
            }
        });
        */
    }
}
