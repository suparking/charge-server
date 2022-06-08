package net.suparking.chargeserver.common;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.parking.BindingDiscountRepository;
import net.suparking.chargeserver.util.Util;

public class DiscountInfo {

    public String discountNo;

    public ValueType valueType;

    public Integer value;

    public Integer quantity;

    public String usedStartTime;

    public String usedEndTime;

    /**
     * 根据用户唯一信息,查找优惠券
     * @return
     */
    public static DiscountInfo getDiscountInfo(final String userId) {
       return bindingDiscountRepository.getDiscountInfo(userId);
    }

    public static boolean same(DiscountInfo discountInfo1, DiscountInfo discountInfo2) {
        if (discountInfo1 != null) {
            return discountInfo1.same(discountInfo2);
        } else if (discountInfo2 != null) {
            return discountInfo2.same(discountInfo1);
        } else {
            return true;
        }
    }
    private static BindingDiscountRepository bindingDiscountRepository = ChargeServerApplication.getBean("BindingDiscountRepositoryImpl", BindingDiscountRepository.class);

    public DiscountInfo() {}

    public DiscountInfo(final DiscountInfo discountInfo) {
        this.discountNo = discountInfo.discountNo;
        this.valueType = discountInfo.valueType;
        this.value = discountInfo.value;
        this.quantity = (discountInfo.quantity != null && discountInfo.quantity > 0) ? discountInfo.quantity : 1;
    }

    public DiscountInfo(final String discountNo, final ValueType valueType, final Integer value, final Integer quantity) {
        this.discountNo = discountNo;
        this.valueType = valueType;
        this.value = value;
        this.quantity = quantity;
    }

    public boolean same(final DiscountInfo discountInfo) {
        if (discountInfo != null) {
            if (discountInfo.quantity != null && discountInfo.quantity > 0 && discountNo != null && quantityEnable()) {
                return discountNo.equals(discountInfo.discountNo) && (quantity.equals(discountInfo.quantity));
            } else {
                assert discountNo != null;
                return discountNo.equals(discountInfo.discountNo);
            }
        }
        return false;
    }

    /** TODO: disDev --> 2021-06-22
     *  支付成功之后更新使用优惠劵
     * */
    public void use() {
        bindingDiscountRepository.useDiscount(discountNo);
    }

    public boolean quantityEnable() {
        return quantity != null && quantity > 0;
    }

    /** TODO: disDev --> 2021-06-22
     *  时长劵获取数据
     * */
    public int discountedTime() {
        if (quantityEnable()) {
            return valueType.equals(ValueType.MINUTE) ? (quantity * value) : 0;
        }
        return valueType.equals(ValueType.MINUTE) ? value : 0;
    }

    public int discountedAmount(int amount) {
        if (quantityEnable()) {
            if (valueType.equals(ValueType.AMOUNT)) {
                return Integer.min(amount, value * quantity);
            }
        }
        if (valueType.equals(ValueType.AMOUNT)) {
            return Integer.min(amount, value);
        } else if (valueType.equals(ValueType.RATE)) {
            return amount - Util.rate(amount, value);
        } else if (valueType.equals(ValueType.FREE)) {
            return amount;
        } else {
            return 0;
        }
    }

}
