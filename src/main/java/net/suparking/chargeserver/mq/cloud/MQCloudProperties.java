package net.suparking.chargeserver.mq.cloud;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration("MQCloudProperties")
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class MQCloudProperties {

    private String host = "ms.suparking.cn";

    private Integer port = 5666;

    private String virtualHost = "/";

    private String userName = "charge";

    private String userPassword = "charge2022";

    private Integer consumerPrefetch = 5;

    private Integer concurrentConsumer = 2;

    private Boolean enable = true;
}
