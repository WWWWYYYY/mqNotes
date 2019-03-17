package org.apache.embed;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：嵌入式MQ
 */
public class EmbedMQ {
    public static void main(String[] args) throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("EmbedMQ");
        brokerService.addConnector("tcp://localhost:62000");
        brokerService.setManagementContext(new ManagementContext());
        brokerService.start();
    }
}
