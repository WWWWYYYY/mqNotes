
package org.apache.spring.queue;

import org.apache.spring.vo.User;
import org.springframework.stereotype.Component;

import javax.jms.*;


/**
 *  * 1、TextMessage：发送文本类型
 *  * 2、MapMessage：发送map类型
 *  * 3、ObjectMessage：发送对象类型
 *  * 4、BytesMessage：发送字节类型
 *  * 5、StreamMessage：发送流类型
 */
public class QueueReceiver2 implements MessageListener {

	public void onMessage(Message message) {
		try {
			// 接收Text消息
			if (message instanceof TextMessage) {
				String textMsg = ((TextMessage)message).getText();
				System.out.println("QueueReceiver2 accept msg : "+textMsg);
			}

			// 接收Map消息
			if (message instanceof MapMessage) {
				MapMessage mm = (MapMessage) message;
				System.out.println("获取 MapMessage：   name：" + mm.getString("name")
						+ "     msg:" + mm.getString("msg"));
			}

			// 接收Object消息
			if (message instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage) message;
				User user = (User) objectMessage.getObject();
				System.out.println("获取 ObjectMessage：  "+user);
			}

			// 接收bytes消息
			if (message instanceof BytesMessage) {
				byte[] b = new byte[1024];
				int len = -1;
				BytesMessage bm = (BytesMessage) message;
				while ((len = bm.readBytes(b)) != -1) {
					System.out.println(new String(b, 0, len));
				}
			}

			// 接收Stream消息
			if (message instanceof StreamMessage) {
				StreamMessage streamMessage = (StreamMessage) message;
				System.out.println(streamMessage.readString());
				System.out.println(streamMessage.readInt());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
