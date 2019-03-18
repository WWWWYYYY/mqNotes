package cn.enjoyedu.service.delay;

import cn.enjoyedu.model.OrderExp;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 *
 *类说明：延时处理订单的的接口
 */
public interface IDelayOrder {

	/**
	 * 进行延时处理的方法
	 * @param order 要进行延时处理的订单
	 * @param expireTime 延时时长，单位秒
	 */
    public void orderDelay(OrderExp order, long expireTime);

}
