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

十四、多个消费者集群消费同一个消息队列的消息
场景：某个消息队列中有10个消息，消费者A集群收到10个消息，并且消费者B集群收到同样的10个消息；
集群A有两台消费者则A1收到5个消息，A2收到5个消息；集群B就一台消费者则B1收到10个消息
解决方案一：虚拟主题
开发步骤：
    
解决方案二：组合Destinations
开发步骤：






相关问题：
1、activemq集群情况下无法保证消息的顺序执行，需要开发人员业务上的设计来保证消息的顺序
2、如果消息持久化在数据库，可以存放的消息要比持久化在本地文件的消息数量要多，如果消息持久化到本地文件则设置了最大存放消息数量。当消息堆积太多了就要再起一个消息消费者服务。
3、消息开启事务、持久化后，消息服务器的性能要下降2-10倍

