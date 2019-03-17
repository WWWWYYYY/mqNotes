package org.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springboot.queue.ProducerQueue;
import org.springboot.topic.ProducerTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MsgTest {

    @Autowired
    private ProducerQueue producerQueue;

    @Autowired
    private ProducerTopic producerTopic;

    //发送p2p消息
    @Test
    public void test(){
        producerQueue.sendMsg("abc");
    }
    //发送topic 消息
    @Test
    public void test2(){
        producerTopic.sendMsg("abc");
    }
}
