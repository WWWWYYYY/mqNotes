package org.apache.spring;

import org.apache.spring.vo.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 生产者
 *
 * 消息类型：
 * 1、TextMessage：发送文本类型
 * 2、MapMessage：发送map类型
 * 3、ObjectMessage：发送对象类型
 * 4、BytesMessage：发送字节类型
 * 5、StreamMessage：发送流类型
 *
 * 总结：
 * 1、如果消息是文本类型则使用 TextMessage
 * 2、如果消息时字节类型则使用 BytesMessage
 * 3、如果消息时对象类型则使用 MapMessage、ObjectMessage、StreamMessage 、TextMessage（json字符串）
 * 常用类型：不论是字符串还是对象类型都是首选 TextMessage 类型，如果是字节类型都是选 BytesMessage，MapMessage 使用次数比较少，其他类型使用次数更少
 */
public class SpringJmsProducer {
    public static final String QUEUE_NAME = "test.queue";

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:application-p.xml");
        JmsTemplate jmsQueueTemplate = (JmsTemplate) context.getBean("jmsQueueTemplate");

        final String message = "text msg...";
        //发送文本类型
        sendTextMessage(jmsQueueTemplate, QUEUE_NAME, message);
        //发送map类型
        sendMapMessage(jmsQueueTemplate, QUEUE_NAME, message);
        //发送对象类型
        sendObjectMessage(jmsQueueTemplate, QUEUE_NAME, message);
        //发送字节类型
        sendBytesMessage(jmsQueueTemplate, QUEUE_NAME, message);
        //发送流类型
        sendStreamMessage(jmsQueueTemplate, QUEUE_NAME, message);

        context.close();
    }

    /**
     * TextMessage类型
     */
    public static void sendTextMessage(JmsTemplate jmsQueueTemplate, String queueName, String message) {
        jmsQueueTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("发送信息：" + message);
                TextMessage msg = session.createTextMessage(message);
                return msg;
            }
        });
    }

    /**
     * MapMessage类型
     */
    public static void sendMapMessage(JmsTemplate jmsQueueTemplate, String queueName, String message) {
        jmsQueueTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage map = session.createMapMessage();
                map.setString("id", "10000");
                map.setString("name", "享学学员");
                return map;
            }
        });
    }

    /**
     * ObjectMessage类型
     * 序列化方式1：传输的对象需要实现 Serializable
     * 序列化方式2：使用 protobuf、kyro、messagepack
     */
    public static void sendObjectMessage(JmsTemplate jmsQueueTemplate, String queueName, String message) {
        jmsQueueTemplate.send(queueName, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                User user = new User(10000, "享学学员");
                ObjectMessage objectMessage = session.createObjectMessage(user);
                return objectMessage;
            }
        });
    }

    /**
     * BytesMessage类型
     */
    public static void sendBytesMessage(JmsTemplate jmsQueueTemplate, String queueName, String message) {
        jmsQueueTemplate.send(queueName, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeBytes(message.getBytes());
                return bytesMessage;
            }
        });
    }

    /**
     * StreamMessage类型
     */
    public static void sendStreamMessage(JmsTemplate jmsQueueTemplate, String queueName, String message) {
        jmsQueueTemplate.send(queueName, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                StreamMessage streamMessage = session.createStreamMessage();
                streamMessage.writeString("享学学员");
                streamMessage.writeInt(10000);
                return streamMessage;
            }
        });
    }


}
