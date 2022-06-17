package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParkingDO extends BaseDO {
    private static final long serialVersionUID = -5296979911048492463L;

    private Long userId;

    private Long projectId;

    private String parkId;

    private String parkNo;

    private String parkName;

    private String deviceNo;

    private String carGroupId;

    private String specialType;

    private Long enter;

    private Long leave;

    private String parkingEvents;

    private Long firstEnterTriggerTime;

    private Long latestTriggerTime;

    private String latestTriggerParkId;

    private Integer latestTriggerTemp;

    private String latestTriggerTypeClass;

    private String latestTriggerTypeName;

    private String parkingState;

    private String abnormalReason;

    private Integer numberOfNight;

    private Integer allowCorrect;

    private Long matchedParkingId;

    private Integer valid;

    private Long pendingOrder;

    private String payParkingId;

    private Integer parkingMinutes;

    private String projectNo;

    private String remark;

    private String creator;

    private String modifier;
}
