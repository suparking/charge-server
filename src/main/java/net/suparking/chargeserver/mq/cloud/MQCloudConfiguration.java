package net.suparking.chargeserver.mq.cloud;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Order(42)
@Configuration("MQCloudConfiguration")
public class MQCloudConfiguration {

    private final MQCloudProperties mqCloudProperties;

    private static final Logger log = LoggerFactory.getLogger(MQCloudConfiguration.class);

    @Autowired
    public MQCloudConfiguration(@Qualifier("MQCloudProperties") final MQCloudProperties mqCloudProperties) {
        this.mqCloudProperties = mqCloudProperties;
    }

    //云消息连接工厂
    @Bean("MQCloudConnectionFactory")
    public CachingConnectionFactory cloudConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                mqCloudProperties.getHost(), mqCloudProperties.getPort());
        connectionFactory.setUsername(mqCloudProperties.getUserName());
        connectionFactory.setPassword(mqCloudProperties.getUserPassword());

        connectionFactory.addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                log.info("MQ cloud connection is created");
            }
            @Override
            public void onShutDown(ShutdownSignalException signal) {
                log.warn("MQ cloud connection is shutdown due to " + signal.getMessage());
            }
        });
        connectionFactory.addChannelListener(new ChannelListener() {
            @Override
            public void onCreate(Channel channel, boolean b) {
                log.info("MQ cloud channel is created");
            }
            @Override
            public void onShutDown(ShutdownSignalException signal) {
                log.warn("MQ cloud channel is shutdown due to " + signal.getMessage());
            }
        });
        return connectionFactory;
    }

    @Bean("MQCloudAMQPAdmin")
    public AmqpAdmin cloudAmqpAdmin(@Qualifier("MQCloudTemplate") RabbitTemplate template) {
        RabbitAdmin admin = new RabbitAdmin(template);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new AlwaysRetryPolicy());
        admin.setRetryTemplate(retryTemplate);
        return admin;
    }

    //云交换机
    @Bean("MQCloudExchange")
    public TopicExchange cloudExchange(@Qualifier("MQCloudAMQPAdmin")AmqpAdmin admin) {
        TopicExchange exchange = new TopicExchange("spk.shared");
        exchange.setShouldDeclare(false);
        return exchange;
    }

    //云Template
    @Bean("MQCloudTemplate")
    public RabbitTemplate mqCloudTemplate(@Qualifier("MQCloudConnectionFactory")CachingConnectionFactory factory) {
        return new RabbitTemplate(factory);
    }

    @Bean("MQCloudQueue")
    public Queue cloudQueue(@Qualifier("MQCloudAMQPAdmin")AmqpAdmin admin) {
        String queueName = "charge";
        Queue queue = new Queue(queueName, false, true, true);
        queue.setAdminsThatShouldDeclare(admin);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean("MQCloudBinding")
    public Binding cloudBinding(@Qualifier("MQCloudAMQPAdmin")AmqpAdmin admin,
                                @Qualifier("MQCloudQueue")Queue queue,
                                @Qualifier("MQCloudExchange")TopicExchange exchange) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with("*.shared.#");
        binding.setAdminsThatShouldDeclare(admin);
        binding.setShouldDeclare(true);
        return binding;
    }

    //云消息监听容器
    @Bean("MQCloudMessageListenerContainer")
    @ConditionalOnProperty(name = "cloudmq.enable", matchIfMissing = true)
    public DirectMessageListenerContainer cloudMessageListenerContainer(
            @Qualifier("MQCloudConnectionFactory")CachingConnectionFactory connectionFactory,
            @Qualifier("MQCloudAMQPAdmin")AmqpAdmin admin,
            @Qualifier("MQCloudQueue")Queue queue
    ) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAmqpAdmin(admin);
        container.setQueueNames(queue.getName());
        container.setPrefetchCount(mqCloudProperties.getConsumerPrefetch());
        container.setConsumersPerQueue(mqCloudProperties.getConcurrentConsumer());
        container.setAcknowledgeMode(AcknowledgeMode.NONE);
        container.setMessageListener(CloudConsumer::consume);
        return container;
    }
}
