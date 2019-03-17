浏览器中访问http://127.0.0.1:8161/admin，即可看到ActiveMQ的管理控制台
ActiveMQ中，61616为服务端口，8161为管理控制台端口。
项目介绍：
一、activemq子模块：activemq 
1、mq基本使用开发步骤：
（1）、添加依赖：
      <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-all</artifactId>
            <version>5.8.0</version>
      </dependency>
（2）、消息消费者：参考org.apache.activemq.JmsConsumer
（3）、消息生产者：参考org.apache.activemq.JmsProducer
2、mq集成spring开发步骤：
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


3、activemq不同的消息类型：参考org.apache.spring.SpringJmsProducer发送消息；org.apache.spring.queue.QueueReceiver1接收消息

4、消息request-response模式 开发步骤：
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

5、activemq实战 参考activemq-asyncApp《atcivemq实战.md》

6、嵌入式mq：
使用场景：公司未提供mq服务&&不需要mq的高级功能||或者试试应用加入mq后的效果 （不建议使用，装逼可以）
嵌入式mq 服务端参考org.apache.embed.EmbedMQ.java
生产者：参考 org.apache.embed.EmbedJmsProducer.java
消费者：参考 org.apache.embed.EmbedJmsConsumer.java

ps：嵌入式mq不推荐使用，嵌入式mq的生产者和消费者和正常的mq的使用方式一样。

7、activemq持久化机制
1、持久化方式：
①、基于本地文件；分为两种 AMQ和KahaDB；AMQ在activemq 5.3以及5.3之前的版本都是使用AMQ，5.4以及之后的版本都是使用KahaDB,KahaDB效率要比AMQ慢一些，但是扩展性强和服务器恢复时间短
5.8以后还支持LevelDB，LevelDB读写速度很高。
②、基于数据库：
③、基于内存 （不考虑）

一般情况下现在使用的activemq使用的版本5.15.7 版本，默认使用KahaDB作为持久化的方式。
持久化文件存放在/usr/local/apache-activemq-5.15.7/data/kahadb/ 目录下

2、修改持久化方式为数据库方式存储：
（1）修改/usr/local/apache-activemq-5.15.7/conf/activemq.xml 
删除
    <persistenceAdapter>
        <kahaDB directory="${activemq.data}/kahadb"/>
    </persistenceAdapter>
新增
    <persistenceAdapter>
        <jdbcPersistenceAdapter dataSource="#mysql-ds" />
    </persistenceAdapter>
    <bean id="mysql-ds" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/activemq?relaxAutoCommit=true&amp;useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=UTC"/>
        <property name="username" value="root"/>
        <property name="password" value=""/>
        <property name="poolPreparedStatements" value="true"/>
    </bean>
（2）在mysql中创建名为activemq数据库；
（3）将mysql驱动包添加到/usr/local/apache-activemq-5.15.7/lib下
（4）重启activemq后观察mysql activemq数据库下的三张表：
    ACTIVEMQ_ACKS：用于存储订阅关系。
        主要字段解释：
            container：消息的destination
            sub_dest：如果是使用static集群，这个字段会有集群其他系统的信息
            client_id：每个订阅者都必须有一个唯一的客户端id用以区分
            sub_name：订阅者名称
            selector：选择器，可以选择只消费满足条件的消息。条件可以用自定义属性实现，可支持多属性and和or操作
            last_acked_id：记录消费过的消息的id
    ACTIVEMQ_LOCK：在集群环境中才有用，只有一个Broker可以获得消息，称为Master Broker，其他的只能作为备份等待Master Broker不可用，才可能成为下一个Master Broker。
        这个表用于记录哪个Broker是当前的Master Broker
    ACTIVEMQ_MSGS：用于存储消息
        主要字段解释：
            id：自增的数据库主键
            container：消息的destination
            msgid_prod：消息发送者客户端的主键
            msg_seq：是发送消息的顺序，msgid_prod+msg_seq可以组成jms的messageid
            expiration：消息的过期时间，存储的是从1970-01-01到现在的毫秒数
            msg：消息本体的java序列化对象的二进制数据
            priority：优先级，从0-9，数值越大优先级越高

ps：使用数据库持久化消息的好处提高了高可用，取决于数据库的高可用，但是使用数据库后消息的拿和放操作会变慢一些，如果数据库所在的服务器很卡，也将意味着activemq效率或者性能下降。

3、zookeeper+LevelDB持久化





相关问题：
1、activemq集群情况下无法保证消息的顺序执行，需要开发人员业务上的设计来保证消息的顺序
2、如果消息持久化在数据库，可以存放的消息要比持久化在本地文件的消息数量要多，如果消息持久化到本地文件则设置了最大存放消息数量。当消息堆积太多了就要再起一个消息消费者服务。

