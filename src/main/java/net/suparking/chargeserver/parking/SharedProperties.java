package net.suparking.chargeserver.parking;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration("SharedProperties")
@ConfigurationProperties(prefix = "shared.http")
public class SharedProperties {
    private String orderUrl;

    private String customerUrl;
}
