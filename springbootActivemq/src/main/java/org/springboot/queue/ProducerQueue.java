package org.springboot.queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProducerQueue {

    //jmsMessagingTemplate和jmsTemplate作用差不多
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "queue")
    private ActiveMQQueue queue;

    public void sendMsg(final String message){
        //通过 JmsMessagingTemplate 对象发送信息
        jmsMessagingTemplate.convertAndSend(queue,message);

        /*
        //通过 JmsTemplate 对象发送信息
        jmsTemplate.send(new ActiveMQQueue(destination), new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage msg =session.createTextMessage(message);
                return msg;
            }
        });
        */
    }

    @JmsListener(destination = "springboot.replyto.queue")
    public void receiveMsg(String msg){
        System.out.println("生产者接收到信息："+msg);
    }
}
