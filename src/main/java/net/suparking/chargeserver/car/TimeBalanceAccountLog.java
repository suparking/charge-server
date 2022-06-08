package net.suparking.chargeserver.car;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ParamNotNull
@Document(collection = "online_time_balance_account_log")
public class TimeBalanceAccountLog {
    @Id
    public ObjectId id;
    public TimeBalanceAccountLogType logType;
    public Integer value;
    public Integer balanceBefore;
    public Integer balanceAfter;
    public String operator;
    public Long operateTime;
    public ObjectId accountId;
    public String projectNo;

    private static TimeBalanceAccountLogRepository timeBalanceAccountLogRepository = ChargeServerApplication.getBean(
            "TimeBalanceAccountLogRepositoryImpl", TimeBalanceAccountLogRepository.class);
    private static TimeBalanceAccountLogProducer timeBalanceAccountLogProducer = ChargeServerApplication.getBean(
            "TimeBalanceAccountLogProducer", TimeBalanceAccountLogProducer.class);

    public TimeBalanceAccountLog(TimeBalanceAccountLogType logType,
                                 TxSnapshot snapshot,
                                 String operator,
                                 ObjectId accountId,
                                 String projectNo) {
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
        timeBalanceAccountLogRepository.save(this);
        timeBalanceAccountLogProducer.publishTimeBalanceAccountSave("time_balance_account_log", this);
    }

    @Override
    public String toString() {
        return "TimeBalanceAccountLog{" + "id=" + id + ", logType=" + logType + ", value=" + value +
               ", balanceBefore=" + balanceBefore + ", balanceAter=" + balanceAfter + ", operator='" + operator + '\'' +
               ", operateTime=" + operateTime + ", accountId=" + accountId + ", projectNo='" + projectNo + '\'' + '}';
    }
}
