package net.suparking.chargeserver.parking;

import lombok.extern.slf4j.Slf4j;
import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.common.ValueType;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Slf4j
@Document(collection = "binding_discount")
public class BindingDiscount {
    @Id
    public ObjectId id;
    public String discountNo;
    /**
     * 地锁用户ID
     */
    public String userId;
    public ValueType valueType;
    public Integer value;
    public Long expireDate;
    public Integer usedCount;
    public Integer maxAvailableCount;
    public Integer quantity;
    public Integer usedQuantity;
    // 新增优惠劵使用时间段限制
    public String usedStartTime;
    public String usedEndTime;
    public String creator;
    public Long createTime;

    private static BindingDiscountRepository bindingDiscountRepository = ChargeServerApplication.getBean(
            "BindingDiscountRepositoryImpl", BindingDiscountRepository.class);

    public BindingDiscount() {
        this.usedCount = 0;
    }

    public void save() {
        bindingDiscountRepository.save(this);
    }

    public DiscountInfo getDiscountInfo() {
        return new DiscountInfo(discountNo, valueType, value, quantity);
    }

    public boolean quantizable() {
        return quantity != null && quantity > 0 && usedQuantity != null && usedQuantity.equals(Util.MAGIC_ZERO);
    }
    /**
     * TODO: 针对是否有数量,判断优惠劵是否有效
     * @return {@link Boolean}
     */
    public boolean available() {
        return maxAvailableCount == null || usedCount < maxAvailableCount;
    }

    public boolean expired() {
        return expireDate != null && expireDate < Util.currentEpoch();
    }



    @Override
    public String toString() {
        return "BindingDiscount{" + "id=" + id + ", discountNo='" + discountNo + '\'' + ", userId='" + userId + '\'' +
               ", valueType=" + valueType + ", value=" + value + ", expireDate=" + expireDate + ", usedCount=" +
               usedCount + ", maxAvailableCount=" + maxAvailableCount + ", quantity=" + quantity + ", usedQuantity=" + usedQuantity + '\'' +
                ", usedStartTime=" + usedStartTime + ", usedEndTime=" + usedEndTime + ", creator='" + creator + '\'' +
               ", createTime=" + createTime + '}';
    }
}
