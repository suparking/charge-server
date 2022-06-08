package net.suparking.chargeserver.mq.cloud;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.config.JasonMapper;
import net.suparking.chargeserver.exception.ParamValidator;
import net.suparking.chargeserver.exception.ServerException;
import net.suparking.chargeserver.mq.BasicMQMessageRet;
import net.suparking.chargeserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

import static net.suparking.chargeserver.exception.ErrorCode.EXCEPTION;

public abstract class CloudConsumer extends ParamValidator {
    protected String method;
    protected static final JasonMapper mapper = ChargeServerApplication.getBean("JasonMapper", JasonMapper.class);
    protected static RabbitTemplate template = ChargeServerApplication.getBean("MQCloudTemplate", RabbitTemplate.class);
    protected static final Logger log = LoggerFactory.getLogger(CloudConsumer.class);

    /**
     * MQ 消费入口
     * @param message
     */
    public static void consume(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        Map<String, Object> headers = messageProperties.getHeaders();
        String method = "CLOUD_MQ_" + headers.get("method");
        String rcvts = (String)headers.get("timestamp");
        String reqBody = new String(message.getBody());

        if (ChargeServerApplication.containsBean(method)) {
            log.info("#" + method + " <== " + reqBody + " from cloud rabbit at " + rcvts);
            CloudConsumer cloudConsumer = ChargeServerApplication.getBean(method, CloudConsumer.class);
            String retBody = null;
            try {
                try {
                    retBody = cloudConsumer.consumeMessage(reqBody);
                } catch (ServerException e) {
                    log.warn("ServerException caught, " + e);
                    retBody = mapper.writeValueAsString(new BasicMQMessageRet(e.getCode(), e.getMsg()));
                } catch (Exception e) {
                    for (StackTraceElement ste: e.getStackTrace()) {
                        log.error(ste.toString());
                    }
                    retBody = mapper.writeValueAsString(new BasicMQMessageRet(EXCEPTION));
                }
            } catch (Exception e) {
                for (StackTraceElement ste: e.getStackTrace()) {
                    log.error(ste.toString());
                }
            }
            if (retBody != null) {
                String replyTo = message.getMessageProperties().getReplyTo();
                String sndts = Util.timestamp();
                log.info("#" + cloudConsumer.method + " ==> " + retBody + " to " + replyTo + " at " + sndts);

                if (replyTo != null) {
                    MessageProperties replyMessageProperties = new MessageProperties();
                    replyMessageProperties.setHeader("method", cloudConsumer.method);
                    replyMessageProperties.setCorrelationId(messageProperties.getCorrelationId());
                    Message messageRet = new Message(retBody.getBytes(), replyMessageProperties);
                    template.send(replyTo, messageRet);
                } else {
                    log.error(cloudConsumer.method + " is discarded as replyTo is null");
                }
            }
        }
    }
    public CloudConsumer(String method) {
        this.method = method;
    }
    public abstract String consumeMessage(String message) throws Exception;
}


