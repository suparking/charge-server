package net.suparking.chargeserver.car;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * wallet account log.
 */
@ParamNotNull
@Document(collection = "online_wallet_account_log")
public class WalletAccountLog {
    @Id
    public ObjectId id;
    public WalletAccountLogType logType;
    public Integer value;
    public Integer balanceBefore;
    public Integer balanceAfter;
    public String operator;
    public Long operateTime;
    public ObjectId accountId;
    public String projectNo;

    private static WalletAccountLogRepository walletAccountLogRepository = ChargeServerApplication.getBean(
            "WalletAccountLogRepositoryImpl", WalletAccountLogRepository.class);
    private static WalletAccountLogProducer walletAccountLogProducer = ChargeServerApplication.getBean(
            "WalletAccountLogProducer", WalletAccountLogProducer.class);

    public WalletAccountLog(final WalletAccountLogType logType,
                                 final TxSnapshot snapshot,
                                 final String operator,
                                 final ObjectId accountId,
                                 final String projectNo) {
        this.logType = logType;
        this.value = snapshot.value;
        this.balanceBefore = snapshot.valueBefore;
        this.balanceAfter = snapshot.valueAfter;
        this.operator = operator;
        this.operateTime = Util.currentEpoch();
        this.accountId = accountId;
        this.projectNo = projectNo;
    }

    public void save() {
        walletAccountLogRepository.save(this);
        walletAccountLogProducer.publishWalletAccountSave("wallet_account_log", this);
    }

    @Override
    public String toString() {
        return "WalletAccountLog{" + "id=" + id + ", logType=" + logType + ", value=" + value +
                ", balanceBefore=" + balanceBefore + ", balanceAter=" + balanceAfter + ", operator='" + operator + '\'' +
                ", operateTime=" + operateTime + ", accountId=" + accountId + ", projectNo='" + projectNo + '\'' + '}';
    }
}
