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
public class ParkingEventDO extends BaseDO {

    private static final long serialVersionUID = 6317336831992629231L;

    private Long projectId;

    private String eventType;

    private Long eventTime;

    private String deviceNo;

    private String parkId;

    private String parkNo;

    private String parkName;

    private Long recogId;

    private String inSubAreaId;

    private String inSubAreaName;

    private String outSubAreaId;

    private String outSubAreaName;

    private Integer leftDay;

    private Integer spaceQuantity;

    private String operator;
}
