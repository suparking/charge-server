package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.car.ChargeTypeRepository;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "charge_type")
public class ChargeType extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public String chargeTypeName;
    @ParamNotNull
    public ChargeRule chargeRule;
    public Boolean defaultType = false;
    public String remark;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    public static void reload(ChargeType chargeType) {
        chargeTypeRepository.reload(chargeType);
    }

    public static void unloadById(ObjectId id) {
        chargeTypeRepository.unloadById(id);
    }

    public static ChargeType findById(ObjectId id) {
        return chargeTypeRepository.findById(id);
    }

    public static ChargeType findByDefault() {
        return chargeTypeRepository.findByDefault();
    }

    private static ChargeTypeRepository chargeTypeRepository = ChargeServerApplication.getBean(
            "ChargeTypeRepositoryImpl", ChargeTypeRepository.class);

    private static final Logger log = LoggerFactory.getLogger(ChargeType.class);

    public ChargeType() {}

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        return chargeRule.validate();
    }

    public boolean chargeFree() {
        return chargeRule.chargeForFree();
    }

    public boolean validForMerge(ChargeType chargeType) {
        return chargeRule.validForMerge(chargeType.chargeRule);
    }

    @Override
    public String toString() {
        return "ChargeType{" + "id=" + id + ", chargeTypeName='" + chargeTypeName + '\'' + ", chargeRule=" +
               chargeRule + ", defaultType=" + defaultType + ", remark='" + remark + '\'' + ", projectNo='" +
               projectNo + '\'' + ", creator='" + creator + '\'' + ", createTime=" + createTime + ", modifier='" +
               modifier + '\'' + ", modifyTime=" + modifyTime + '}';
    }
}
