package net.suparking.chargeserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("ApplicationProperties")
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private Boolean debugOn = false;

    private String version = "not_defined";

    @Bean("DebugOn")
    public Boolean isDebugOn() {
        return debugOn;
    }

    @Bean("Version")
    public String getVersion() {
        return version;
    }

    public void setDebugOn(final Boolean debugOn) {
        this.debugOn = debugOn;
    }

    public void setVersion(final String version) {
        this.version = version;
    }
}
