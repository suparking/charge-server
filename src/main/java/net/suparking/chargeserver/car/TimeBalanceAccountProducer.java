package net.suparking.chargeserver.car;

import net.suparking.server.mq.BasicMQData;
import net.suparking.server.mq.local.LocalProducer;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("TimeBalanceAccountProducer")
public class TimeBalanceAccountProducer extends LocalProducer {
    public static class TimeBalanceAccountSave extends BasicMQData {
        public Object data;
        public TimeBalanceAccountSave(Object data) {
            super("time_balance_account", "save");
            this.data = data;
        }

        @Override
        public String toString() {
            return "TimeBalanceAccountSave{" + "data=" + data + "} " + super.toString();
        }
    }
    public static class TimeBalanceAccountRemove extends BasicMQData {
        public Object data;
        public TimeBalanceAccountRemove(Object data) {
            super("time_balance_account", "remove");
            this.data = data;
        }

        @Override
        public String toString() {
            return "TimeBalanceAccountRemove{" + "data=" + data + "} " + super.toString();
        }
    }
    public void publishTimeBalanceAccountSave(String topic, TimeBalanceAccount timeBalanceAccount) {
        send(topic, "DATA", new TimeBalanceAccountSave(timeBalanceAccount), success -> {
            if (success) {
                log.info("Deliver TimeBalanceAccountSave success, id = " + timeBalanceAccount.id.toString());
            } else {
                log.error("Deliver TimeBalanceAccountSave failed, id = " + timeBalanceAccount.id.toString());
            }
        });
    }
    public void publishTimeBalanceAccountRemove(String topic, List<ObjectId> ids) {
        send(topic, "DATA", new TimeBalanceAccountRemove(ids), success -> {
            if (success) {
                log.info("Deliver TimeBalanceAccountRemove success, ids = " + ids);
            } else {
                log.error("Deliver TimeBalanceAccountRemove failed, ids = " + ids);
            }
        });
    }
}
