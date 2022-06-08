package net.suparking.chargeserver.car;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "online_date_type")
public class DateType extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public String dateTypeName;
    public Boolean defaultType = false;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    private static ChargeCalenderDateRepository chargeCalenderDateRepository = ChargeServerApplication.getBean(
            "ChargeCalenderDateRepositoryImpl", ChargeCalenderDateRepository.class);

    public static void reload(DateType dateType) {
        chargeCalenderDateRepository.reloadDateType(dateType);
    }

    public static void unloadById(ObjectId id) {
        chargeCalenderDateRepository.unloadDateTypeById(id);
    }

    @Override
    public String toString() {
        return "DateType{" + "id='" + id + '\'' + ", dateTypeName='" + dateTypeName + '\'' + ", defaultType=" +
               defaultType + ", projectNo='" + projectNo + '\'' + ", creator='" + creator + '\'' + ", createTime=" +
               createTime + ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime + '}';
    }
}
