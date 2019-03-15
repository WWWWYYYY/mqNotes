开发步骤：
1、pom加入依赖
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-pool</artifactId>
        </dependency>
2、配置 配置文件 application.yml
3、配置类 ActiveMqConfig.java
    配置队列bean、连接工厂bean、jmsQueueListenerContainerFactory、jmsTopicListenerContainerFactory
4、编辑org.springboot.MsgTest.java测试类