package org.apache.spring.topic;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *类说明：
 * topic模式接收消息类型的处理方式和P2P模式一样
 */
public class TopicReceiver2 implements MessageListener {

	public void onMessage(Message message) {
		try {
			String textMsg = ((TextMessage)message).getText();
			System.out.println("TopicReceiver2 accept msg : "+textMsg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
