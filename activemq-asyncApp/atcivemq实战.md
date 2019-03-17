ActiveMQ实战之一用户注册的异步处理
实战场景一：（消息队列异步处理业务）
用户注册请求发送后台；串行处理的情况下会经过一下步骤：
步骤1、用户信息保存到数据库 消耗50ms
步骤2、发送邮件 消耗50ms
步骤3、发送短信 消耗50ms

实战分析：
方案一：串行先后调用则消耗150ms 参考cn.enjoyedu.service.reg.impl.SerialProcess.java

经过分析 步骤1是最主要的，步骤2和步骤3都是可以异步化进行，异步化操作有三种
方案二：
    使用线程池，将步骤2和步骤3封装成任务交给线程池执行；（缺点，消耗当前服务性能，导致并发量下降）参考cn.enjoyedu.service.reg.impl.ParallelProcess.java
方案三
    RPC异步调用（非mq重点）
方案四：
    将步骤2和步骤3封装成消息投递到消息队列中；参考cn.enjoyedu.service.reg.impl.MqProcess.java
    
其中方案四和方案三根据实际情况首选。此处选择方案四

实战场景二：
在实战场景一的基础上 新增需求 要求将用户注册的信息发送给数据中心；
步骤：
1、在activemq服务器上添加一个新队列，存放数据中心需要的消息
2、在用户注册接口添加步骤4
        //新增需求 要求将用户信息发送给数据中心
        sendMq(jmsTopicTemplate,"user.topic",user.toString());
        
        
总结：通过mq实战可以看出消息队列的异步处理作用和解耦作用。