package org.springboot.topic;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerTopic2 {

    @JmsListener(destination = "springboot.topic",containerFactory = "jmsTopicListenerContainerFactory")
//    @JmsListener(destination = "springboot.topic")//可以监听多个队列
    public void receiveMsg(String msg){
        System.out.println("接收到信息2"+ConsumerTopic2.class.getName()+":"+msg);
    }
}
