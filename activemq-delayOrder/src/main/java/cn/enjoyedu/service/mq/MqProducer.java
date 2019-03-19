package cn.enjoyedu.service.mq;

import cn.enjoyedu.model.OrderExp;
import cn.enjoyedu.service.delay.IDelayOrder;
import com.google.gson.Gson;
import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 *
 *类说明：消息队列的实现
 */
@Service
@Qualifier("mq")
public class MqProducer implements IDelayOrder {
	
	private Logger logger = LoggerFactory.getLogger(MqProducer.class);
	
	@Autowired
	private JmsTemplate jmsTemplate;	
    
    /**
     *类说明：创建消息的类
     */
    private static class CreateMessage implements MessageCreator{
    	
    	private OrderExp order;
    	private long expireTime;
    	
		public CreateMessage(OrderExp order, long expireTime) {
			super();
			this.order = order;
			this.expireTime = expireTime;
		}

		public Message createMessage(Session session) throws JMSException {
			Gson gson = new Gson();
			String txtMsg = gson.toJson(order);
			Message message = session.createTextMessage(txtMsg);
			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, expireTime);
//			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
//			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
//			message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat); //重复投递几次
//			也可使用 CRON 表达式，如
// 			message.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "0 * * * *");
			return message;
		}
    }

	public void orderDelay(OrderExp order, long expireTime) {
		logger.info("订单[超时时长："+expireTime+"秒] 将被发送给消息队列，详情："+order);
		jmsTemplate.send("order.delay", new CreateMessage(order,expireTime*1000));
		
	}
    
}
