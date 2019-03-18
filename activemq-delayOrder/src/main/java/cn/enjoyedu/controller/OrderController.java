package cn.enjoyedu.controller;

import cn.enjoyedu.service.busi.SaveOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 *
 *类说明：用户注册的控制器
 */
@Controller
public class OrderController {
    private static final String SUCCESS = "suc";
    private static final String FAILUER = "failure";

    @Autowired
    private SaveOrder saveOrder;

    @RequestMapping("/index")
    public String userReg(){
        return "order";
    }

    @RequestMapping("/submitOrder")
    @ResponseBody
    public String saveUser(@RequestParam("orderNumber")int orderNumber){
    	saveOrder.insertOrders(orderNumber);
    	return SUCCESS;
    }


}
