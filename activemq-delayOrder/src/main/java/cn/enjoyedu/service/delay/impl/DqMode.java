package cn.enjoyedu.service.delay.impl;

import cn.enjoyedu.model.OrderExp;
import cn.enjoyedu.service.busi.DlyOrderProcessor;
import cn.enjoyedu.service.delay.IDelayOrder;
import cn.enjoyedu.vo.ItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.DelayQueue;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 *
 *类说明：阻塞队列的实现
 */
@Service
@Qualifier("dq")
public class DqMode implements IDelayOrder {
	
	private Logger logger = LoggerFactory.getLogger(DqMode.class);
	
	@Autowired
	private DlyOrderProcessor processDelayOrder;
	private Thread takeOrder;
	
	private static DelayQueue<ItemVo<OrderExp>> delayOrder
		= new DelayQueue<ItemVo<OrderExp>>();

    public void orderDelay(OrderExp order, long expireTime) {
    	ItemVo<OrderExp> itemOrder = new ItemVo<OrderExp>(expireTime*1000,order);
    	delayOrder.put(itemOrder);
    	logger.info("订单[超时时长："+expireTime+"秒]被推入检查队列，订单详情："+order);
    }
    
    private class TakeOrder implements Runnable{
    	
    	private DlyOrderProcessor processDelayOrder;

		public TakeOrder(DlyOrderProcessor processDelayOrder) {
			super();
			this.processDelayOrder = processDelayOrder;
		}

		public void run() {
			logger.info("处理到期订单线程已经启动！");
			while(!Thread.currentThread().isInterrupted()) {
				try {
					ItemVo<OrderExp> itemOrder = delayOrder.take();
					if (itemOrder!=null) {
						processDelayOrder.checkDelayOrder(itemOrder.getData());
					}
				} catch (Exception e) {
					logger.error("The thread :",e);
				}
			}
			logger.info("处理到期订单线程准备关闭......");
		}
    }
    /*	通过mq来实现就不需要这段代码了
    @PostConstruct
    public void init() {
    	takeOrder = new Thread(new TakeOrder(processDelayOrder));
    	takeOrder.start();
    }

    @PreDestroy
    public void close() {
    	takeOrder.interrupt();
    }
    */
}
