package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParkingOrderDO extends BaseDO {

    private static final long serialVersionUID = -3064987299814485320L;

    private Long userId;

    private String orderNo;

    private String payParkingId;

    private Integer tempType;

    private String carTypeClass;

    private String carTypeName;

    private String carTypeId;

    private Long beginTime;

    private Long endTime;

    private Long nextAggregateBeginTime;

    private Integer aggregatedMaxAmount;

    private Integer parkingMinutes;

    private Integer totalAmount;

    private Integer discountedMinutes;

    private Integer discountedAmount;

    private Integer chargeAmount;

    private Integer extraAmount;

    private Integer dueAmount;

    private Integer chargeDueAmount;

    private Integer paidAmount;

    private String payChannel;

    private String payType;

    private Long payTime;

    private Integer receivedAmount;

    private String termNo;

    private String operator;

    private Long expireTime;

    private String invoiceState;

    private String refundState;

    private String projectNo;

    private String creator;

    private String modifier;
}
