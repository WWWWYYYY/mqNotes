浏览器中访问http://127.0.0.1:8161/admin，即可看到ActiveMQ的管理控制台
ActiveMQ中，61616为服务端口，8161为管理控制台端口。
项目介绍：
1、activemq子模块：activemq 
①、mq基本使用开发步骤：
（1）、添加依赖：
      <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-all</artifactId>
            <version>5.8.0</version>
      </dependency>
（2）、消息消费者：参考org.apache.activemq.JmsConsumer
（3）、消息生产者：参考org.apache.activemq.JmsProducer
②、mq集成spring开发步骤：
（1）、添加依赖：
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
            <version>4.3.11.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.apache.xbean</groupId>
            <artifactId>xbean-spring</artifactId>
            <version>3.16</version>
        </dependency>
（2）、编辑application-c.xml、application-p.xml 配置文件
（3）、编辑org.apache.spring.SpringJmsProducer.java和org.apache.spring.SpringJmsConsumer.java以及相关的监听类


③activemq不同的消息类型：参考org.apache.spring.SpringJmsProducer发送消息；org.apache.spring.queue.QueueReceiver1接收消息

④消息request-response模式 开发步骤：
（1）参考activemq-producter 子模块 org.apache.spring.SpringJmsProducer类：
jmsQueueTemplate.send(QUEUE_NAME, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                System.out.println("发送信息1：" + message);
                TextMessage msg = session.createTextMessage(message);
                //配置 信息消费者回复消息
                Destination tempDst = session.createTemporaryQueue();
                MessageConsumer responseConsumer = session.createConsumer(tempDst);
                responseConsumer.setMessageListener(getResponse);
                msg.setJMSReplyTo(tempDst);
                //设置ID，让消费者通过id查询缓存redis或者数据库mysql查询有效数据
                String uid = System.currentTimeMillis()+"";
                msg.setJMSCorrelationID(uid);
                return msg;
            }
        });

（2）参考activemq-consumer 子模块 org.apache.spring.queue.ReplyTo类：
    //参数1：回复生产者的信息那内容，参数2：生产者传递过来的消息对象（包含了回复信息的目的地）
    public void send(final String consumerMsg, Message producerMessage)
            throws JMSException {
        jmsTemplate.send(producerMessage.getJMSReplyTo(),
                new MessageCreator() {
                    public Message createMessage(Session session)
                            throws JMSException {
                        Message msg
                                = session.createTextMessage("ReplyTo " + consumerMsg);
                        return msg;
                    }
                });
    }
