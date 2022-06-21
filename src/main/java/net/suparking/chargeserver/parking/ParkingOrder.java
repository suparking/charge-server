package net.suparking.chargeserver.parking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.car.CarContext;
import net.suparking.chargeserver.car.CarGroup;
import net.suparking.chargeserver.car.CarType;
import net.suparking.chargeserver.car.ChargeCalender;
import net.suparking.chargeserver.car.ExtraPolicy;
import net.suparking.chargeserver.car.MaxAmountForMultiParking;
import net.suparking.chargeserver.car.MultiMaxPolicy;
import net.suparking.chargeserver.car.MultiParkingUnit;
import net.suparking.chargeserver.car.Period;
import net.suparking.chargeserver.charge.ChargeDetail;
import net.suparking.chargeserver.charge.ChargeFrameInfo;
import net.suparking.chargeserver.charge.ChargeHandler;
import net.suparking.chargeserver.charge.ChargeInfo;
import net.suparking.chargeserver.charge.ChargePeriod;
import net.suparking.chargeserver.common.CarTypeClass;
import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.common.ValueType;
import net.suparking.chargeserver.parking.mysql.ChargeDetailDO;
import net.suparking.chargeserver.parking.mysql.ChargeInfoVO;
import net.suparking.chargeserver.parking.mysql.DiscountInfoDO;
import net.suparking.chargeserver.parking.mysql.ParkingOrderVO;
import net.suparking.chargeserver.project.ParkingConfig;
import net.suparking.chargeserver.util.HttpUtils;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static net.suparking.chargeserver.parking.EventType.EV_CAR_TYPE_CHANGE;
import static net.suparking.chargeserver.parking.EventType.EV_CHARGE_TYPE_CHANGE;
import static net.suparking.chargeserver.parking.EventType.EV_PREPAY;
import static net.suparking.chargeserver.util.ChargeConstants.SUCCESS;

public class ParkingOrder {
    public String orderNo;
    public String payParkingId;
    public Long userId;
    public Boolean tempType;
    public CarTypeClass carTypeClass;
    public String carTypeName;
    public ObjectId carTypeId;
    public Long beginTime;
    public Long endTime;
    public Long nextAggregateBeginTime;
    public Integer aggregatedMaxAmount;
    public Integer parkingMinutes;
    public DiscountInfo discountInfo;
    public LinkedList<ChargeInfo> chargeInfos;
    public Integer totalAmount;
    public Integer discountedMinutes;
    public Integer discountedAmount;
    public HistoryOrderTraceInfo historyOrderTraceInfo;
    public Integer chargeAmount;
    public Integer extraAmount;
    public Integer dueAmount;
    // 实际计算金额
    public Integer chargeDueAmount;
    // 预付金额
    public Integer paidAmount;
    public String payChannel;
    public PayType payType;
    public Long payTime;
    public Integer receivedAmount;
    public String termNo;
    public String operator;
    public Long bestBefore;
    // expireTime 等拿到计费信息等待支付时候赋值
    public Long expireTime;
    public String remark;
    public String comments;
    public InvoiceState invoiceState;
    public RefundState refundState;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    public static final String serverTermNo = "000";
    public static final String BIND_DISCOUNT_TITLE = "PLN";

    private static final Logger log = LoggerFactory.getLogger(ParkingOrder.class);

    private static final SharedProperties sharedProperties = ChargeServerApplication.getBean("SharedProperties", SharedProperties.class);

    public static List<ParkingOrder> findByUserIdsAndBeginTimeOrEndTimeRange(String projectNo, List<String> userIds, long begin, long end) {
        ParkingQuery parkingQuery = ParkingQuery.builder()
                .projectNo(projectNo)
                .userIds(userIds)
                .begin(begin)
                .end(end)
                .build();
        JSONObject result = HttpUtils.sendPost(sharedProperties.getOrderUrl() + "/parking-order/findByPlateNosAndBeginTimeOrEndTimeRange", JSON.toJSONString(parkingQuery));

        if (Objects.nonNull(result) && result.containsKey("code") && result.getInteger("code").equals(SUCCESS)) {
            List<ParkingOrderVO> parkingOrderVOList = (List<ParkingOrderVO>) result.get("data");
            if (parkingOrderVOList.size() > 0) {
               return convert(parkingOrderVOList);
            }
        }
        return Collections.emptyList();


    }

    /**
     * ParkingOrderVO -> ParkingOrder.
     * @param parkingOrderVO {@link ParkingOrderVO}
     * @return {@link ParkingOrder}
     */
    private static ParkingOrder convert(final ParkingOrderVO parkingOrderVO) {
        ParkingOrder parkingOrder = new ParkingOrder();
        parkingOrder.orderNo = parkingOrderVO.getOrderNo();
        parkingOrder.payParkingId = parkingOrderVO.getPayParkingId();
        parkingOrder.userId = parkingOrderVO.getUserId();
        parkingOrder.tempType = parkingOrderVO.getTempType().equals(1);
        parkingOrder.carTypeClass = CarTypeClass.valueOf(parkingOrderVO.getCarTypeClass());
        parkingOrder.carTypeName = parkingOrderVO.getCarTypeName();
        parkingOrder.carTypeId = new ObjectId(parkingOrderVO.getCarTypeId());
        parkingOrder.beginTime = parkingOrderVO.getBeginTime();
        parkingOrder.endTime = parkingOrderVO.getEndTime();
        parkingOrder.nextAggregateBeginTime = parkingOrderVO.getNextAggregateBeginTime();
        parkingOrder.aggregatedMaxAmount = parkingOrderVO.getAggregatedMaxAmount();
        parkingOrder.parkingMinutes = parkingOrderVO.getParkingMinutes();

        // 增加 优惠券信息,如果存在
        DiscountInfoDO discountInfoDO = parkingOrderVO.getDiscountInfoDO();
        if (Objects.nonNull(discountInfoDO)) {
            DiscountInfo discountInfo = new DiscountInfo();
            discountInfo.setDiscountNo(discountInfoDO.getDiscountNo());
            discountInfo.setValueType(ValueType.valueOf(discountInfoDO.getValueType()));
            discountInfo.setValue(discountInfoDO.getValue());
            discountInfo.setQuantity(discountInfoDO.getQuantity());
            discountInfo.setUsedStartTime(discountInfoDO.getUsedStartTime());
            discountInfo.setUsedEndTime(discountInfoDO.getUsedEndTime());
            parkingOrder.discountInfo = discountInfo;
        }

        LinkedList<ChargeInfoVO> chargeInfoVOList = parkingOrderVO.getChargeInfos();
        if (Objects.nonNull(chargeInfoVOList) && chargeInfoVOList.size() > 0) {
            LinkedList<ChargeInfo>  chargeInfos = new LinkedList<>();
            chargeInfoVOList.forEach(chargeInfoVO -> {
                ChargeInfo chargeInfo = new ChargeInfo();
                chargeInfo.beginCycleSeq = chargeInfoVO.getBeginCycleSeq();
                chargeInfo.cycleNumber = chargeInfoVO.getCycleNumber();
                chargeInfo.parkingMinutes = chargeInfoVO.getParkingMinutes();
                chargeInfo.balancedMinutes = chargeInfoVO.getBalancedMinutes();
                chargeInfo.discountedMinutes = chargeInfoVO.getDiscountedMinutes();
                chargeInfo.totalAmount = chargeInfoVO.getTotalAmount();
                chargeInfo.extraAmount = chargeInfoVO.getExtraAmount();


                LinkedList<ChargeDetailDO> chargeDetailDOList = chargeInfoVO.getChargeDetailDOList();
                if (Objects.nonNull(chargeDetailDOList) && chargeDetailDOList.size() > 0) {
                    LinkedList<ChargeDetail> chargeDetails = new LinkedList<>();
                    chargeDetailDOList.forEach(chargeDetailDO -> {
                        ChargeDetail chargeDetail = new ChargeDetail();
                        chargeDetail.chargeTypeName  = chargeDetailDO.getChargeTypeName();
                        chargeDetail.beginTime = chargeDetailDO.getBeginTime();
                        chargeDetail.endTime = chargeDetailDO.getEndTime();
                        chargeDetail.parkingMinutes = chargeDetailDO.getParkingMinutes();
                        chargeDetail.balancedMinutes = chargeDetailDO.getBalancedMinutes();
                        chargeDetail.freedMinutes = chargeDetailDO.getFreeMinutes();
                        chargeDetail.discountedMinutes = chargeDetailDO.getDiscountedMinutes();
                        chargeDetail.chargingMinutes = chargeDetailDO.getChargingMinutes();
                        chargeDetail.chargeAmount = chargeDetailDO.getChargeAmount();
                        chargeDetail.remark = chargeDetailDO.getRemark();
                        chargeDetails.add(chargeDetail);
                    });
                    chargeInfo.chargeDetails = chargeDetails;
                }
                chargeInfos.add(chargeInfo);
            });
            parkingOrder.chargeInfos = chargeInfos;
        }
        parkingOrder.totalAmount = parkingOrderVO.getTotalAmount();
        parkingOrder.discountedMinutes = parkingOrderVO.getDiscountedMinutes();
        parkingOrder.discountedAmount = parkingOrderVO.getDiscountedAmount();
        parkingOrder.chargeAmount = parkingOrderVO.getChargeAmount();
        parkingOrder.extraAmount = parkingOrderVO.getExtraAmount();
        parkingOrder.dueAmount = parkingOrderVO.getDueAmount();
        parkingOrder.chargeDueAmount = parkingOrderVO.getChargeDueAmount();
        parkingOrder.paidAmount = parkingOrderVO.getPaidAmount();
        parkingOrder.payChannel = parkingOrderVO.getPayChannel();
        parkingOrder.payType = PayType.valueOf(parkingOrderVO.getPayType());
        parkingOrder.payTime = parkingOrderVO.getPayTime();
        parkingOrder.receivedAmount = parkingOrderVO.getReceivedAmount();
        parkingOrder.termNo = parkingOrderVO.getTermNo();
        parkingOrder.operator = parkingOrderVO.getOperator();
        parkingOrder.expireTime = parkingOrderVO.getExpireTime();
        parkingOrder.invoiceState = InvoiceState.valueOf(parkingOrderVO.getInvoiceState());
        parkingOrder.refundState = RefundState.valueOf(parkingOrderVO.getRefundState());
        parkingOrder.projectNo = parkingOrderVO.getProjectNo();
        parkingOrder.creator = parkingOrderVO.getCreator();
        parkingOrder.modifier = parkingOrderVO.getModifier();
        return parkingOrder;
    }

    /**
     * ParkingOrderVOList -> ParkingOrderList.
     * @param parkingOrderVOList {@link ParkingOrderVO}
     * @return {@link ParkingOrder}
     */
    private static List<ParkingOrder> convert(final List<ParkingOrderVO> parkingOrderVOList) {
        List<ParkingOrder> parkingOrderList = new ArrayList<>(parkingOrderVOList.size());
        parkingOrderVOList.forEach(parkingOrderVO -> {
            ParkingOrder parkingOrder = new ParkingOrder();
            parkingOrder.orderNo = parkingOrderVO.getOrderNo();
            parkingOrder.payParkingId = parkingOrderVO.getPayParkingId();
            parkingOrder.userId = parkingOrderVO.getUserId();
            parkingOrder.tempType = parkingOrderVO.getTempType().equals(1);
            parkingOrder.carTypeClass = CarTypeClass.valueOf(parkingOrderVO.getCarTypeClass());
            parkingOrder.carTypeName = parkingOrderVO.getCarTypeName();
            parkingOrder.carTypeId = new ObjectId(parkingOrderVO.getCarTypeId());
            parkingOrder.beginTime = parkingOrderVO.getBeginTime();
            parkingOrder.endTime = parkingOrderVO.getEndTime();
            parkingOrder.nextAggregateBeginTime = parkingOrderVO.getNextAggregateBeginTime();
            parkingOrder.aggregatedMaxAmount = parkingOrderVO.getAggregatedMaxAmount();
            parkingOrder.parkingMinutes = parkingOrderVO.getParkingMinutes();

            // 增加 优惠券信息,如果存在
            DiscountInfoDO discountInfoDO = parkingOrderVO.getDiscountInfoDO();
            if (Objects.nonNull(discountInfoDO)) {
                DiscountInfo discountInfo = new DiscountInfo();
                discountInfo.setDiscountNo(discountInfoDO.getDiscountNo());
                discountInfo.setValueType(ValueType.valueOf(discountInfoDO.getValueType()));
                discountInfo.setValue(discountInfoDO.getValue());
                discountInfo.setQuantity(discountInfoDO.getQuantity());
                discountInfo.setUsedStartTime(discountInfoDO.getUsedStartTime());
                discountInfo.setUsedEndTime(discountInfoDO.getUsedEndTime());
                parkingOrder.discountInfo = discountInfo;
            }

            LinkedList<ChargeInfoVO> chargeInfoVOList = parkingOrderVO.getChargeInfos();
            if (Objects.nonNull(chargeInfoVOList) && chargeInfoVOList.size() > 0) {
               LinkedList<ChargeInfo>  chargeInfos = new LinkedList<>();
               chargeInfoVOList.forEach(chargeInfoVO -> {
                   ChargeInfo chargeInfo = new ChargeInfo();
                   chargeInfo.beginCycleSeq = chargeInfoVO.getBeginCycleSeq();
                   chargeInfo.cycleNumber = chargeInfoVO.getCycleNumber();
                   chargeInfo.parkingMinutes = chargeInfoVO.getParkingMinutes();
                   chargeInfo.balancedMinutes = chargeInfoVO.getBalancedMinutes();
                   chargeInfo.discountedMinutes = chargeInfoVO.getDiscountedMinutes();
                   chargeInfo.totalAmount = chargeInfoVO.getTotalAmount();
                   chargeInfo.extraAmount = chargeInfoVO.getExtraAmount();


                   LinkedList<ChargeDetailDO> chargeDetailDOList = chargeInfoVO.getChargeDetailDOList();
                   if (Objects.nonNull(chargeDetailDOList) && chargeDetailDOList.size() > 0) {
                       LinkedList<ChargeDetail> chargeDetails = new LinkedList<>();
                       chargeDetailDOList.forEach(chargeDetailDO -> {
                           ChargeDetail chargeDetail = new ChargeDetail();
                           chargeDetail.chargeTypeName  = chargeDetailDO.getChargeTypeName();
                           chargeDetail.beginTime = chargeDetailDO.getBeginTime();
                           chargeDetail.endTime = chargeDetailDO.getEndTime();
                           chargeDetail.parkingMinutes = chargeDetailDO.getParkingMinutes();
                           chargeDetail.balancedMinutes = chargeDetailDO.getBalancedMinutes();
                           chargeDetail.freedMinutes = chargeDetailDO.getFreeMinutes();
                           chargeDetail.discountedMinutes = chargeDetailDO.getDiscountedMinutes();
                           chargeDetail.chargingMinutes = chargeDetailDO.getChargingMinutes();
                           chargeDetail.chargeAmount = chargeDetailDO.getChargeAmount();
                           chargeDetail.remark = chargeDetailDO.getRemark();
                           chargeDetails.add(chargeDetail);
                       });
                       chargeInfo.chargeDetails = chargeDetails;
                   }
                   chargeInfos.add(chargeInfo);
               });
                parkingOrder.chargeInfos = chargeInfos;
            }
            parkingOrder.totalAmount = parkingOrderVO.getTotalAmount();
            parkingOrder.discountedMinutes = parkingOrderVO.getDiscountedMinutes();
            parkingOrder.discountedAmount = parkingOrderVO.getDiscountedAmount();
            parkingOrder.chargeAmount = parkingOrderVO.getChargeAmount();
            parkingOrder.extraAmount = parkingOrderVO.getExtraAmount();
            parkingOrder.dueAmount = parkingOrderVO.getDueAmount();
            parkingOrder.chargeDueAmount = parkingOrderVO.getChargeDueAmount();
            parkingOrder.paidAmount = parkingOrderVO.getPaidAmount();
            parkingOrder.payChannel = parkingOrderVO.getPayChannel();
            parkingOrder.payType = PayType.valueOf(parkingOrderVO.getPayType());
            parkingOrder.payTime = parkingOrderVO.getPayTime();
            parkingOrder.receivedAmount = parkingOrderVO.getReceivedAmount();
            parkingOrder.termNo = parkingOrderVO.getTermNo();
            parkingOrder.operator = parkingOrderVO.getOperator();
            parkingOrder.expireTime = parkingOrderVO.getExpireTime();
            parkingOrder.invoiceState = InvoiceState.valueOf(parkingOrderVO.getInvoiceState());
            parkingOrder.refundState = RefundState.valueOf(parkingOrderVO.getRefundState());
            parkingOrder.projectNo = parkingOrderVO.getProjectNo();
            parkingOrder.creator = parkingOrderVO.getCreator();
            parkingOrder.modifier = parkingOrderVO.getModifier();
            parkingOrderList.add(parkingOrder);
        });
        return parkingOrderList;
    }

    public static List<ParkingOrder> findByUserIdsAndEndTimeRange(String projectNo, List<String> userIds, long begin, long end) {
        ParkingQuery parkingQuery = ParkingQuery.builder()
                .projectNo(projectNo)
                .userIds(userIds)
                .begin(begin)
                .end(end)
                .build();
        JSONObject result = HttpUtils.sendPost(sharedProperties.getOrderUrl() + "/parking-order/findByUserIdsAndEndTimeRange", JSON.toJSONString(parkingQuery));

        if (Objects.nonNull(result) && result.containsKey("code") && result.getInteger("code").equals(SUCCESS)) {
            List<ParkingOrderVO> parkingOrderVOList = (List<ParkingOrderVO>) result.get("data");
            if (parkingOrderVOList.size() > 0) {
                return convert(parkingOrderVOList);
            }
        }
        return Collections.emptyList();

    }

    public static ParkingOrder findNextAggregateBeginTime(String projectNo, List<String> userIds) {
        ParkingQuery parkingQuery = ParkingQuery.builder()
                .projectNo(projectNo)
                .userIds(userIds)
                .build();
        JSONObject result = HttpUtils.sendPost(sharedProperties.getOrderUrl() + "/parking-order/findNextAggregateBeginTime", JSON.toJSONString(parkingQuery));

        if (Objects.nonNull(result) && result.containsKey("code") && result.getInteger("code").equals(SUCCESS)) {
            ParkingOrderVO parkingOrderVO = (ParkingOrderVO) result.get("data");
            if (Objects.nonNull(parkingOrderVO)) {
                return convert(parkingOrderVO);
            }
        }
        return null;
    }

    public ParkingOrder() {}

    public ParkingOrder(Parking parking, CarContext carContext) {
        this.userId = parking.userId;
        this.payParkingId = parking.payParkingId;
        this.tempType = !carContext.active();
        this.carTypeClass = carContext.getCarTypeClass();
        this.carTypeName = carContext.getCarTypeName();
        this.carTypeId = carContext.getCarTypeId();
        this.nextAggregateBeginTime = 0L;
        this.aggregatedMaxAmount = 0;
        this.parkingMinutes = 0;
        this.totalAmount = 0;
        this.discountedMinutes = 0;
        this.discountedAmount = 0;
        this.chargeAmount = 0;
        this.extraAmount = 0;
        this.dueAmount = 0;
        this.chargeDueAmount = 0;
        this.paidAmount = 0;
        this.operator = "system";
        ParkingConfig parkingConfig = parking.getParkingConfig();
        expireTime = Util.expireTime(parkingConfig.txTTL);
        this.projectNo = parking.getProjectNo();
        this.invoiceState = InvoiceState.UNISSUED;
        this.refundState = RefundState.NONE;
        this.creator = "system";
        this.createTime = Util.currentEpoch();
    }

    /**
     * TODO: 核心计算费用 --> 根据停车事件,临时车辆类型, 车辆上下文 计算费用
     * @param events
     * @param tempTypeId
     * @param carContext
     */
    public void updateForParkingEvents(LinkedList<ParkingEvent> events, ObjectId tempTypeId, CarContext carContext, ParkingConfig parkingConfig) {
        if (events.size() < 2) {
            log.error("Not enough event to update order, " + events.toString());
            remark = "无效订单";
            return;
        }
        beginTime = Util.shapeDayBegin(events.getFirst().eventTime);
        endTime = Util.shapeDayEnd(events.getLast().eventTime);

        log.info("用户: " + userId + " 优惠劵检查前计费结束时间: " + endTime);
        // TODO 判断是否有绑定劵
        Long tmpEndTime;
        // 优惠券使用者享受减免免费时长
        tmpEndTime = checkDiscountForFree(discountInfo, beginTime, endTime, parkingConfig);
        log.info("用户: " + userId + " 优惠劵检查后计费结束时间: " + tmpEndTime);
        if (!tmpEndTime.equals(endTime)) {
            log.info("用户: " + userId + " 优惠劵检查后计费结束时间由 " + endTime + " ==> " + tmpEndTime);
            events.getLast().eventTime = tmpEndTime;
        }

        if (beginTime >= endTime) {
            log.error("Invalid begin/end time: [" + beginTime + "," + endTime + "]");
            remark = "无效订单";
            return;
        }

        /** 停车时长是用原始的开始与结束时间计算,计费时长是经过处理 */
        boolean prepaid = resetBeginTime(events);
        parkingMinutes = Util.timeGapToMinute(beginTime, endTime);

        //  判断存在预交费
        if (prepaid) {
            if (Util.timeGapToMinute(beginTime, endTime) <= parkingConfig.prepayFreeMinutes) {
                // free period not input  periods.
                updateForMultiMax(carContext, null);
                return;
            }
        }

//        ParkingOrder historyOrder = findHistoryByPayParkingId(payParkingId);
//        if (historyOrder != null) {
//            beginTime = Util.shapeDayBegin(historyOrder.endTime);
//            parkingMinutes = Util.timeGapToMinute(beginTime, endTime);
//            if (historyOrder.withinBestBefore(endTime)) {
//                parkingMinutes = Util.timeGapToMinute(beginTime, endTime);
//                return;
//            }
//        }

        LinkedList<Frame> rawFrames = makeRawFrames(beginTime, events);
        LinkedList<Frame> unpaidFrames = makeCarGroupFrames(rawFrames, carContext);
        LinkedList<ChargePeriod> periods = buildChargePeriods(carContext.getProjectNo(), unpaidFrames, tempTypeId);

        if (periods.isEmpty()) {
            log.error("Empty periods, should not happen!");
            remark = "无效订单";
            return;
        }

        // txTTL 最小事物时长 分钟
        List<ChargeFrameInfo> chargeFrameInfos = ChargeHandler.buildChargeFrameInfo(carContext.getProjectNo(), periods);
        // 通过计费规则类型等于FREE 返回真正的计费时长
        int chargeMinutes = ChargeHandler.chargeFrameInfoToGapTime(chargeFrameInfos);
        /** TODO: 计费核心 --> 根据计算出来的计费时间段,优惠时长,时长账户,进行最终的时长计算 */
        chargeInfos = ChargeHandler.genChargeInfo(chargeFrameInfos, chargeMinutes, discountedMinutes, 0);

        sumTotalAmount();
        updateForMultiMax(carContext, periods);
        updateForExtra(carContext);
        updateForDiscount();
    }

    public boolean payable() {
        return receivedAmount > 0
               || discountInfo != null;
    }

    public boolean expired() {
        return expireTime != null && expireTime < Util.currentEpoch();
    }

    public void finish(PayInfo payInfo, CarContext carContext) {
        orderNo = Util.makeOrderNo(carContext.getProjectNo(), payInfo.getTermNo());
        payChannel = payInfo.getPayChannel();
        payType = payInfo.getPayType();
        payTime = Util.currentEpoch();
        receivedAmount = payInfo.getPayAmount();
        termNo = payInfo.getTermNo();
        operator = payInfo.getOperator();
        modifier = payInfo.getOperator();
        remark = payInfo.getRemark();
        finish(carContext);
    }

    public void finish(CarContext carContext) {
        payTime = Util.currentEpoch();
        if (discountInfo != null) {
//            discountInfo.use();
        }
    }
    public int effectiveAmount() {
        int walletAmount =  0;
        return receivedAmount + walletAmount;
    }
    public int effectiveAmount(long begin, long end) {
        int amount1 = receivedAmount;
        int amount2 = 0;
        if (amount1 > 0) {
            for (ChargeInfo ci: chargeInfos) {
                int m = ci.overlappedMinutes(begin, end);
                if (m > 0) {
                    if (ci.cycleNumber > 1) {
                        int cycleMinutes = ci.parkingMinutes / ci.cycleNumber;
                        int cycleAmount = ci.totalAmount / ci.cycleNumber;
                        int effectiveCycleNumber = Util.lengthToUnit(m, cycleMinutes);
                        amount2 += cycleAmount * effectiveCycleNumber;
                    } else {
                        amount2 += ci.totalAmount;
                    }
                }
            }
        }
        return Integer.min(amount1, amount2);
    }

    /**
     * 判断是否是绑定劵,如果是绑定劵则 判断是否设置了减免时长,如果设置了,那就将结束时间相应的往前推
     * @param discountInfo {DiscountInfo}
     * @param beginTime {Long}
     * @param time {Long}
     * @return
     */
    private Long checkDiscountForFree(final DiscountInfo discountInfo, final Long beginTime, final Long time, ParkingConfig parkingConfig) {
       Long endTime = time;
       if (!ObjectUtils.isEmpty(discountInfo) && !ObjectUtils.isEmpty(time)) {
          if (discountInfo.discountNo.startsWith(BIND_DISCOUNT_TITLE)) {
             if (!ObjectUtils.isEmpty(parkingConfig) && !ObjectUtils.isEmpty(parkingConfig.bindDiscountFreeMinutes) &&
                     parkingConfig.bindDiscountFreeMinutes > 0) {
                 if ((endTime - beginTime) > (parkingConfig.bindDiscountFreeMinutes * 60)) {
                     endTime -= parkingConfig.bindDiscountFreeMinutes * 60;
                 } else {
                     endTime = beginTime;
                 }
                 return endTime;
             }
          }
       }
       return endTime;
    }

    private int findBeginEventIndex(long beginTime, LinkedList<ParkingEvent> events) {
        int idx = 0;
        for (; idx < events.size(); ++idx) {
            if (Util.round(events.get(idx).eventTime, beginTime)) {
                return idx;
            }
        }
        return idx;
    }


    private boolean resetBeginTime(LinkedList<ParkingEvent> events) {
        int lastIdx = 0;
        boolean result = false;
        for (int idx = 0; idx < events.size(); ++idx) {
            if (EV_PREPAY.equals(events.get(idx).eventType)) {
                lastIdx = idx;
                result = true;
            }
        }
        beginTime = events.get(lastIdx).eventTime;
        return result;
    }

    private LinkedList<Frame> makeRawFrames(long beginTime, LinkedList<ParkingEvent> events) {
        LinkedList<Frame> frames = new LinkedList<>();
        for (int idx = findBeginEventIndex(beginTime, events); idx + 1 < events.size(); ++idx) {
            ParkingEvent beginEvent = events.get(idx);
            beginEvent.eventTime = Util.shapeDayBegin(beginEvent.eventTime);
            ParkingEvent endEvent = events.get(idx+1);
            endEvent.eventTime = Util.shapeDayEnd(endEvent.eventTime);
            Frame frame = new Frame(beginEvent, endEvent);
            frame.parkingShared = beginEvent.parkingShared();
            frame.subAreaId = endEvent.outSubAreaId;
            frames.add(frame);
        }
        return frames;
    }

    private LinkedList<Frame> makeCarGroupFrames(LinkedList<Frame> unpaidFrames, CarContext carContext) {
        LinkedList<Frame> frames = unpaidFrames;
        ObjectId carTypeId = carContext.getCarTypeId();
        for (Frame frame: unpaidFrames) {
            frame.carTypeId = carTypeId;
        }

        if (!carContext.timeBased()) {
            return frames;
        }

        frames = new LinkedList<>();
        CarGroup carGroup = carContext.getCarGroup();
        CarType tempCarType = null;
        // add carGroup expired and carType change.
        if (Objects.nonNull(carContext.getProtocol().expiredCarTypeId)) {
            tempCarType = CarType.findById(carContext.getProjectNo(), new ObjectId(carContext.getProtocol().expiredCarTypeId));
        }
        ObjectId tempCarTypeId = tempCarType.id;
        ObjectId groupTypeId = carContext.getGroupType().id;
        for (Frame frame: unpaidFrames) {
            for (Period period: carGroup.periods) {
                if (period.endDate <= frame.beginEvent.eventTime) {
                    frame.carTypeId = tempCarTypeId;
                    continue;
                }
                if (frame.endEvent.eventTime <= period.beginDate) {
                    frame.carTypeId = tempCarTypeId;
                    break;
                }
                if (frame.endEvent.eventTime <= period.endDate) {
                    frame.carTypeId = groupTypeId;
                    if (frame.beginEvent.eventTime < period.beginDate) {
                        long newEndEpoch = Util.shapeDayEnd(period.beginDate);
                        ParkingEvent newEndEvent = new ParkingEvent(newEndEpoch, EV_CAR_TYPE_CHANGE, frame.beginEvent);
                        ParkingEvent newBeginEvent = new ParkingEvent(newEndEpoch + 1,
                                                                      EV_CAR_TYPE_CHANGE,
                                                                      frame.beginEvent);
                        Frame newFrame = new Frame(frame.beginEvent, newEndEvent);
                        newFrame.parkingShared = frame.parkingShared;
                        newFrame.subAreaId = frame.subAreaId;
                        newFrame.carTypeId = tempCarTypeId;
                        frames.addLast(newFrame);
                        frame.beginEvent = newBeginEvent;
                    }
                    break;
                } else {
                    long newEndEpoch1 = Util.shapeDayEnd(period.endDate);
                    ParkingEvent newEndEvent1 = new ParkingEvent(newEndEpoch1, EV_CAR_TYPE_CHANGE, frame.beginEvent);
                    ParkingEvent newBeginEvent1 = new ParkingEvent(newEndEpoch1 + 1,
                                                                   EV_CAR_TYPE_CHANGE,
                                                                   frame.beginEvent);
                    Frame newFrame1 = new Frame(frame.beginEvent, newEndEvent1);
                    newFrame1.parkingShared = frame.parkingShared;
                    newFrame1.subAreaId = frame.subAreaId;
                    newFrame1.carTypeId = groupTypeId;
                    frame.beginEvent = newBeginEvent1;
                    frame.carTypeId = tempCarTypeId;
                    if (newFrame1.beginEvent.eventTime < period.beginDate) {
                        long newEndEpoch2 = Util.shapeDayEnd(period.beginDate);
                        ParkingEvent newEndEvent2 = new ParkingEvent(newEndEpoch2,
                                                                     EV_CAR_TYPE_CHANGE,
                                                                     newFrame1.beginEvent);
                        ParkingEvent newBeginEvent2 = new ParkingEvent(newEndEpoch2 + 1,
                                                                       EV_CAR_TYPE_CHANGE,
                                                                       newFrame1.beginEvent);
                        Frame newFrame2 = new Frame(newFrame1.beginEvent, newEndEvent2);
                        newFrame2.parkingShared = newFrame1.parkingShared;
                        newFrame2.subAreaId = newFrame1.subAreaId;
                        newFrame2.carTypeId = tempCarTypeId;
                        frames.addLast(newFrame2);
                        newFrame1.beginEvent = newBeginEvent2;
                    }
                    frames.addLast(newFrame1);
                }
            }
            frames.addLast(frame);
        }

        return frames;
    }

    /**
     * TODO: 5 --> 构建计费时间段
     * @param unpaidFrames
     * @param tempCarTypeId
     * @return
     */
    private LinkedList<ChargePeriod> buildChargePeriods(String projectNo, LinkedList<Frame> unpaidFrames, ObjectId tempCarTypeId) {
        LinkedList<ChargePeriod> periods = new LinkedList<>();
        for (Frame frame: unpaidFrames) {
            Calendar beginCalendar = Util.dateToCalendar(frame.beginEvent.eventTime);
            Calendar endCalendar = Util.dateToCalendar(frame.endEvent.eventTime);

            ObjectId beginDateTypeId = ChargeCalender.getDateTypeIdByCalender(projectNo, beginCalendar);
            if (tempCarTypeId != null && CarType.tempType(projectNo, frame.carTypeId)) {
                frame.carTypeId = tempCarTypeId;
            }

            CarType carType = CarType.findById(projectNo, frame.carTypeId);
            frame.chargeTypeId = carType.findChargeTypeId(frame.parkingShared, frame.subAreaId, beginDateTypeId);

            for (Calendar preDay = beginCalendar, nextDay = Util.nextDay(preDay);
                 Util.dayCompare(nextDay, endCalendar) <= 0;
                 preDay = nextDay, nextDay = Util.nextDay(preDay)) {
                ObjectId nextDateTypeId = ChargeCalender.getDateTypeIdByCalender(projectNo, nextDay);
                ObjectId nextChargeTypeId = carType.findChargeTypeId(frame.parkingShared, frame.subAreaId, nextDateTypeId);
                if (nextChargeTypeId.equals(frame.chargeTypeId)) {
                    continue;
                }
                long newEndEpoch = Util.dayEndTime(preDay.toInstant().getEpochSecond());
                periods.addLast(new ChargePeriod(frame.beginEvent.eventTime, newEndEpoch, frame.chargeTypeId));
                frame.beginEvent.eventType = EV_CHARGE_TYPE_CHANGE;
                frame.beginEvent.eventTime = newEndEpoch + 1;
                frame.chargeTypeId = nextChargeTypeId;
            }

            periods.addLast(new ChargePeriod(frame.beginEvent.eventTime, frame.endEvent.eventTime, frame.chargeTypeId));
        }
        return periods;
    }

    private void sumTotalAmount() {
        totalAmount = 0;
        for (ChargeInfo ci: chargeInfos) {
            totalAmount += ci.totalAmount;
        }
        dueAmount = chargeAmount = totalAmount;
    }

    private ObjectId getChargeTypeId(final Long eventTime, final LinkedList<ChargePeriod> periods) {
        if (!ObjectUtils.isEmpty(periods)) {
            for(ChargePeriod period : periods) {
                if (period.beginTime <= eventTime && period.endTime >= eventTime ){
                    return period.chargeTypeId;
                }
            }
        }
        return new ObjectId();
    }
    /**
     * 更新
     * @param carContext
     */
    private void updateForMultiMax(CarContext carContext, LinkedList<ChargePeriod> periods) {
        MultiMaxPolicy multiMaxPolicy = carContext.multiMaxPolicy(beginTime, endTime);
        if (multiMaxPolicy != null) {
            if (multiMaxPolicy.fromEnter()) {
                int m = multiMaxPolicy.getMinutes();
                int max = multiMaxPolicy.getMaxAmount();
                ParkingOrder lastOrder = findNextAggregateBeginTime(carContext.getProjectNo(), multiMaxPolicy.userIds);
                long nextBegin = 0;
                int aggregateMax = 0;
                if (lastOrder != null) {
                    nextBegin = lastOrder.nextAggregateBeginTime != null ? lastOrder.nextAggregateBeginTime : lastOrder.endTime;
                    aggregateMax = lastOrder.aggregatedMaxAmount != null ? lastOrder.aggregatedMaxAmount : 0;
                }
                if (dueAmount > 0) {
                    if (nextBegin < beginTime) {
                        int n = Util.lengthToUnit(parkingMinutes, m);
                        int totalMaxAmount = n * max;
                        nextAggregateBeginTime = beginTime + (long) n * m * Util.minuteSeconds;
                        dueAmount = chargeAmount = Integer.min(chargeAmount, totalMaxAmount);
                        aggregatedMaxAmount = totalMaxAmount - dueAmount;
                    } else if (nextBegin >= endTime) {
                        nextAggregateBeginTime = nextBegin;
                        dueAmount = chargeAmount = Integer.min(aggregateMax, dueAmount);
                        aggregatedMaxAmount = aggregateMax - dueAmount;
                    } else {
                        int effMinutes = Util.timeGapToMinute(nextBegin, endTime);
                        int n = Util.lengthToUnit(effMinutes, m);
                        int totalMaxAmount = n * max + aggregateMax;
                        nextAggregateBeginTime = nextBegin + (long) n * m * Util.minuteSeconds;
                        dueAmount = chargeAmount = Integer.min(chargeAmount, totalMaxAmount);
                        aggregatedMaxAmount = totalMaxAmount - dueAmount;
                    }
                } else {
                    nextAggregateBeginTime = nextBegin;
                    aggregatedMaxAmount = 0;
                }
            } else if (multiMaxPolicy.dayRange()) {

                // 判断此车辆类型是否需要进行不计次数判断,这里时根据车辆类型中的不计次数配置决定的，与合约无关
                MaxAmountForMultiParking maxAmountForMultiParking = carContext.getCarType().maxAmountForMultiParking;
                if (!ObjectUtils.isEmpty(maxAmountForMultiParking.multiParkingUnits)) {
                    log.info("用户:[{}],开通了周期内累加计费不限次数配置,下面进入累加流程处理", carContext.getUserId());
                    /**
                     * 已有参数:
                     * 周期:          开始结束时间
                     * 累加时间范围:   开始结束时间
                     * 1. 通过开始结束时间查询范围内的订单数据
                     * 2. 拿到数据之后循环计算出这个范围内的停车总时长
                     * 3. 拿着总时长按照 multiParkingUnits 配置的计费规则进行计算费用,与 计费规则算出来的费用进行筛选
                     * 注: 进入这个流程目前先不管计费金额是否>0
                     */
                    int tmpTotalParkingMinutes = 0;
                    int tmpTotalParkingAmount = 0;
                    int tmpTotalDueAmount = 0;
                    /**
                     * 根据 parkingMinutes / 1440 = UnitLength
                     * 1. 根据 beginTime 找到
                     */
                    for (MultiParkingUnit multiParkingUnit : maxAmountForMultiParking.multiParkingUnits) {
                        tmpTotalParkingMinutes = 0;
                        tmpTotalParkingAmount = 0;
                        Date currentDate = Util.currentTime();
                        // 判断入场时间所在周期是否需要累加
                        if (!Util.currentSimpleYMD(currentDate).equals(Util.currentSimpleYMD(new Date(beginTime * 1000))) &&
                                getChargeTypeId(beginTime, periods).equals(carContext.getCarType().defaultChargeTypeId)) {
                            List<Long> entryAddDateSeconds = Util.timeToSecond(new Date(beginTime * 1000), multiParkingUnit.getAddBegin(), multiParkingUnit.getAddEnd());
                            if (entryAddDateSeconds.isEmpty()) {
                                log.warn("车辆类型:{},入场不限次数累加时间范围有误",carContext.getCarTypeName());
                                return;
                            }
                            long entryCycleBegin = entryAddDateSeconds.get(0);
                            long entryCycleEnd = entryAddDateSeconds.get(1);
                            if (beginTime > entryCycleBegin && beginTime < entryCycleEnd) {
                                // search order
                                // 2. 根据当前订单的结束时间所在的年月日周期 查询是否存在已支付订单
                                List<ParkingOrder> parkingOrders = findByUserIdsAndBeginTimeOrEndTimeRange(carContext.getProjectNo(), multiMaxPolicy.userIds, entryCycleBegin, entryCycleEnd);
                                if (!parkingOrders.isEmpty()) {
                                    // 计算未整合之前的计费这个时间段的费用
                                    int tmpEntryLastAmount = 0;
                                    int tmpEntryDueAmount = 0;
                                    int tmpEntryAddAmount = Util.multiParkingAmount(Util.secondToMinute(entryCycleEnd - beginTime), multiParkingUnit);
                                    log.info("用户:" + multiMaxPolicy.userIds +"计费模块计算出金额:" + dueAmount);
                                    dueAmount -= tmpEntryAddAmount;
                                    int tmpEntryParkingMinutes = 0;
                                    // 如果查询当前累加周期未查询到支付订单,就根据当前订单的开始时间 判断所在的周期内
                                    for (ParkingOrder parkingOrder : parkingOrders) {
                                        if (parkingOrder.beginTime >= entryCycleBegin && parkingOrder.endTime <= entryCycleEnd) {
                                            tmpEntryParkingMinutes += parkingOrder.parkingMinutes;
                                            if (Objects.nonNull(parkingOrder.discountInfo)) {
                                                tmpEntryDueAmount += parkingOrder.totalAmount;
                                            } else {
                                                tmpEntryDueAmount += parkingOrder.dueAmount;
                                            }
                                        } else if (parkingOrder.beginTime < entryCycleBegin && parkingOrder.endTime < entryCycleEnd) {
                                            tmpEntryParkingMinutes += Util.secondToMinute(parkingOrder.endTime - entryCycleBegin);
                                            tmpEntryDueAmount += Util.multiParkingAmount(Util.secondToMinute(parkingOrder.endTime - entryCycleBegin), multiParkingUnit);
                                        } else if (parkingOrder.endTime> entryCycleEnd && parkingOrder.beginTime > entryCycleBegin && parkingOrder.beginTime < entryCycleEnd){
                                            tmpEntryParkingMinutes += Util.secondToMinute(entryCycleEnd - parkingOrder.beginTime);
                                            tmpEntryDueAmount += Util.multiParkingAmount(Util.secondToMinute(entryCycleEnd - parkingOrder.beginTime), multiParkingUnit);
                                        }
                                    }
                                    tmpEntryParkingMinutes += Util.secondToMinute(entryCycleEnd - beginTime);
                                    // 计算玩累计范围内的所有订单时间那么下面拿着时间进行费用计算
                                    if (tmpEntryParkingMinutes > 0) {
                                        tmpEntryLastAmount = Util.multiParkingAmount(tmpEntryParkingMinutes, multiParkingUnit);
                                    }
                                    dueAmount += Integer.min(tmpEntryLastAmount, maxAmountForMultiParking.maxAmount) - tmpEntryDueAmount;
                                }
                            }
                        }

                        if (getChargeTypeId(endTime, periods).equals(carContext.getCarType().defaultChargeTypeId)) {
                            // 1. 查询离场时间在累加范围内的订单
                            List<Long> addDateSecond = Util.timeToSecond(currentDate, multiParkingUnit.getAddBegin(), multiParkingUnit.getAddEnd());
                            if (addDateSecond.isEmpty()) {
                                log.warn("车辆类型:{},不限次数累加时间范围有误",carContext.getCarTypeName());
                                return;
                            }
                            long addCycleBegin = addDateSecond.get(0);
                            long addCycleEnd = addDateSecond.get(1);
                            log.info("计算出累加开始与结束时间:" + addCycleBegin + "," + addCycleEnd);
                            // 判断是否需要累加同时判断当前开始或者结束是否有在当前累加周期内
                            if (!multiParkingUnit.isAddEnabled() || beginTime > addCycleEnd || endTime < addCycleBegin) {
                                continue;
                            }
                            // 2. 根据当前订单的结束时间所在的年月日周期 查询是否存在已支付订单
                            List<ParkingOrder> parkingOrders = findByUserIdsAndBeginTimeOrEndTimeRange(carContext.getProjectNo(), multiMaxPolicy.userIds, addCycleBegin, addCycleEnd);
                            if (!parkingOrders.isEmpty()) {
                                // 如果查询当前累加周期未查询到支付订单,就根据当前订单的开始时间 判断所在的周期内
                                for (ParkingOrder parkingOrder : parkingOrders) {
                                    if (parkingOrder.beginTime >= addCycleBegin && parkingOrder.endTime <= addCycleEnd) {
                                        tmpTotalParkingMinutes += parkingOrder.parkingMinutes;
                                        // 进出场时间全在周期内所以费用可以直接累加
                                        if (Objects.nonNull(parkingOrder.discountInfo)) {
                                           tmpTotalDueAmount += parkingOrder.totalAmount;
                                        } else {
                                            tmpTotalDueAmount += parkingOrder.dueAmount;
                                        }
                                    } else if (parkingOrder.beginTime < addCycleBegin && addCycleEnd > endTime) {
                                        tmpTotalParkingMinutes += Util.secondToMinute(parkingOrder.endTime - addCycleBegin);
                                        tmpTotalDueAmount += Util.multiParkingAmount(Util.secondToMinute(parkingOrder.endTime - addCycleBegin), multiParkingUnit);
                                    } else if (addCycleEnd < endTime && beginTime > addCycleBegin){
                                        tmpTotalParkingMinutes += Util.secondToMinute(addCycleEnd - parkingOrder.beginTime);
                                        tmpTotalDueAmount += Util.multiParkingAmount(Util.secondToMinute(addCycleEnd - parkingOrder.beginTime), multiParkingUnit);
                                    }
                                }
                                int tmpLeaveDueAmount = 0;
                                // 以往订单 + 本次停车时长
                                if (beginTime >= addCycleBegin && endTime <= addCycleEnd) {
                                    tmpTotalParkingMinutes += parkingMinutes;
                                    tmpLeaveDueAmount += Util.multiParkingAmount(parkingMinutes, multiParkingUnit);
                                } else if (beginTime < addCycleBegin && addCycleEnd > endTime) {
                                    tmpTotalParkingMinutes += Util.secondToMinute(endTime - addCycleBegin);
                                    tmpLeaveDueAmount += Util.multiParkingAmount(Util.secondToMinute(endTime - addCycleBegin), multiParkingUnit);
                                } else if (addCycleEnd < endTime && beginTime > addCycleBegin){
                                    tmpTotalParkingMinutes += Util.secondToMinute(addCycleEnd - beginTime);
                                    tmpLeaveDueAmount += Util.multiParkingAmount(Util.secondToMinute(addCycleEnd - beginTime), multiParkingUnit);
                                }
                                // dueAmount - leave time money
                                dueAmount -= tmpLeaveDueAmount;
                                // 计算玩累计范围内的所有订单时间那么下面拿着时间进行费用计算
                                if (tmpTotalParkingMinutes > 0) {
                                    tmpTotalParkingAmount += Util.multiParkingAmount(tmpTotalParkingMinutes, multiParkingUnit);
                                }
                                dueAmount += Integer.min(tmpTotalParkingAmount, maxAmountForMultiParking.maxAmount) - tmpTotalDueAmount;
                            }
                        }
                    }
                    log.info("经过累计计算最终应收金额为: " + dueAmount);
                    totalAmount = chargeAmount = dueAmount;

                } else {
                    // 如果当前计费规则与车辆类型对应的默认计费规则一致,则走计费规则累计计算
                    if (getChargeTypeId(beginTime, periods).equals(carContext.getCarType().defaultChargeTypeId) && dueAmount > 0) {
                        List<ParkingOrder> parkingOrders = findByUserIdsAndEndTimeRange(carContext.getProjectNo(), multiMaxPolicy.userIds,
                                multiMaxPolicy.begin,
                                multiMaxPolicy.end);
                        if (!parkingOrders.isEmpty()) {
                            if (multiMaxPolicy.dayRange()) {
                                historyOrderTraceInfo = new HistoryOrderTraceInfo(multiMaxPolicy.begin, multiMaxPolicy.end,
                                        multiMaxPolicy.getMaxAmount(), Util.dayMinutes);
                                for (ParkingOrder po : parkingOrders) {
                                    historyOrderTraceInfo.addPayment(
                                            new HistoryPayment(po, multiMaxPolicy.begin, multiMaxPolicy.end));
                                }
                            } else {
                                if (multiMaxPolicy.durationPayRange()) {
                                    historyOrderTraceInfo = new HistoryOrderTraceInfo(multiMaxPolicy.begin, multiMaxPolicy.end,
                                            multiMaxPolicy.getMaxAmount(),
                                            multiMaxPolicy.getMinutes());
                                } else {
                                    historyOrderTraceInfo = new HistoryOrderTraceInfo(parkingOrders.get(0).beginTime,
                                            multiMaxPolicy.end, multiMaxPolicy.getMaxAmount(),
                                            multiMaxPolicy.getMinutes());
                                }

                                for (ParkingOrder po : parkingOrders) {
                                    historyOrderTraceInfo.addPayment(new HistoryPayment(po));
                                }
                            }
                            dueAmount = chargeAmount = Integer.min(chargeAmount, historyOrderTraceInfo.maxAvailableAmount());
                        }
                    }
                }
            }
        }
    }

    private void updateForExtra(CarContext carContext) {
        ExtraPolicy extraPolicy = carContext.extraPolicy();
        if (extraPolicy != null) {
            for (ChargeInfo chargeInfo : chargeInfos) {
                long begin = chargeInfo.beginTime();
                long end = chargeInfo.endTime();
                long point = Util.dayBeginTime(begin) + extraPolicy.time;
                if (Util.compareLongRangeClose(point, begin, end) == 0) {
                    chargeInfo.extraAmount += Util.lengthToUnit((int)(end - point + 1), Util.daySeconds) * extraPolicy.amount;
                }
                extraAmount += chargeInfo.extraAmount;
            }
            dueAmount += extraAmount;
        }
    }

    /**
     * TODO: 只对金额劵,折扣劵,全免劵进行 使用优惠劵更新金额
     */
    private void updateForDiscount() {
        if (dueAmount > 0 && discountInfo != null) {
            discountedAmount = discountInfo.discountedAmount(dueAmount);
            dueAmount -= discountedAmount;
        }
    }

    private boolean withinBestBefore(long time) {
        return bestBefore != null && time <= bestBefore;
    }

    @Override
    public String toString() {
        return "ParkingOrder{" + ", orderNo='" + orderNo + '\'' +
                ", payParkingId=" + payParkingId + ", userId=" + userId + ", tempType=" + tempType +
               ", carTypeClass=" + carTypeClass + ", carTypeName='" + carTypeName + '\'' + ", carTypeId=" + carTypeId +
               ", beginTime=" + beginTime + ", endTime=" + endTime + ", nextAggregateBeginTime=" +
               nextAggregateBeginTime + ", aggregatedMaxAmount=" + aggregatedMaxAmount + ", parkingMinutes=" +
               parkingMinutes + ", discountInfo=" + discountInfo + ", chargeInfos=" + chargeInfos + ", totalAmount=" +
               totalAmount  + ", discountedMinutes=" + discountedMinutes + ", discountedAmount=" + discountedAmount +
               ", historyOrderTraceInfo=" + historyOrderTraceInfo + ", chargeAmount=" + chargeAmount +
               ", extraAmount=" + extraAmount + ", dueAmount=" + dueAmount + ", chargeDueAmount=" + chargeDueAmount +
                ", paidAmount=" + paidAmount + ", payChannel='" + payChannel + '\'' + ", payType=" + payType +
                ", payTime=" + payTime + ", receivedAmount=" + receivedAmount + ", termNo='" +
               termNo + '\'' + ", operator='" + operator + '\'' + ", bestBefore=" + bestBefore + ", expireTime=" +
               expireTime + ", remark='" + remark + '\'' + ", comments='" + comments + '\'' + ", invoiceState=" +
               invoiceState + ", refundState=" + refundState + ", projectNo='" + projectNo + '\'' + ", creator='" +
               creator + '\'' + ", createTime=" + createTime + ", modifier='" + modifier + '\'' + ", modifyTime=" +
               modifyTime + '}';
    }
}