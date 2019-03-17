
package org.apache.spring.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 */
@Service("queueReceiver1")
public class QueueReceiver1 implements MessageListener {

	@Autowired
	private ReplyTo replyTo;

	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage){
				String textMsg = ((TextMessage)message).getText();
				System.out.println("QueueReceiver1 accept msg : "+textMsg);
				//获取到id进行业务处理并且回复消息，由于消息的生产者希望消费者回复消息，因此message的内容中携带了 回复消息的目的地
				replyTo.send("消费者答复1",message);
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
