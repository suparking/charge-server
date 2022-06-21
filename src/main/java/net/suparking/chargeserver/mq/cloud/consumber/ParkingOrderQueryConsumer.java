package net.suparking.chargeserver.mq.cloud.consumber;

import net.suparking.chargeserver.car.CarGroupTraceInfo;
import net.suparking.chargeserver.cmd.ParkingOrderQueryCommand;
import net.suparking.chargeserver.cmd.ParkingOrderQueryIn;
import net.suparking.chargeserver.cmd.ParkingOrderQueryOut;
import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.common.ValueType;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.mq.BasicMQMessage;
import net.suparking.chargeserver.mq.BasicMQMessageRet;
import net.suparking.chargeserver.mq.cloud.CloudConsumer;
import net.suparking.chargeserver.parking.EventType;
import net.suparking.chargeserver.parking.Parking;
import net.suparking.chargeserver.parking.ParkingEvent;
import net.suparking.chargeserver.parking.ParkingOrder;
import net.suparking.chargeserver.parking.ParkingState;
import net.suparking.chargeserver.parking.ParkingTrigger;
import net.suparking.chargeserver.parking.mysql.DiscountInfoDO;
import net.suparking.chargeserver.parking.mysql.ParkingDO;
import net.suparking.chargeserver.parking.mysql.ParkingEventDO;
import net.suparking.chargeserver.parking.mysql.ParkingTriggerDO;
import net.suparking.chargeserver.parking.mysql.ProjectConfig;
import net.suparking.chargeserver.parking.mysql.UserInfo;
import net.suparking.chargeserver.project.ParkingConfig;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component("CLOUD_MQ_PARKING_ORDER_QUERY")
public class ParkingOrderQueryConsumer extends CloudConsumer {

    private static class ParkingOrderQuery extends BasicMQMessage {

        // 用户驶入信息
        @ParamNotNull
        public ParkingDO parking;

        public DiscountInfoDO discountInfo;

        public ParkingTriggerDO enter;

        public List<ParkingEventDO> parkingEvents;
        public ObjectId tempCarTypeId;

        @ParamNotNull
        public ProjectConfig projectConfig;

        @ParamNotNull
        public UserInfo userInfo;

        // 会员信息
        public Parking convert(final ParkingDO parkingDO) {
            return Parking.builder()
                    .id(parkingDO.getId().toString())
                    .userId(Long.valueOf(userInfo.getUserId()))
                    .parkId(parkingDO.getParkId())
                    .parkNo(parkingDO.getParkNo())
                    .parkName(parkingDO.getParkName())
                    .deviceNo(parkingDO.getDeviceNo())
                    .enter(convert(this.enter))
                    .firstEnterTriggerTime(parkingDO.getFirstEnterTriggerTime())
                    .latestTriggerTime(parkingDO.getLatestTriggerTime())
                    .latestTriggerParkId(new ObjectId(parkingDO.getLatestTriggerParkId()))
                    .parkingEvents(convert(this.parkingEvents))
                    .parkingState(ParkingState.valueOf(parkingDO.getParkingState()))
                    .abnormalReason(parkingDO.getAbnormalReason())
                    .allowCorrect(parkingDO.getAllowCorrect().equals(1))
                    .valid(parkingDO.getValid().equals(1))
                    .payParkingId(parkingDO.getPayParkingId())
                    .remark(parkingDO.getRemark())
                    .projectNo(parkingDO.getProjectNo())
                    .creator(parkingDO.getCreator())
                    .build();
        }

        // 缺少 车辆类型ID 名称 和 合约 剩余信息.
        public ParkingTrigger convert(final ParkingTriggerDO parkingTriggerDO) {

            return ParkingTrigger.builder()
                    .recogId(parkingTriggerDO.getId().toString())
                    .recogTime(parkingTriggerDO.getRecogTime())
                    .openTime(parkingTriggerDO.getOpenTime())
                    .deviceNo(parkingTriggerDO.getDeviceNo())
                    .parkId(new ObjectId(parkingTriggerDO.getParkId()))
                    .parkNo(parkingTriggerDO.getParkNo())
                    .parkName(parkingTriggerDO.getParkName())
                    .inSubAreaId(new ObjectId(parkingTriggerDO.getInSubAreaId()))
                    .inSubAreaName(parkingTriggerDO.getInSubAreaName())
                    .outSubAreaId(new ObjectId(parkingTriggerDO.getOutSubAreaId()))
                    .outSubAreaName(parkingTriggerDO.getOutSubAreaName())
                    .operator(parkingTriggerDO.getOperator())
                    .remark(parkingTriggerDO.getRemark())
                    .build();
        }

        public DiscountInfo convert(final DiscountInfoDO discountInfoDO) {
            if (Objects.nonNull(discountInfoDO)) {
                return DiscountInfo.builder()
                        .discountNo(discountInfoDO.getDiscountNo())
                        .valueType(ValueType.valueOf(discountInfoDO.getValueType()))
                        .value(discountInfoDO.getValue())
                        .quantity(discountInfoDO.getQuantity())
                        .usedStartTime(discountInfoDO.getUsedStartTime())
                        .usedEndTime(discountInfoDO.getUsedEndTime())
                        .build();
            }
            return null;
        }

        public LinkedList<ParkingEvent> convert(List<ParkingEventDO> parkingEventsDO) {
            LinkedList<ParkingEvent> parkingEvents = new LinkedList<ParkingEvent>();
            parkingEventsDO.forEach(item -> {
                ParkingEvent parkingEvent = new ParkingEvent();
                parkingEvent.eventType = EventType.valueOf(item.getEventType());
                parkingEvent.eventTime = item.getEventTime();
                parkingEvent.parkId = new ObjectId(item.getParkId());
                parkingEvent.parkNo = item.getParkNo();
                parkingEvent.deviceNo = item.getDeviceNo();
                parkingEvent.parkName = item.getParkName();
                parkingEvent.recogId = item.getRecogId().toString();
                parkingEvent.inSubAreaId = new ObjectId(item.getInSubAreaId());
                parkingEvent.inSubAreaName = item.getInSubAreaName();
                parkingEvent.outSubAreaId = new ObjectId(item.getOutSubAreaId());
                parkingEvent.outSubAreaName = item.getOutSubAreaName();
                CarGroupTraceInfo carGroupTraceInfo = new CarGroupTraceInfo();
                carGroupTraceInfo.leftDay = item.getLeftDay();
                carGroupTraceInfo.spaceQuantity = item.getSpaceQuantity();
                parkingEvent.carGroupTraceInfo = carGroupTraceInfo;
                parkingEvents.add(parkingEvent);
            });

            return parkingEvents;
        }

        public ParkingConfig convert(ProjectConfig parkingConfigDO) {
            ParkingConfig parkingConfig = new ParkingConfig();
            parkingConfig.txTTL = parkingConfigDO.getTxTTL();
            parkingConfig.minIntervalForDupPark = parkingConfigDO.getMinIntervalForDupPark();
            parkingConfig.bindDiscountFreeMinutes = parkingConfigDO.getBindDiscountFreeMinutes();
            parkingConfig.prepayFreeMinutes = parkingConfigDO.getPrepayFreeMinutes();
            return parkingConfig;
        }
    }

    private static class ParkingOrderQueryRetMQ extends BasicMQMessageRet {

        // 用户驶入信息
        public Parking parking;
        public ParkingOrder parkingOrder;

        public ParkingOrderQueryRetMQ(String code) {
            super(code);
        }

    }

    private final ParkingOrderQueryCommand cmd;

    @Autowired
    public ParkingOrderQueryConsumer(@Qualifier("PARKING_ORDER_QUERY") ParkingOrderQueryCommand cmd) {
        super("PARKING_ORDER_QUERY_RET");
        this.cmd = cmd;
    }

    @Override
    public String consumeMessage(String message) throws Exception {
        ParkingOrderQuery parkingOrderQuery = mapper.readValue(message, ParkingOrderQuery.class);
        parkingOrderQuery.validate();

        ParkingOrderQueryIn in = new ParkingOrderQueryIn();
        in.parkingConfig = parkingOrderQuery.convert(parkingOrderQuery.projectConfig);
        in.parking = parkingOrderQuery.convert(parkingOrderQuery.parking);
        in.parking.parkingConfig = in.parkingConfig;
        in.discountInfo = parkingOrderQuery.convert(parkingOrderQuery.discountInfo);
        in.enter = parkingOrderQuery.convert(parkingOrderQuery.enter);
        in.parkingEvents = parkingOrderQuery.convert(parkingOrderQuery.parkingEvents);
        in.tempCarTypeId = parkingOrderQuery.tempCarTypeId;
        in.userInfo = parkingOrderQuery.userInfo;
        ParkingOrderQueryOut out = cmd.execute(in);

        ParkingOrderQueryRetMQ ret = new ParkingOrderQueryRetMQ(out.code);
        ret.parking = in.parking;
        ret.parkingOrder = out.parkingOrder;
        return mapper.writeValueAsString(ret);
    }
}
