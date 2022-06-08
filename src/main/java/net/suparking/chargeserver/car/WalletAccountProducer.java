package net.suparking.chargeserver.car;

import net.suparking.server.mq.BasicMQData;
import net.suparking.server.mq.local.LocalProducer;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("WalletAccountProducer")
public class WalletAccountProducer extends LocalProducer {
    public static class WalletAccountSave extends BasicMQData {
        public Object data;
        public WalletAccountSave(Object data) {
            super("wallet_account", "save");
            this.data = data;
        }

        @Override
        public String toString() {
            return "WalletAccountSave{" + "data=" + data + "} " + super.toString();
        }
    }
    public static class WalletAccountRemove extends BasicMQData {
        public Object data;
        public WalletAccountRemove(Object data) {
            super("wallet_account", "remove");
            this.data = data;
        }

        @Override
        public String toString() {
            return "WalletAccountRemove{" + "data=" + data + "} " + super.toString();
        }
    }
    public void publishWalletAccountSave(String topic, WalletAccount walletAccount) {
        send(topic, "DATA", new WalletAccountSave(walletAccount), success -> {
            if (success) {
                log.info("Deliver WalletAccountSave success, id = " + walletAccount.id.toString());
            } else {
                log.error("Deliver WalletAccountSave failed, id = " + walletAccount.id.toString());
            }
        });
    }
    public void publishWalletAccountRemove(String topic, List<ObjectId> ids) {
        send(topic, "DATA", new WalletAccountRemove(ids), success -> {
            if (success) {
                log.info("Deliver WalletAccountRemove success, ids = " + ids);
            } else {
                log.error("Deliver WalletAccountRemove failed, ids = " + ids);
            }
        });
    }
}
