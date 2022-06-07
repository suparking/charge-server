package net.suparking.chargeserver.parking;

import net.suparking.chargeserver.common.DiscountInfo;

public interface BindingDiscountRepository {

    void save(BindingDiscount bindingDiscount);

    DiscountInfo getDiscountInfo(String userId);

    DiscountInfo getDiscountInfo(String projectNo, String userId);

    void useDiscount(String discountNo);
}
