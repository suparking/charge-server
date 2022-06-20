package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeInfoDO implements Serializable {

    private static final long serialVersionUID = 9104829773833342278L;

    private Long id;

    private Long parkingOrderId;

    private Integer beginCycleSeq;

    private Integer cycleNumber;

    private Integer parkingMinutes;

    private Integer balancedMinutes;

    private Integer discountedMinutes;

    private Integer totalAmount;

    private Integer extraAmount;
}
