package cn.enjoyedu.service.busi;

import cn.enjoyedu.dao.OrderExpDao;
import cn.enjoyedu.model.OrderExp;
import cn.enjoyedu.service.delay.IDelayOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 *
 *类说明：订单相关的服务
 */
@Service
public class SaveOrder {
	
	private Logger logger = LoggerFactory.getLogger(SaveOrder.class);
	
	public final static short UNPAY = 0;
	public final static short PAYED = 1;
	public final static short EXPIRED = -1;
	
	@Autowired
	private OrderExpDao orderExpDao;
	
	@Autowired
	@Qualifier("mq")
	private IDelayOrder delayOrder;

	/**
	 * 接收前端页面参数，生成订单
	 * @param orderNumber 订单个数
	 */
    public void insertOrders(int orderNumber){
    	Random r = new Random();
    	OrderExp orderExp ;
    	for(int i=0;i<orderNumber;i++) {
    		long expireTime = r.nextInt(20)+5;//订单的超时时长，单位秒
            orderExp = new OrderExp();
            String orderNo = "DD00_"+expireTime+"S";
            orderExp.setOrderNo(orderNo);
            orderExp.setOrderNote("享学订单——"+orderNo);
            orderExp.setOrderStatus(UNPAY);
            orderExpDao.insertDelayOrder(orderExp,expireTime);  
            logger.info("保存订单到DB:"+orderNo);
            delayOrder.orderDelay(orderExp, expireTime);
    	}
    }
    
/*	通过mq来实现就不需要这段代码了
    @PostConstruct
    public void initDelayOrder() {
    	logger.info("系统启动，扫描表中过期未支付的订单并处理.........");

    	int counts = orderExpDao.updateExpireOrders();
    	logger.info("系统启动，处理了表中["+counts+"]个过期未支付的订单！");

    	List<OrderExp> orderList = orderExpDao.selectUnPayOrders();
    	logger.info("系统启动，发现了表中还有["+orderList.size()
    	+"]个未到期未支付的订单！推入检查队列准备到期检查....");
    	for(OrderExp order:orderList) {
    		long expireTime
    			= order.getExpireTime().getTime()-(new Date().getTime());
    		//if(expireTime<=0){马上更新数据库}
    		delayOrder.orderDelay(order, expireTime);
    	}
    }
*/



}
