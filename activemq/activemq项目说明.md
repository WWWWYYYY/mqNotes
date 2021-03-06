浏览器中访问http://127.0.0.1:8161/admin，即可看到ActiveMQ的管理控制台
ActiveMQ中，61616为服务端口，8161为管理控制台端口。
项目介绍：
（一）、activemq子模块：activemq 
一、mq基本使用开发步骤：
（1）、添加依赖：
      <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-all</artifactId>
            <version>5.8.0</version>
      </dependency>
（2）、消息消费者：参考org.apache.activemq.JmsConsumer
（3）、消息生产者：参考org.apache.activemq.JmsProducer
二、mq集成spring开发步骤：
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


三、activemq不同的消息类型：参考org.apache.spring.SpringJmsProducer发送消息；org.apache.spring.queue.QueueReceiver1接收消息

四、消息request-response模式 开发步骤：
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

五、activemq实战 参考activemq-asyncApp《atcivemq实战.md》

六、嵌入式mq：
使用场景：公司未提供mq服务&&不需要mq的高级功能||或者试试应用加入mq后的效果 （不建议使用，装逼可以）
嵌入式mq 服务端参考org.apache.embed.EmbedMQ.java
生产者：参考 org.apache.embed.EmbedJmsProducer.java
消费者：参考 org.apache.embed.EmbedJmsConsumer.java

ps：嵌入式mq不推荐使用，嵌入式mq的生产者和消费者和正常的mq的使用方式一样。

七、activemq持久化机制
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

（5）、zookeeper+LevelDB持久化

八、topic消息持久化订阅；普通的topic消息时非持久化的，为了让tipic消息持久化保存，直到发送给所有的topic订阅者以后消息才被删除。当topic订阅者上线收马上收到离线时消息服务器接收到的topic消息。
开发步骤：
生产者:代码和非持久化代码一样；具体参考org.apache.durable.DurableTopicProducer.java
消费者:变化如下；具体参考org.apache.durable.DurableTopicConsumer.java
1、connection.setClientID("wang");//连接必须设置ID
2、Topic destination = session.createTopic("durableTopic");//消息目的地必须是Topic类型
3、TopicSubscriber consumer = session.createDurableSubscriber(destination,"xxx"); //消费者类型必须是持久订阅TopicSubscriber类型

ps：消费者要运行后再关闭，表示在activemq服务器上注册了 持久化topic订阅。持久化以后topic消息队列的消息在activemq服务器重启以后消息依然存在。

九、消息可靠性 （发送的消息队列类型只能是queue类型）
1、生产者到activemq服务器的消息可靠性：生产者开启事务提交消息，直到提交了事务。消息才能最终持久化保存，未提交时事务前，即使生产者发送了消息，activemq服务器是看不到未提交的消息
Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);//第一个参数为true表示开启事务，
因此在发送了消息以后调用事务提交方法
    session.commit();//可以一次提交一个消息可以提交多个消息；在未提交之前activemq服务器是看不到未提交的消息
2、activemq服务器到消费者的消息可靠性：
①通常情况下使用消费者自动应答，如下消费者代码
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);//当设置为AUTO_ACKNOWLEDGE表示onMessage(Message message)方法正常执行完成后，
 消费者将自动向消费者回复应答消息。表示消息已经被消费者正常获取了。至于消息怎么处理，根据业务处理成功还是失败和activemq没有关系。因此在代码实现上：
             consumer.setMessageListener(new MessageListener() {
                 @Override
                 public void onMessage(Message message) {
                     try {
                         System.out.println("接收到信息：" + ((TextMessage)message).getText());
                         //do business
                     } catch (JMSException e) {
                         e.printStackTrace();
                         //do business atfer exception
                     }
                 }
             });
ps：从以上代码可以看出只要onMessage正常执行完成，activemq服务器就会收到消息消费了的确定应答。
至于消费者接收到消息后，根据消息的内容，如果业务处理异常（调用了其他服务连接不上、或者消息内容不符合业务逻辑）则需要开发者定义异常后的业务do business atfer exception。
从而使得消费者获取消息回复activemq服务器的步骤和业务处理的步骤没有关联。
自动应答使用场景：
    不希望业务处理异常导致消费者没有回复activemq服务器或者导致activemq服务器重发消息。
需要注意：如果消费者没有正常的执行onMessage方法或者抛出了异常或者没有答复，则activemq会进行消息重发（重发到当前消费者或者是其他消费者）；默认重发6次，重发次数可以更改。

②消费者手动应答：如下代码
    Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);//CLIENT_ACKNOWLEDGE表示消费者手动应答
    consumer.setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message message) {
            try {
                System.out.println("接收到信息：" + ((TextMessage)message).getText());
                message.acknowledge();//手动应答，会将之前所有未应答的消息全部应答了。
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    });
    
手动应答使用场景：
    如果当前消费者处理业务失败，没有调用message.acknowledge()并且希望activemq服务器把该消息发送给其他消费者去处理。
    
③批量自动应答：减少了网络传输应答，效率提高了，但是会产生消息重复处理的问题(如果有多条消息，处理了一半发生了异常，将导致全部消息发送给其他消费者重新处理)。谨慎使用
    Session session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
    
④事务模式应答: 某一批消息 要么全部处理成功要么全部处理失败
Session session = connection.createSession(true, Session.SESSION_TRANSACTED);//并且调用session.commit()提交事务
ps：批次消息通过业务设计来决定这几个消息是同一批的消息

十、通配符式分层订阅
"." 用于作为路径上名字间的分隔符。
"*" 用于匹配路径上的任何名字。
">" 用于递归地匹配任何以这个名字开始的destination。

例如activemq服务器上存在一个实际队列名为xiang.vip.xxx.xxx
消费者若想监听该队列，则监听队列名可以写成 (谨慎编写接收的队列名，不要把不该接收的信息接收了)
1、xiang.vip.xxx.xxx
2、xiang.*.*.xxx
3、xiang.vip>

使用场景：有些消费者需要接收所有的消息，有些消费需要接收部分消息

十一、策略修改
消费者方代码：具体参考org.apache.dlq.DlqConsumer.java
        //限制了重发次数策略
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(1);
        try {
            //通过连接工厂获取连接
            connection = (ActiveMQConnection) connectionFactory.createConnection();
            //启动连接
            connection.start();
            // 拿到消费者端重复策略map
            RedeliveryPolicyMap redeliveryPolicyMap
                    = connection.getRedeliveryPolicyMap();
            //创建session
            session
                    = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            destination = (ActiveMQDestination) session.createQueue("TestDlq2");
            // 将消费者端重发策略配置给消费者
            redeliveryPolicyMap.put(destination,redeliveryPolicy);
ps:此处仅仅设置了重发次数为1次。还可以配置其他内容。

十二、死信队列：保存处理失败或者过期的消息
1、出现下面情况时，消息会被重发： 
i.	事务会话被回滚。
ii.	事务会话在提交之前关闭。
iii.	会话使用CLIENT_ACKNOWLEDGE模式，并且Session.recover()被调用。 
iv.	自动应答失败
当一个消息被重发超过最大重发次数（缺省为6次，消费者端可以修改）时，会给broker发送一个"有毒标记“，这个消息被认为是有问题，这时broker将这个消息发送到死信队列，以便后续处理。

2、设置额外的死信队列 修改/usr/local/apache-activemq-5.15.7/conf/activemq.xml policyEntries子元素下添加：
    #queuePrefix：死信队列名前缀；useQueueForQueueMessages：设置使用队列保存死信
    #useQueueForTopicMessages:设置使用topic保存死信
    <policyEntry queue=">" producerFlowControl="true" memoryLimit="1mb" >
            <deadLetterStrategy>
                    <individualDeadLetterStrategy queuePrefix="DLQ." useQueueForQueueMessages="true" />
            </deadLetterStrategy>
    </policyEntry>
配置后重启activemq服务器。
3、生产者创建消息 参考org.apache.dlq.DlqProducer.java
   消费者消费消息 参考org.apache.dlq.DlqConsumer.java
   死信队列消费者  参考org.apache.dlq.ProcessDlqConsumer.java
   
请求过程：DlqProducer向名为TestDlq2的队列发送了一个消息，DlqConsumer接收了消息但是onMessage方法中抛出异常，因此该消息被放置到了名为DLQ.TestDlq2消息队列中
通过ProcessDlqConsumer处理死信消息队列中的消息。如果业务复杂，死信消费者也可以根据业务分为多个死信消费者。
ps：死信队列和普通队列一样，只是名称叫死信。


十三、镜像队列:用于监控被消费者消费的消息，在消费者拿走消息的同时会复制转发一份消息到镜像队列。（非重点）
ActiveMQ每一个queue中消息只能被一个消费者消费，然而，有时候，你希望能够监视生产者和消费者之间的消息流。
MirroredQueue: Broker会把发送到某一个队列上的所有消息转发到一个名称类似的topic,因此监控程序只需要订阅这个topic.为启用MirroredQueue，首先要将BrokerService的useMirroredQueues属性设置为true：
<broker xmlns=http://activemq.apache.org/schema/core useMirroredQueue="true">
</broker>
 然后可以通过destinationInterceptors设置其属性，如mirrortopic的前缀，缺省是VritualTopic.Mirror.
修改后缀的配置示例：
<broker xmlns="http://activemq.apache.org/schema/core">
       <destinationInterceptors>
              <mirroredQueue copyMessage="true" postfix=".qmirror" prefix="" />
       </destinationInterceptors>
</broker>

十四、多个消费者集群消费同一个消息队列的消息；翻译过来就是一个消息要发给多个模块，而每个模块都是集群，即每个模块都有 同一个队列多个消费者
场景：某个消息队列中有10个消息，消费者A集群收到10个消息，并且消费者B集群收到同样的10个消息；集群A有两台消费者则A1收到5个消息，A2收到5个消息；集群B就一台消费者则B1收到10个消息
解决方案一：虚拟主题
开发步骤(需要更改的地方)：
    1、生产者目的地定义：具体参考 org.apache.virtualtopic.VtProducer.java
        destination = session.createTopic("VirtualTopic.vtgroup");
    2、集群A消费者目的地定义：具体参考org.apache.virtualtopic.VtConsumerA.java 和org.apache.virtualtopic.VtConsumerA2.java
        destination = session.createQueue("Consumer.A.VirtualTopic.vtgroup");//Consumer.开头是固定的，A表示某一个集群的代号
       集群B消费者目的地定义：具体参考org.apache.virtualtopic.VtConsumerB.java
        destination = session.createQueue("Consumer.B.VirtualTopic.vtgroup");//Consumer.开头是固定的，B表示某一个集群的代号
       集群C消费者目的地定义：具体参考org.apache.virtualtopic.VtConsumerc.java
        destination = session.createQueue("Consumer.C.VirtualTopic.vtgroup");//Consumer.开头是固定的，C表示某一个集群的代号
       
    
解决方案二：组合Destinations
开发步骤(需要更改的地方)：
    1、生产者目的地定义：具体参考 org.apache.compositedest.CdProducer.java
        destination = session.createQueue("cd.queue,topic://cd.mark,otherqueue");
    2、消费者目的地定义：
       集群otherqueue消费者目的地定义：具体参考 org.apache.compositedest.CdConsumerOtherQueue.java
        destination = session.createQueue("otherqueue");
       集群cd.queue消费者目的地定义：具体参考 org.apache.compositedest.CdConsumerQueueA 和org.apache.compositedest.CdConsumerQueueB
        destination = session.createQueue("cd.queue");
       集群otopic://cd.mark消费者目的地定义：具体参考 org.apache.compositedest.CdConsumerTopicA.java
        destination = session.createTopic("cd.mark");
    ps:多个消费者集群消费同一个消息队列的消息是常见的场景，以上两种方案都可以，方案二比较好理解一些，方案一队列名称需要有一定的规范    
        
十五、实战：限时订单
场景：下订单后长时间未支付，清除未支付的过期的订单
方案一、传统做法：定时扫描订单数据库表，判断哪些订单记录过期则修改其标志位或者清除
缺点：清除不够及时，浪费性能
方案二、使用java中延迟队列DelayQueue
    在单机环境下能够很好解决延迟订单问题：比如服务器正常运行时，对超时的订单进行修改数据库操作，如果服务器重启，重新将未超时的订单加载到内存，超时的订单直接修改状态。
    但是在集群环境下；重启之后，重新加载未超时的延迟订单就会和其他服务器重复。最终导致一个订单进行了多次数据库操作。
    为了解决这个重启后不加载重复的延迟订单，则应该为延迟订单表新增一个ip字段，让各自服务器处理各自的延迟订单；但是这样做又会引出新的问题；某一台服务器死机了后一直重启不了，
    则死机的服务器对应的延迟订单就没办法操作了。此时需要进行人工处理，执行sql:update 延迟订单表 set ip=某个在线服务器的ip where ip=死机服务器的ip。即使这样做还是不能从根本上解决问题。
    并且对于应用的伸缩性和拓展性都收到了限制。总而言之方案二在复杂的环境下也是不可行的。
方案三、使用mq的延迟队列
步骤1：修改配置文件(activemq.xml)，增加延迟和定时投递支持 schedulerSupport="true"
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}" schedulerSupport="true">
步骤2：编辑生产者cn.enjoyedu.service.mq.MqProducer.java
主要在于发送信息时设置延迟发送：
    Message message = session.createTextMessage(txtMsg);//创建消息
    message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, expireTime);//设置延迟发送消息的属性
步骤3：编辑消费者cn.enjoyedu.service.mq.MqConsume.java 正常的消息消费者




十六、消息服务器集群 高可用
1、master-slave主从模式（不支持消息负载，并且activemq5.8以及之前版本才支持，性能不高）
（1）共享文件形式 如果使用kahadb意味着读个消息服务端搭建在同一台服务器上，是不可行的。如果硬要使用共享文件的形式必须使用分布式文件系统，例如hdfs、fastdfs；使用文件形式相对麻烦
（2）共享数据库形式 参考 七、activemq持久化机制
   开发步骤：主、从消息服务器都要进行操作①
   ①修改 activemq.xml
    一、在broker元素上添加useJmx="true"
        <broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}" useJmx="true">
    二、替换persistenceAdapter为jdbc方式
    <persistenceAdapter>  
        <jdbcPersistenceAdapter dataDirectory="${activemq.data}" dataSource="#mysql-ds" createTablesOnStartup="false" useDatabaseLock="true"/>  
    </persistenceAdapter>
    三、添加数据源
    <bean id="mysql-ds" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/activemq?relaxAutoCommit=true&amp;useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=UTC"/>
        <property name="username" value="root"/>
        <property name="password" value=""/>
        <property name="poolPreparedStatements" value="true"/>
    </bean>
   ②客户端访问连接改为 
   private static final String BROKEURL="failover:(tcp://localhost:61616,tcp://localhost:61617,tcp://localhost:61618)?randomize=false"
   其中默认情况下消息发送到第一个地址,randomize=false表示发送或者接收消息连接到主消息服务器
   
（3）zookeeper+levelDB：（5.9以及之后版本支持）推荐使用
Leveldb是一个google实现的非常高效的kv数据库，目前的版本1.2能够支持billion级别的数据量了。 在这个数量级别下还有着非常高的性能，采用单进程的服务，性能非常之高，在一台4核Q6600的CPU机器上，
每秒钟写数据超过40w，而随机读的性能每秒钟超过10w。由此可以看出，具有很高的随机写，顺序读/写性能，但是随机读的性能很一般，也就是说，LevelDB很适合应用在查询较少，而写很多的场景。
LevelDB应用了LSM (Log Structured Merge) 策略，通过一种类似于归并排序的方式高效地将更新迁移到磁盘，降低索引插入开销。

开发步骤：Leveldb是activemq内置提供，因此不需要安装，只需配置activemq.xml文件即可
   ①修改 activemq.xml
    <persistenceAdapter> 
      <replicatedLevelDB 
        directory="${activemq.data}/leveldb" 
        replicas="3" 
        bind="tcp://0.0.0.0:62623" 
        zkAddress="127.0.0.1:2181" 
        hostname="localhost" 
        zkPath="/activemq/leveldb-stores"/> 
    </persistenceAdapter>
•	directory：持久化数据存放地址
•	replicas：集群中节点的个数
•	bind：集群通信端口
•	zkAddress：ZooKeeper集群地址 zkAddress="192.168.1.191:2181,192.168.1.192:2181,192.168.1.193:2181"
•	hostname：当前服务器的IP地址，如果集群启动的时候报未知主机名错误，那么就需要配置主机名到IP地址的映射关系。
•	zkPath：ZooKeeper数据挂载点
   ②客户端访问连接改为 
    private static final String BROKEURL="failover:(tcp://localhost:61616,tcp://localhost:61617,tcp://localhost:61618)?randomize=false"
    其中默认情况下消息发送到第一个地址,randomize=false表示发送或者接收消息连接到主消息服务器
    

2、broker-cluster(支持消息负载，会在多个服务器上进行消息路由，保证了activemq服务的伸缩性但不保证消息的可靠性)
（1）Static Discovery静态发现 集群 
    场景：有4台服务器10.211.55.2、10.211.55.6、10.211.55.7、10.211.55.8
        10.211.55.2、10.211.55.6用于接收生产者消息
        10.211.55.7、10.211.55.8用于给消费者发送消息
    Static Discovery静态发现的原理：
        生产者发送消息到10.211.55.2，会判断10.211.55.7、10.211.55.8两台服务器是否都是活跃状态，7、8两台服务是否都有消费者在等待消息，
        如果只有7是活跃的则服务器10.211.55.7先充当10.211.55.2的消费者，并把消息存储到10.211.55.7本地后再充当正在消费者的生产者，最后把消息发给真正的消费者。
        如果7和8都是活跃的状态，则消息平均分发。
    开发步骤：具体参考《20181202_1、消息中间件概述和ActiveMQ_09.vep》视频
    ①配置文件配置
        4台服务器使用的都是kahadb，并且10.211.55.2、10.211.55.6的activemq.xml不需要额外的配置
        10.211.55.7、10.211.55.8的activemq.xml需要在broker元素下添加
                <networkConnectors> 
                      <networkConnector uri="static:(tcp://10.211.55.2:61616,tcp://10.211.55.6:61616)" duplex="true"/>
                      <!--默认NetworkConnector在需要转发消息时是单向连接的。当duplex=true时，就变成了双向连接，这时配置在broker2端的指向broker1的duplex networkConnector，相当于即配置了
                          broker2到broker1的网络连接，也配置了broker1到broker2的网络连接。-->
                </networkConnectors>
        配置完后先后启动10.211.55.2、10.211.55.6、10.211.55.7、10.211.55.8上activemq服务
    ②java代码需要修改的地方：
        //生产者 参考：org.apache.brokercluster.producer.BcPCProducer.java
        private static final String BROKEURL = "failover:(tcp://10.211.55.2:61616,tcp://10.211.55.6:61616)?randomize=false";
        //消费者:org.apache.brokercluster.consumer.BcOCConsumer.java
        private static final String BROKEURL = "failover:(tcp://10.211.55.7:61616,tcp://10.211.55.8:61616)?randomize=false";
    
    ps：开发过程中不要去监听10.211.55.2、10.211.55.6上的消息;消息队列集群练习在单机情况下不好操作，启动多个activemq服务端总是提示端口被占用，放到多个虚拟机下会好一些。
    实际情况向10.211.55.2服务器发送消息时，10.211.55.2、0.211.55.7、0.211.55.8三台服务器可以作为消费者，而10.211.55.6是收不到消息的
                   向10.211.55.6服务器发送消息时，10.211.55.6、0.211.55.7、0.211.55.8三台服务器可以作为消费者，而10.211.55.2是收不到消息的
                   可分别登入http://10.211.55.2/admin 和http://10.211.55.6查看得知10.211.55.2和10.211.55.6消息是互相不可见的。
    
    
（2）Dynamic Discovery动态发现 集群
    开发步骤:
        ①配置文件修改：activemq.xml需要在
        broker元素下添加
        <networkConnectors> 
              <networkConnector uri="multicast://default" />
        </networkConnectors>
        broker》transportConnectors将
        <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        修改为
         <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"
         discoveryUri="multicast://default"/>       
        ②java代码修改
            //生产者地址
            private static final String BROKEURL = "failover:(tcp://10.211.55.2:61616,tcp://10.211.55.6:61616)?randomize=false";
            //消费者地址
            private static final String BROKEURL = "failover:(tcp://10.211.55.2:61616,tcp://10.211.55.6:61616)?randomize=false";
        
        ps：动态发现是怎么将不同服务器上的activemq服务融合在一起的？

集群使用总结：activemq broker-cluster集群，使得消息可靠性变得很差，如果要保证消息可靠性不推荐使用activemq broker-cluster集群，只要使用activemq master-slave模式，但是activemq主从模式下，没有使用集群负载情况下最高并发量在6000左右
因此为了保证消息可靠性并且并发量超出6000，应该选择rabbitmq或者rocketmq或者kafka。

相关问题：
1、activemq集群情况下无法保证消息的顺序执行，需要开发人员业务上的设计来保证消息的顺序
2、如果消息持久化在数据库，可以存放的消息要比持久化在本地文件的消息数量要多，如果消息持久化到本地文件则设置了最大存放消息数量。当消息堆积太多了就要再起一个消息消费者服务。或者让消费使用阻塞模式接收消息。
3、消息开启事务、持久化后，消息服务器的性能要下降2-10倍

