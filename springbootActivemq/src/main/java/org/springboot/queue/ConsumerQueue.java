package org.springboot.queue;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerQueue {

    @JmsListener(destination = "springboot.queue")
//    @JmsListener(destination = "springboot.queue")//可以监听多个队列
    public void receiveMsg(String msg){
        System.out.println("接收到信息"+ConsumerQueue.class.getName()+":"+msg);
    }
}
