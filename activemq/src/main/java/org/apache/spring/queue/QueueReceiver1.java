
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
public class QueueReceiver1 implements MessageListener {

	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage){
				String textMsg = ((TextMessage)message).getText();
				System.out.println("QueueReceiver1 accept msg : "+textMsg);
			}else if (message instanceof MapMessage){
				MapMessage mapMessage=(MapMessage)message;
				String id = mapMessage.getString("id");
				String name = mapMessage.getString("name");
				System.out.println("QueueReceiver1 accept msg : id="+id+",name="+name);
			}else if (message instanceof ObjectMessage){
				ObjectMessage objectMessage = (ObjectMessage) message;
				User u= (User) objectMessage.getObject();
				System.out.println("QueueReceiver1 accept msg :u="+u.toString());
			}else if (message instanceof BytesMessage){
				BytesMessage bytesMessage = (BytesMessage) message;
				long bodyLength = bytesMessage.getBodyLength();
				byte[] bs =new byte[(int) bodyLength];
				bytesMessage.readBytes(bs);
				System.out.println("QueueReceiver1 accept msg :"+new String(bs));
			}else if (message instanceof StreamMessage){
				StreamMessage streamMessage = (StreamMessage) message;
				String s = streamMessage.readString();
				int i = streamMessage.readInt();
				System.out.println("QueueReceiver1 accept msg :name:"+s+",no:"+i);
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
