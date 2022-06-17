package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * TODO: 6 ---> 计费规则Handler
 */
public class ChargeHandler {
    public static LinkedList<ChargeInfo> genChargeInfo(List<ChargeFrameInfo> chargeFrameInfos, int chargeMinutes,
                                                       int discountedMinutes,
                                                       int balancedMinutes) {
        // TODO: -> 计算parking charge time
        // int parkingMinutes = Util.timeGapToMinute(periods.getFirst().beginTime, periods.getLast().endTime);
        int effectiveDiscountMinutes = 0;
        int effectiveBalanceMinutes = 0;
        /**
         * TODO: 如果优惠时长 > 停车时长,那么就直接减免 如果相反,检查时长账户是否大于 停车时长 - 优惠时长
         */
        if (discountedMinutes >= chargeMinutes) {
            effectiveDiscountMinutes = getEffectiveDiscountMinutes(chargeFrameInfos, effectiveDiscountMinutes);
        } else {
            /** TODO: 如果走时长账户,那么就先比对停车时长 - 优惠时长 , 如果时长大于 以上结果, 那么最后就用时长账户 - 优惠时长 */
            effectiveDiscountMinutes = discountedMinutes;
            if (balancedMinutes >= chargeMinutes - discountedMinutes) {
                effectiveBalanceMinutes = getEffectiveDiscountMinutes(chargeFrameInfos, effectiveBalanceMinutes);
                effectiveBalanceMinutes -= effectiveDiscountMinutes;
            } else {
                effectiveBalanceMinutes = balancedMinutes;
            }
        }

        TimeDeductInfo deductInfo = new TimeDeductInfo(effectiveDiscountMinutes, effectiveBalanceMinutes);

        LinkedList<ChargeInfo> chargeInfos = new LinkedList<>();
        for (ChargeFrameInfo cfi: chargeFrameInfos) {
            cfi.calculateChargeInfos(chargeInfos, deductInfo);
        }

        return chargeInfos;
    }

    private static int getEffectiveDiscountMinutes(List<ChargeFrameInfo> chargeFrameInfos, int effectiveDiscountMinutes) {
        for (ChargeFrameInfo cfi: chargeFrameInfos) {
            for (CycleFrame cycleFrame : cfi.cycleFrames) {
                for (ChargeFrame chargeFrame : cycleFrame.chargeFrames) {
                    if (!chargeFrame.chargeType.chargeRule.ruleType.equals(RuleType.FREE)) {
                        effectiveDiscountMinutes += cfi.totalCycleMinutes;
                    }
                }
            }
        }
        return effectiveDiscountMinutes;
    }

    /**
     * 计算有效计费时间
     * @param chargeFrameInfos
     * @return
     */
    public static int chargeFrameInfoToGapTime(final List<ChargeFrameInfo> chargeFrameInfos) {
        int chargeTimeMinutes = 0;
        for (ChargeFrameInfo chargeFrameInfo : chargeFrameInfos) {
            for (CycleFrame cycleFrame : chargeFrameInfo.cycleFrames) {
                for (ChargeFrame chargeFrame : cycleFrame.chargeFrames) {
                    if (!chargeFrame.chargeType.chargeRule.ruleType.equals(RuleType.FREE)) {
                        chargeTimeMinutes += Util.timeGapToMinute(chargeFrame.endTime, chargeFrame.beginTime);
                    }
                }
            }
        }
        return chargeTimeMinutes;
    }
    /**
     * TODO: 构造计费Frame,validForMerge -> 有效合并
     * @param periods
     * @return
     */
    public static LinkedList<ChargeFrameInfo> buildChargeFrameInfo(String projectNo, List<ChargePeriod> periods) {
        LinkedList<ChargeFrameInfo> chargeFrameInfos = new LinkedList<>();
        ChargeFrameInfo chargeFrameInfo = null;
        ChargeFrame lastChargeFrame = null;
        for (ChargePeriod period: periods) {
            ChargeType chargeType = ChargeType.findById(projectNo, period.chargeTypeId);
            if (lastChargeFrame == null) {
                chargeFrameInfo = new ChargeFrameInfo(period.beginTime);
                chargeFrameInfos.addLast(chargeFrameInfo);
            } else if (!chargeFrameInfo.validForMerge(chargeType)) {
                chargeFrameInfo = new ChargeFrameInfo(period.beginTime);
                chargeFrameInfos.addLast(chargeFrameInfo);
            } else if (lastChargeFrame.chargeType.id.equals(period.chargeTypeId)) {
                lastChargeFrame.endTime = period.endTime;
                chargeFrameInfo.endTime = period.endTime;
                continue;
            }
            lastChargeFrame = new ChargeFrame(period.beginTime, period.endTime, chargeType);
            chargeFrameInfo.chargeFrames.addLast(lastChargeFrame);
            chargeFrameInfo.endTime = period.endTime;
            chargeFrameInfo.updateCycleParam(chargeType);
        }

        int beginSeq = 1;
        for (ChargeFrameInfo cfi: chargeFrameInfos) {
            beginSeq = cfi.buildCycle(beginSeq);
            for (CycleFrame cycleFrame: cfi.cycleFrames) {
                cfi.totalCycleMinutes += cycleFrame.cycleChargeMinutes;
            }
        }

        return chargeFrameInfos;
    }
}
