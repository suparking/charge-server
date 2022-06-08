package net.suparking.chargeserver.car;

import net.suparking.server.mq.BasicMQData;
import net.suparking.server.mq.local.LocalProducer;
import org.springframework.stereotype.Component;

/**
 * wallet account log producer.
 */
@Component("WalletAccountLogProducer")
public class WalletAccountLogProducer extends LocalProducer {
    public static class WalletAccountLogSave extends BasicMQData {
        public Object data;
        public WalletAccountLogSave(final Object data) {
            super("wallet_account_log", "save");
            this.data = data;
        }

        @Override
        public String toString() {
            return "WalletAccountLogSave{" + "data=" + data + "} " + super.toString();
        }
    }
    public void publishWalletAccountSave(String topic, WalletAccountLog walletAccountLog) {
        send(topic, "DATA", new WalletAccountLogSave(walletAccountLog), success -> {
            if (success) {
                log.info("Deliver TimeBalanceAccountLogSave success, id = " + walletAccountLog.id);
            } else {
                log.error("Deliver TimeBalanceAccountLogSave failed, id = " + walletAccountLog.id);
            }
        });
    }
}
