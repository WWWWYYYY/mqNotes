
package org.apache.spring.queue;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com
 *往期视频咨询芊芊老师  QQ：2130753077  VIP课程咨询 依娜老师QQ：2133576719
 *类说明：
 */
@Component
public class QueueReceiver2 implements MessageListener {

	public void onMessage(Message message) {
		try {
			// 接收Text消息
			if (message instanceof TextMessage) {
				String textMsg = ((TextMessage)message).getText();
				System.out.println("QueueReceiver2 accept msg : "+textMsg);
			}

/*			// 接收Map消息
			if (message instanceof MapMessage) {
				MapMessage mm = (MapMessage) message;
				System.out.println("获取 MapMessage：   name：" + mm.getString("name")
						+ "     msg:" + mm.getString("msg"));
			}*/

/*			// 接收Object消息
			if (message instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage) message;
				User user = (User) objectMessage.getObject();
				System.out.println("获取 ObjectMessage：  "+user);
			}*/

/*			// 接收bytes消息
			if (message instanceof BytesMessage) {
				byte[] b = new byte[1024];
				int len = -1;
				BytesMessage bm = (BytesMessage) message;
				while ((len = bm.readBytes(b)) != -1) {
					System.out.println(new String(b, 0, len));
				}
			}*/

/*			// 接收Stream消息
			if (message instanceof StreamMessage) {
				StreamMessage streamMessage = (StreamMessage) message;
				System.out.println(streamMessage.readString());
				System.out.println(streamMessage.readInt());
			}*/
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
