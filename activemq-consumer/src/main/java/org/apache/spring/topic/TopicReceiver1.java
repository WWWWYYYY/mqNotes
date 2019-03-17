package org.apache.spring.topic;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *类说明：
 * topic模式接收消息类型的处理方式和P2P模式一样
 */
public class TopicReceiver1 implements MessageListener {
    public void onMessage(Message message) {
        try {
            System.out.println(((TextMessage)message).getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
