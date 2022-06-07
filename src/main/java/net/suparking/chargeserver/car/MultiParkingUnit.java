package net.suparking.chargeserver.car;

import lombok.Builder;
import lombok.Data;
import net.suparking.chargeserver.charge.AmountPerUnits;

import java.util.List;

/**
 * 累加计费时间范围,以自然日为主
 */
@Data
@Builder
public class MultiParkingUnit {
    private String addBegin;
    private String addEnd;
    private boolean addEnabled;
    private boolean rangeFreeInCharge;
    private int rangeFreeMinutes;
    private int minUnitLength;
    private List<AmountPerUnits> amountPerUnits;
}
