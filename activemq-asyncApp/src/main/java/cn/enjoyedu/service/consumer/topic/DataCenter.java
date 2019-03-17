package cn.enjoyedu.service.consumer.topic;

import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * <p>
 * 更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 * <p>
 * 类说明：数据中心服务：订阅了topic消息的消费者
 */
@Service
public class DataCenter implements MessageListener {

    public void onMessage(Message message) {
        try {
            String textMsg = ((TextMessage) message).getText();
            //do my 业务工作
            System.out.println("We save User[" + textMsg + "] into DataCenter ");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
