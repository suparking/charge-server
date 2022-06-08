package net.suparking.chargeserver.car;

import lombok.Data;
import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

@Data
@Document(collection = "online_car_group")
public class CarGroup extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public List<String> userIds;
    @ParamNotNull
    public String carTypeName;
    @ParamNotNull
    public ObjectId carTypeId;
    @ParamNotNull
    public String protocolName;
    @ParamNotNull
    public ProtocolType protocolType;
    @ParamNotNull
    public ObjectId protocolId;
    public List<Period> periods;
    public String importNo;
    public List<String> paperInfos;
    public String userName;
    public String userMobile;
    public String address;
    public String remark;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    public static CarGroup findByUserId(String userId) {
        return carGroupRepository.findByUserId(userId);
    }

    public static CarGroup findById(ObjectId id) {
        return carGroupRepository.findById(id);
    }

    public static List<CarGroup> findByProtocolId(ObjectId id) {
        return carGroupRepository.findByProtocolId(id);
    }

    private static CarGroupRepository carGroupRepository = ChargeServerApplication.getBean(
            "CarGroupRepositoryImpl", CarGroupRepository.class);

    public boolean timeBased() {
        return protocolType.equals(ProtocolType.TIME_BASED);
    }

    public boolean longTermed() {
        return protocolType.equals(ProtocolType.LONG_TERM);
    }

    public boolean active() {
        return longTermed() || activePeriod() != null;
    }

    public Period activePeriod() {
        if (periods != null) {
            Collections.sort(periods);
            ListIterator<Period> it = periods.listIterator();
            Period last = null;
            while (it.hasNext()) {
                Period period = it.next();
                if (period.beginDate >= period.endDate) {
                    it.remove();
                    continue;
                }
                if (last == null || period.beginDate > last.endDate) {
                    last = period;
                } else {
                    if (period.endDate > last.endDate) {
                        last.endDate = period.endDate;
                    }
                    it.remove();
                }
            }

            long now = Util.currentEpoch();
            for (it = periods.listIterator(); it.hasNext();) {
                Period p = it.next();
                if (now >= p.beginDate && now <= p.endDate) {
                    return p;
                }
            }
        }
        return null;
    }

    public Integer leftDay() {
        Period period = activePeriod();
        if (period != null) {
            return (int) (period.endDate - Util.currentEpoch()) / Util.daySeconds + 1;
        } else {
            return null;
        }
    }

    public Integer userIdCount() {
        return userIds.size();
    }

    @Override
    public String toString() {
        return "CarGroup{" + "id=" + id + ", userIds=" + userIds + ", carTypeName='" + carTypeName + '\'' +
               ", carTypeId=" + carTypeId + ", protocolName='" + protocolName + '\'' + ", protocolType=" +
               protocolType + ", protocolId=" + protocolId + ", periods=" + periods + ", importNo='" + importNo + '\'' +
               ", paperInfos=" + paperInfos + ", userName='" + userName + '\'' + ", userMobile='" + userMobile + '\'' +
               ", address='" + address + '\'' + ", remark='" + remark + '\'' + ", projectNo='" + projectNo + '\'' +
               ", creator='" + creator + '\'' + ", createTime=" + createTime + ", modifier='" + modifier + '\'' +
               ", modifyTime=" + modifyTime + "} " + super.toString();
    }
}
