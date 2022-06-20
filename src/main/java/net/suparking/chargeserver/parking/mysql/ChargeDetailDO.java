package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeDetailDO implements Comparable<ChargeDetailDO> {

    private Long id;

    private Long changeInfoId;

    private String chargeTypeName;

    private Long beginTime;

    private Long endTime;

    private Integer parkingMinutes;

    private Integer balancedMinutes;

    private Integer freeMinutes;

    private Integer discountedMinutes;

    private Integer chargingMinutes;

    private Integer chargeAmount = 0;

    private String remark;

    @Override
    public int compareTo(final ChargeDetailDO o) {
        return (int) (o.endTime - this.endTime);
    }
}
