package net.suparking.chargeserver.mq.cloud.consumber;

import com.fasterxml.jackson.databind.JavaType;
import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.config.JasonMapper;
import net.suparking.chargeserver.mq.cloud.CloudConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicDataHandler {
    protected static final JasonMapper mapper = ChargeServerApplication.getBean("JasonMapper", JasonMapper.class);
    protected static final Logger log = LoggerFactory.getLogger(CloudConsumer.class);
    public abstract void handle(String type, String message) throws Exception;
    protected static <T> T readAsObject(Object object, Class<T> tClass) throws Exception {
        if (object instanceof String) {
            return mapper.readValue((String)object, tClass);
        } else {
            return mapper.convertValue(object, tClass);
        }
    }
    protected static <T> T readAsObject(Object object, JavaType javaType) throws Exception {
        if (object instanceof String) {
            return mapper.readValue((String)object, javaType);
        } else {
            return mapper.convertValue(object, javaType);
        }
    }
}
