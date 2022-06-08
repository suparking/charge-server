package net.suparking.chargeserver.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.suparking.chargeserver.car.CarContext;
import net.suparking.chargeserver.car.Recog;
import net.suparking.chargeserver.common.CarTypeClass;
import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.common.SpecialType;
import net.suparking.chargeserver.project.ParkingConfig;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static net.suparking.chargeserver.parking.EventType.EV_ORDER_CREATE;
import static net.suparking.chargeserver.parking.ParkingState.ENTERED;
import static net.suparking.chargeserver.parking.ParkingState.ENTERED_FORCE;
import static net.suparking.chargeserver.parking.ParkingState.LEAVE_UNMATCHED;
import static net.suparking.chargeserver.parking.ParkingState.PASS_UNMATCHED;
import static net.suparking.chargeserver.parking.ParkingState.PAY_WAIT;

@Data
@Builder
@AllArgsConstructor
public class Parking {
    // 停车记录id
     public String id;

     public String userId;

     // 车位编号,车位名称
     public String parkNo;

     public String parkName;

     // 设备编号
     public String deviceNo;

     public ObjectId carGroupId;

     public SpecialType specialType;

     public ParkingTrigger enter;

     public ParkingTrigger leave;

     public Long firstEnterTriggerTime;

     public Long latestTriggerTime;

     public ObjectId latestTriggerParkId;

     public Boolean latestTriggerTemp;

    public CarTypeClass latestTriggerTypeClass;
    public String latestTriggerTypeName;

    public LinkedList<ParkingEvent> parkingEvents = new LinkedList<>();

    public ParkingState parkingState;

    public String abnormalReason;

    public Integer numberOfNight;

    public Boolean allowCorrect;

    public String matchedParkingId;

    public Boolean valid;

    public ParkingOrder pendingOrder;

    public String payParkingId;

    public Integer parkingMinutes;

    public String remark;
    public String projectNo;

    public ParkingConfig parkingConfig;

    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;
    private static final Logger log = LoggerFactory.getLogger(Parking.class);
    public Parking() {}

    public Parking(Recog recog, CarContext carContext, ParkingConfig parkingConfig) {
        this.parkNo = recog.getParkNo();
        this.parkName = recog.getParkName();
        this.deviceNo = recog.getDeviceNo();
        this.userId = recog.getUserId();
        resetCarContextInfo(carContext);
        this.allowCorrect = true;
        this.valid = true;
        this.payParkingId = new ObjectId(Util.currentTime(), Util.incPayNo()).toString();
        this.projectNo = recog.getProjectNo();
        this.creator = "system";
        this.createTime = recog.getRecogTime();
        this.parkingConfig = parkingConfig;
    }

    public Parking(String userId, CarContext carContext, ParkingConfig parkingConfig) {
        this.userId = userId;
        resetCarContextInfo(carContext);
        this.allowCorrect = true;
        this.valid = true;
        this.payParkingId = new ObjectId(Util.currentTime(), Util.incPayNo()).toString();
        this.projectNo = carContext.getProjectNo();
        this.creator = "system";
        this.createTime = Util.currentEpoch();
        this.parkingConfig = parkingConfig;
    }

    public void resetCarContextInfo(CarContext carContext) {
        carGroupId = carContext.getCarGroupId();
        specialType = carContext.getSpecialType();
        latestTriggerTemp = !carContext.active();
        latestTriggerTypeClass = carContext.getCarTypeClass();
        latestTriggerTypeName = carContext.getCarTypeName();
    }

    /**
     * TODO: 3 --> 根据优惠劵ID
     * @param discountInfo
     * @param tempCarTypeId
     * @return
     */
    public ParkingOrder queryOrder(final DiscountInfo discountInfo, ObjectId tempCarTypeId) {
        CarContext carContext = CarContext.findCarContext(userId);
        ParkingOrder parkingOrder = new ParkingOrder(this, carContext);
        //  TODO: 离场费用计费如果付费端没有指定优惠劵,那么会查询是否存在绑定劵,如果存在则使用
        parkingOrder.discountInfo = discountInfo;

        long now = Util.currentEpoch();
        ParkingEvent parkingEvent = new ParkingEvent(now, EV_ORDER_CREATE, lastEvent());

        LinkedList<ParkingEvent> events = cloneEvents();
        events.addLast(parkingEvent);
        parkingOrder.updateForParkingEvents(events, tempCarTypeId, carContext, parkingConfig);
        return parkingOrder;
    }

    private LinkedList<ParkingEvent> cloneEvents() {
        LinkedList<ParkingEvent> events = new LinkedList<>();
        for (ParkingEvent event: parkingEvents) {
            events.add(new ParkingEvent(event));
        }
        return events;
    }
    public void logTrace(Park park, String info) {
        if (park != null) {
            log.info(latestTriggerTypeName
                    + ":" + userId
                    + " >> " + park.parkNo + '-' + park.parkName
                    + " -- " + info);
        } else {
            log.info(latestTriggerTypeName
                    + ":" + userId
                    + " -- " + info);
        }
    }
    public boolean present() {
        return valid && validForState(ENTERED, ENTERED_FORCE, PASS_UNMATCHED, LEAVE_UNMATCHED, PAY_WAIT);
    }
    public boolean entered() {
        return present() && Objects.nonNull(enter) && !parkingEvents.isEmpty();
    }
    public ParkingEvent lastEvent() {
        return parkingEvents.peekLast();
    }

    public boolean waitingForPay() {
        return valid && validForState(PAY_WAIT);
    }

    private boolean validForState(ParkingState ...states) {
        List<ParkingState> parkingStates = new ArrayList<>(Arrays.asList(states));
        if (parkingStates.contains(parkingState)) {
            switch (parkingState) {
                case ENTER_WAIT:
                case PASS_UNMATCHED:
                    return Objects.nonNull(enter);
                case ENTERED:
                case ENTERED_FORCE:
                    return Objects.nonNull(enter) && !parkingEvents.isEmpty();
                case LEAVE_UNMATCHED:
                    return leave != null;
                case LEFT:
                    return Objects.nonNull(enter) && leave != null && !parkingEvents.isEmpty();
                case LEFT_ABNORMAL:
                case LEFT_FORCE:
                    return leave != null && !parkingEvents.isEmpty();
                case PAY_WAIT:
                    return Objects.nonNull(enter) && leave != null && !parkingEvents.isEmpty() && pendingOrder != null;
                default:
                    log.error("Should not happen, parkingState = " + parkingState);
            }
        }
        return false;
    }
}
