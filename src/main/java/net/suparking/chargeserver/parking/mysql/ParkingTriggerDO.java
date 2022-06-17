package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParkingTriggerDO extends BaseDO {

    private static final long serialVersionUID = -6292785087610775485L;

    private Long projectId;

    private Long recogTime;

    private Long openTime;

    private String deviceNo;

    private String parkId;

    private String parkName;

    private String parkNo;

    private String inSubAreaId;

    private String inSubAreaName;

    private String outSubAreaId;

    private String outSubAreaName;

    private String carTypeId;

    private String carTypeName;

    private Integer leftDay;

    private Integer spaceQuantity;

    private String operator;

    private String remark;
}
