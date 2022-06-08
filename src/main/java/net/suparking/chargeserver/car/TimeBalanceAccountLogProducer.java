package net.suparking.chargeserver.car;

import net.suparking.server.mq.BasicMQData;
import net.suparking.server.mq.local.LocalProducer;
import org.springframework.stereotype.Component;

@Component("TimeBalanceAccountLogProducer")
public class TimeBalanceAccountLogProducer extends LocalProducer {
    public static class TimeBalanceAccountLogSave extends BasicMQData {
        public Object data;
        public TimeBalanceAccountLogSave(Object data) {
            super("time_balance_account_log", "save");
            this.data = data;
        }

        @Override
        public String toString() {
            return "TimeBalanceAccountLogSave{" + "data=" + data + "} " + super.toString();
        }
    }
    public void publishTimeBalanceAccountSave(String topic, TimeBalanceAccountLog timeBalanceAccountLog) {
        send(topic, "DATA", new TimeBalanceAccountLogSave(timeBalanceAccountLog), success -> {
            if (success) {
                log.info("Deliver TimeBalanceAccountLogSave success, id = " + timeBalanceAccountLog.id);
            } else {
                log.error("Deliver TimeBalanceAccountLogSave failed, id = " + timeBalanceAccountLog.id);
            }
        });
    }
}
