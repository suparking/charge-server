package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.util.Util;
import org.springframework.util.ObjectUtils;

import java.util.LinkedList;
import java.util.ListIterator;

public class ChargeFrameInfo {
    public CycleType cycleType = CycleType.ENTER;
    public int dayStartMinute = 0;
    public int cycleLength = 1440;
    public Integer cycleFreeMinutes = 0;
    public Boolean cycleFreeInCharge;
    public Integer cycleMaxAmount = 0;
    public Integer cycleMinAmount = 0;
    public long beginTime;
    public long endTime;
    public int totalCycleMinutes = 0;
    public LinkedList<ChargeFrame> chargeFrames = new LinkedList<>();
    public LinkedList<CycleFrame> cycleFrames = new LinkedList<>();
    public int balancedMinutes = 0;
    public int discountedMinutes = 0;

    public ChargeFrameInfo(long beginTime) {
        this.beginTime = beginTime;
    }

    public boolean validForMerge(ChargeType chargeType) {
        for (ChargeFrame cf: chargeFrames) {
            if (!cf.chargeType.validForMerge(chargeType)) {
                return false;
            }
        }
        return true;
    }

    public int updateCycleAmount(int amount) {
        if (cycleMinAmount > 0) {
            if (totalCycleMinutes > cycleFreeMinutes) {
                amount = Integer.max(amount, cycleMinAmount);
            }
        }
        if (cycleMaxAmount > 0) {
            amount = Integer.min(amount, cycleMaxAmount);
        }
        return amount;
    }

    public void updateCycleParam(ChargeType chargeType) {
        if (!chargeType.chargeFree()) {
            ChargeRule cr = chargeType.chargeRule;
            if (cr.cycleType != null) {
                cycleType = cr.cycleType;
            }
            if (cr.dayStartMinute != null) {
                dayStartMinute = cr.dayStartMinute;
            }
            if (isParkingType()) {
                cycleLength = Util.timeGapToMinute(beginTime, endTime);
            } else if (cr.cycleLength != null) {
                cycleLength = cr.cycleLength;
            }
            if (cr.cycleFreeMinutes != null && cycleFreeMinutes < cr.cycleFreeMinutes) {
                cycleFreeMinutes = cr.cycleFreeMinutes;
            }
            if (cr.cycleFreeInCharge != null) {
                if (cycleFreeInCharge == null) {
                    cycleFreeInCharge = cr.cycleFreeInCharge;
                } else {
                    cycleFreeInCharge = cycleFreeInCharge && cr.cycleFreeInCharge;
                }
            }
            if (cr.cycleMaxAmount != null && cycleMaxAmount < cr.cycleMaxAmount) {
                cycleMaxAmount = cr.cycleMaxAmount;
            }
            if (cr.cycleMinAmount != null && cycleMinAmount < cr.cycleMinAmount) {
                cycleMinAmount = cr.cycleMinAmount;
            }
        }
    }

    public boolean isParkingType() {
        return cycleType.equals(CycleType.PARKING);
    }

    public boolean isNaturalType() {
        return cycleType.equals(CycleType.NATURAL);
    }

    public void calculateChargeInfos(LinkedList<ChargeInfo> chargeInfos, TimeDeductInfo timeDeductInfo) {
        for (CycleFrame cycleFrame: cycleFrames) {
            int cfm = cycleFreeMinutes;
            ChargeInfo chargeInfo = new ChargeInfo(cycleFrame.beginCycleSeq);

            int usedDiscountPart = 0;
            int usedBalancePart = 0;

            if (cycleFrame.cycleNumber <= 1) {
                chargeInfo.cycleNumber = cycleFrame.cycleNumber;
                for (ChargeFrame chargeFrame: cycleFrame.chargeFrames) {
                    ChargeType chargeType = chargeFrame.chargeType;
                    int chargeMinutes = Util.timeGapToMinute(chargeFrame.beginTime, chargeFrame.endTime);

                    int minuteDiscount = timeDeductInfo.discountedMinutes;
                    int minuteBalance = timeDeductInfo.balancedMinutes;
                    int availableMinutes = minuteDiscount + minuteBalance;

                    if (chargeType.chargeFree()) {
                        ChargeDetail chargeDetail = new ChargeDetail(
                                chargeFrame.beginTime, chargeFrame.endTime,
                                0, 0, 0,
                                chargeType.chargeTypeName);
                        chargeInfo.chargeDetails.addLast(chargeDetail);
                    } else if (chargeMinutes <= availableMinutes) {
                        usedDiscountPart = Integer.min(minuteDiscount, chargeMinutes);
                        usedBalancePart = chargeMinutes - usedDiscountPart;
                        ChargeDetail chargeDetail = new ChargeDetail(
                                chargeFrame.beginTime, chargeFrame.endTime, usedBalancePart,
                                0, usedDiscountPart, chargeType.chargeTypeName);
                        chargeInfo.chargeDetails.addLast(chargeDetail);
                    } else {
                        usedDiscountPart = minuteDiscount;
                        usedBalancePart = minuteBalance;
                        chargeMinutes -= availableMinutes;
                        if (chargeMinutes <= cfm) {
                            ChargeDetail chargeDetail = new ChargeDetail(
                                    chargeFrame.beginTime, chargeFrame.endTime, usedBalancePart, chargeMinutes,
                                    usedDiscountPart, chargeType.chargeTypeName);
                            chargeInfo.chargeDetails.addLast(chargeDetail);
                            cfm -= chargeMinutes;
                        } else {
                            ChargeDetail chargeDetail = chargeDetailWithCycleFreeMinutesExceeded(
                                    cfm, chargeType,
                                    chargeFrame.beginTime, chargeFrame.endTime,
                                    usedBalancePart, usedDiscountPart);
                            chargeInfo.chargeDetails.addLast(chargeDetail);
                            chargeInfo.totalAmount += chargeDetail.chargeAmount;
                            cfm = 0;
                        }
                    }

                    timeDeductInfo.discountedMinutes -= usedDiscountPart;
                    timeDeductInfo.balancedMinutes -= usedBalancePart;
                }
                chargeInfo.totalAmount = updateCycleAmount(chargeInfo.totalAmount);
            } else {
                chargeInfo.cycleNumber = cycleFrame.cycleNumber;
                ChargeFrame chargeFrame = cycleFrame.chargeFrames.getFirst();
                ChargeType chargeType = chargeFrame.chargeType;
                int chargeMinutes = Util.timeGapToMinute(chargeFrame.beginTime, chargeFrame.endTime);

                int minuteDiscount = timeDeductInfo.discountedMinutes;
                int minuteBalance = timeDeductInfo.balancedMinutes;
                int availableMinutes = minuteBalance + minuteDiscount;

                if (chargeType.chargeFree()) {
                    ChargeDetail chargeDetail = new ChargeDetail(
                            chargeFrame.beginTime, chargeFrame.endTime,
                            0, 0, 0,
                            chargeType.chargeTypeName);
                    chargeInfo.chargeDetails.addLast(chargeDetail);
                } else if (chargeMinutes <= availableMinutes) {
                    usedDiscountPart = Integer.min(minuteDiscount, chargeMinutes);
                    usedBalancePart = chargeMinutes - usedDiscountPart;
                    ChargeDetail chargeDetail = new ChargeDetail(
                            chargeFrame.beginTime, chargeFrame.endTime, usedBalancePart,
                            0, usedDiscountPart, chargeType.chargeTypeName);
                    chargeInfo.chargeDetails.addLast(chargeDetail);
                } else {
                    if (availableMinutes > 0) {
                        // 当可用优惠时长 > 0 ,按照周期计算进行减免
                        int totalReducedCycleNumber = availableMinutes / cycleLength;
                        int totalReducedMinute = totalReducedCycleNumber * cycleLength;
                        if (totalReducedCycleNumber > 0) {
                            long beginEpoch = cycleFrame.beginTime;
                            // 计算几个周期之后的结束时间
                            long endEpoch = beginEpoch + (long) totalReducedCycleNumber * cycleLength * Util.minuteSeconds - 1;
                            usedDiscountPart = Integer.min(totalReducedMinute, minuteDiscount);
                            usedBalancePart = totalReducedMinute - usedDiscountPart;

                            minuteDiscount -= usedDiscountPart;
                            minuteBalance -= usedBalancePart;
                            // 这是在一个周期内进行的计算可用分钟
                            availableMinutes = minuteDiscount + minuteBalance;

                            ChargeDetail chargeDetail = new ChargeDetail(
                                    beginEpoch, endEpoch, usedBalancePart, 0,
                                    usedDiscountPart, chargeType.chargeTypeName);
                            chargeInfo.chargeDetails.addLast(chargeDetail);
                        }

                        usedDiscountPart = usedBalancePart = 0;
                        // 如果计算出减免时 < 一个周期时长
                        long halfBeginEpoch = cycleFrame.beginTime + (long) totalReducedCycleNumber * cycleLength * Util.minuteSeconds;
                        long halfEndEpoch = halfBeginEpoch + (long) cycleLength * Util.minuteSeconds - 1;
                        usedDiscountPart += minuteDiscount;
                        usedBalancePart += minuteBalance;
                        int chargingMinutes = cycleLength - availableMinutes;
                        if (chargingMinutes <= cfm) {
                            ChargeDetail chargeDetail = new ChargeDetail(
                                    halfBeginEpoch, halfEndEpoch, usedBalancePart,
                                    chargingMinutes, minuteDiscount, chargeType.chargeTypeName);
                            chargeInfo.chargeDetails.addLast(chargeDetail);
                        } else {
                            ChargeDetail chargeDetail = chargeDetailWithCycleFreeMinutesExceeded(
                                    cfm, chargeType, halfBeginEpoch,
                                    halfEndEpoch, usedBalancePart, minuteDiscount);
                            chargeInfo.chargeDetails.add(chargeDetail);
                            chargeDetail.chargeAmount = updateCycleAmount(chargeDetail.chargeAmount);
                            chargeInfo.totalAmount += chargeDetail.chargeAmount;
                        }

                        int totalLeftCycleNumber = cycleFrame.cycleNumber - totalReducedCycleNumber - 1;
                        if (totalLeftCycleNumber > 0) {
                            long beginEpoch = halfEndEpoch + 1;

                            ChargeDetail chargeDetail = chargeMultiCycle(cfm, chargeType, beginEpoch,
                                                                         cycleFrame.endTime, totalLeftCycleNumber);
                            chargeInfo.chargeDetails.addLast(chargeDetail);
                            chargeInfo.totalAmount += chargeDetail.chargeAmount;
                        }
                    } else {
                        ChargeDetail chargeDetail = chargeMultiCycle(cfm, chargeType, cycleFrame.beginTime,
                                                                     cycleFrame.endTime, cycleFrame.cycleNumber);
                        chargeInfo.chargeDetails.addLast(chargeDetail);
                        chargeInfo.totalAmount += chargeDetail.chargeAmount;
                    }
                }
                timeDeductInfo.discountedMinutes -= usedDiscountPart;
                timeDeductInfo.balancedMinutes -= usedBalancePart;
            }

            chargeInfos.addLast(chargeInfo);
            for (ChargeDetail chargeDetail: chargeInfo.chargeDetails) {
                chargeInfo.parkingMinutes += chargeDetail.parkingMinutes;
                chargeInfo.balancedMinutes += chargeDetail.balancedMinutes;
                chargeInfo.discountedMinutes += chargeDetail.discountedMinutes;
            }
        }
    }

    private ChargeDetail chargeDetailWithCycleFreeMinutesExceeded(int cycleFreeMinutes,
                                                                  ChargeType chargeType,
                                                                  long begin, long end,
                                                                  int balancedMinutes,
                                                                  int discountedMinutes) {
        int freedMinutes = (cycleFreeInCharge != null && cycleFreeInCharge) ? 0 : cycleFreeMinutes;
        return generateChargeDetail(chargeType, begin, end, balancedMinutes, freedMinutes, discountedMinutes);
    }

    private ChargeDetail chargeMultiCycle(int cycleFreeMinutes, ChargeType chargeType,
                                          long begin, long end, int cycleNumber) {
        long singleCycleEnd = begin + cycleLength * Util.minuteSeconds - 1;
        ChargeDetail chargeDetail;

        if (cycleFreeMinutes < cycleLength) {
            chargeDetail = chargeDetailWithCycleFreeMinutesExceeded(cycleFreeMinutes, chargeType, begin, singleCycleEnd,
                                                                    0, 0);
            chargeDetail.endTime = end;
            chargeDetail.parkingMinutes *= cycleNumber;
            chargeDetail.chargingMinutes *= cycleNumber;
            chargeDetail.freedMinutes *= cycleNumber;
            chargeDetail.chargeAmount = updateCycleAmount(chargeDetail.chargeAmount);
            chargeDetail.chargeAmount *= cycleNumber;
        } else {
            chargeDetail = new ChargeDetail(begin, end, 0, cycleNumber*cycleLength,
                                            0, chargeType.chargeTypeName);
        }
        return chargeDetail;
    }

    private ChargeDetail generateChargeDetail(ChargeType chargeType, long beginTime, long endTime,
                                              int balancedMinutes, int freedMinutes, int discountedMinutes) {
        int reducedMinutes = freedMinutes + balancedMinutes + discountedMinutes;
        ChargeDetail chargeDetail = new ChargeDetail(beginTime, endTime, balancedMinutes, freedMinutes,
                                                     discountedMinutes, chargeType.chargeTypeName);
        chargeDetail.chargeAmount = 0;
        ChargeRule chargeRule = chargeType.chargeRule;
        RuleType ruleType = chargeRule.ruleType;
        if (ruleType.equals(RuleType.HOUR)) {
            int amountPerHour = chargeRule.amountPerHour;
            chargeDetail.chargeAmount += Util.minuteToHour(chargeDetail.chargingMinutes, chargeRule.minHourLength) * amountPerHour;
        } else if (ruleType.equals(RuleType.TIME)) {
            int amountPerTime = chargeRule.amountPerTime;
            chargeDetail.chargeAmount += amountPerTime;
        } else {
            ComplicatedRule complicatedRule = chargeRule.complicatedRule;
            RangeRule beginRR = complicatedRule.rangeRules.getFirst();
            if (complicatedRule.rangeType.equals(RangeType.ENTER)) {
                if (complicatedRule.rangeRules.size() > 1) {
                    rangeCharge(chargeDetail,
                                complicatedRule.rangeRules,
                                0,
                                chargeDetail.chargingMinutes);
                } else {
                    rangeAmount(beginRR.rangeRuleInfo, chargeDetail.chargingMinutes, chargeDetail);
                }
            } else {
                long begin = beginTime + (long) reducedMinutes * Util.minuteSeconds;
                long end = endTime;
                ListIterator<RangeRule> it = complicatedRule.rangeRules.listIterator();
                while (begin < end) {
                    RangeRule rule = it.hasNext() ? it.next() : (it = complicatedRule.rangeRules.listIterator()).next();
                    int minOffset = Util.minuteOffsetInDay(begin);
                    int rangeMin = rule.rangeMinutes(minOffset); // 720 min
                    if (rangeMin > 0) {
                        int chargeMin = Util.secondToMinute(end - begin);
                        if (chargeMin <= rangeMin) {
                            chargeDetail.chargeAmount += rangeAmount(rule.rangeRuleInfo, chargeMin);
                            break;
                        }
                        chargeDetail.chargeAmount += rangeAmount(rule.rangeRuleInfo, rangeMin);
                        begin += (long) Util.minuteSeconds * rangeMin;
                    }
                }

                //TODO

//                if (complicatedRule.rangeRules.size() > 1) {
//                    long beginEpoch = beginTime + reducedMinutes * Util.minuteSeconds;
//                    while (beginEpoch < endTime) {
//                        long newBeginEpoch = beginEpoch;
//                        long newEndEpoch = Util.dayEndTime(newBeginEpoch);
//                        if (endTime < newEndEpoch) {
//                            newEndEpoch = endTime;
//                        }
//                        rangeCharge(chargeDetail,
//                                    complicatedRule.rangeRules,
//                                    Util.minuteOffsetInDay(newBeginEpoch),
//                                    Util.minuteOffsetInDay(newEndEpoch));
//                        beginEpoch = Util.nextDayBeginTime(beginEpoch);
//                    }
//                } else {
//                    rangeAmount(beginRR.rangeRuleInfo, chargeDetail.chargingMinutes, chargeDetail);
//                }
            }
        }
        return chargeDetail;
    }

    private void rangeCharge(ChargeDetail chargeDetail, LinkedList<RangeRule> rangeRules, int beginMinute, int endMinute) {
        int lastBeginMinute = beginMinute;
        for (RangeRule rr: rangeRules) {
            int cmp = Util.compareIntRangeOpen(rr.end, lastBeginMinute, endMinute);
            if (cmp > 0) {
                int m = endMinute - lastBeginMinute;
                rangeAmount(rr.rangeRuleInfo, m, chargeDetail);
                break;
            } else if (cmp == 0) {
                int m = rr.end - lastBeginMinute;
                rangeAmount(rr.rangeRuleInfo, m, chargeDetail);
                lastBeginMinute = rr.end;
            }
        }
    }

    private void rangeAmount(RangeRuleInfo rri, int minutes, ChargeDetail chargeDetail) {
        int rangeFreedMinutes = 0;
        if (rri.rangeFreeMinutes != null) {
            if (rri.rangeFreeMinutes >= minutes) {
                rangeFreedMinutes = minutes;
            } else {
                rangeFreedMinutes = rri.rangeFreeInCharge ? 0 : rri.rangeFreeMinutes;
            }
        }
        chargeDetail.freedMinutes += rangeFreedMinutes;
        minutes -= rangeFreedMinutes;

        if (minutes > 0) {
            if (rri.rangeRuleType.equals(RangeRuleType.UNIT)) {
                int units = Util.lengthToUnit(minutes, rri.unitLength, rri.minUnitLength);
                int amount = units * rri.amountPerUnit;
                if (amount > 0) {
                    if (rri.rangeMinAmount != null) {
                        amount = Integer.max(amount, rri.rangeMinAmount);
                    }
                    if (rri.rangeMaxAmount != null) {
                        amount = Integer.min(amount, rri.rangeMaxAmount);
                    }
                }
                chargeDetail.chargeAmount += amount;
            } else {
                chargeDetail.chargeAmount += rri.fixedAmount;
            }
        }
    }

    private int rangeAmount(RangeRuleInfo rri, int minutes) {
        int rangeFreedMinutes = 0;
        if (rri.rangeFreeMinutes != null) {
            if (rri.rangeFreeMinutes >= minutes) {
                rangeFreedMinutes = minutes;
            } else {
                rangeFreedMinutes = rri.rangeFreeInCharge ? 0 : rri.rangeFreeMinutes;
            }
        }
        minutes -= rangeFreedMinutes;

        if (minutes > 0) {
            if (rri.rangeRuleType.equals(RangeRuleType.UNIT)) {
                int amount = 0;
                if (!ObjectUtils.isEmpty(rri.amountPerUnits)) {
                    for (AmountPerUnits amountPerUnits : rri.amountPerUnits) {
                        if (minutes >= amountPerUnits.getTotalUnitLength()) {
                            amount += amountPerUnits.getTotalUnitLength() / amountPerUnits.getUnitLength() * amountPerUnits.getAmountPerUnit();
                            minutes -= amountPerUnits.getTotalUnitLength();
                        } else {
                            if (minutes > rri.minUnitLength) {
                                amount += (minutes / amountPerUnits.getUnitLength() + ((minutes % amountPerUnits.getUnitLength() > rri.minUnitLength) ? 1 : 0)) * amountPerUnits.getAmountPerUnit();
                            }
                            break;
                        }
                    }
                } else {
                    int units = Util.lengthToUnit(minutes, rri.unitLength, rri.minUnitLength);
                    amount = units * rri.amountPerUnit;
                    if (amount > 0) {
                        if (rri.rangeMinAmount != null) {
                            amount = Integer.max(amount, rri.rangeMinAmount);
                        }
                        if (rri.rangeMaxAmount != null) {
                            amount = Integer.min(amount, rri.rangeMaxAmount);
                        }
                    }
                }
                return amount;
            } else {
                return rri.fixedAmount;
            }
        }
        return 0;
    }

    public int buildCycle(int beginSeq) {
        if (isNaturalType()) {
            return buildNaturalCycle(beginSeq);
        } else if (isParkingType()) {
            return buildSingleCycle(beginSeq);
        } else {
            return buildEnterCycle(beginSeq);
        }
    }

    private int buildNaturalCycle(int beginSeq) {
        beginTime = Util.shapeDayBegin(beginTime, dayStartMinute);
        CycleFrame cycleFrame = new CycleFrame(beginSeq, beginTime);
        long cycleEnd = Util.dayEndTime(beginTime, dayStartMinute);
        for (int idx = 0; idx < chargeFrames.size();) {
            ChargeFrame chargeFrame = chargeFrames.get(idx);
            if (chargeFrame.endTime < chargeFrame.beginTime) {
                ++idx;
            } else if (chargeFrame.endTime <= cycleEnd) {
                cycleFrame.addChargeFrame(chargeFrame);

                if (cycleFrame.endTime == cycleEnd) {
                    cycleFrames.addLast(cycleFrame);
                    cycleFrame = new CycleFrame(++beginSeq, cycleEnd + 1);
                    cycleEnd += Util.daySeconds;
                }
                ++idx;
            } else if (cycleFrame.isFrameEmpty() && Util.isDayBegin(chargeFrame.beginTime, dayStartMinute)) {
                cycleFrame.cycleNumber = chargeFrame.calculateChargeMinute() / cycleLength;
                cycleEnd += (long) (cycleFrame.cycleNumber - 1) * Util.daySeconds;
                ChargeFrame newChargeFrame = new ChargeFrame(chargeFrame);
                newChargeFrame.endTime = cycleEnd;
                cycleFrame.addChargeFrame(newChargeFrame);

                chargeFrame.beginTime = cycleEnd + 1;
                cycleEnd += Util.daySeconds;
                cycleFrames.addLast(cycleFrame);
                beginSeq += cycleFrame.cycleNumber;
                cycleFrame = new CycleFrame(beginSeq, chargeFrame.beginTime);
            } else {
                ChargeFrame newChargeFrame = new ChargeFrame(chargeFrame);
                newChargeFrame.endTime = cycleEnd;
                cycleFrame.addChargeFrame(newChargeFrame);

                chargeFrame.beginTime = cycleEnd + 1;
                cycleEnd += Util.daySeconds;
                cycleFrames.addLast(cycleFrame);
                cycleFrame = new CycleFrame(++beginSeq, chargeFrame.beginTime);
            }
        }
        if (!cycleFrame.isFrameEmpty()) {
            cycleFrames.addLast(cycleFrame);
            beginSeq += cycleFrame.cycleNumber;
        }
        return beginSeq;
    }

    private int buildEnterCycle(int beginSeq) {
        CycleFrame cycleFrame = new CycleFrame(beginSeq, beginTime);
        long cycleEnd = beginTime + cycleLength * Util.minuteSeconds - 1;
        for (int idx = 0; idx < chargeFrames.size();) {
            ChargeFrame chargeFrame = chargeFrames.get(idx);
            if (chargeFrame.endTime < chargeFrame.beginTime) {
                ++idx;
            } else if (chargeFrame.endTime <= cycleEnd) {
                cycleFrame.addChargeFrame(chargeFrame);

                if (cycleFrame.endTime == cycleEnd) {
                    cycleFrames.addLast(cycleFrame);
                    cycleFrame = new CycleFrame(++beginSeq, cycleEnd + 1);
                    cycleEnd += cycleLength * Util.minuteSeconds;
                }
                ++idx;
            } else if (cycleFrame.isFrameEmpty()) {
                cycleFrame.cycleNumber = chargeFrame.calculateChargeMinute() / cycleLength;
                cycleEnd += (cycleFrame.cycleNumber - 1) * cycleLength * Util.minuteSeconds;
                ChargeFrame newChargeFrame = new ChargeFrame(chargeFrame);
                newChargeFrame.endTime = cycleEnd;
                cycleFrame.addChargeFrame(newChargeFrame);

                chargeFrame.beginTime = cycleEnd + 1;
                cycleEnd += cycleLength * Util.minuteSeconds;
                cycleFrames.addLast(cycleFrame);
                beginSeq += cycleFrame.cycleNumber;
                cycleFrame = new CycleFrame(beginSeq, chargeFrame.beginTime);
            } else {
                ChargeFrame newChargeFrame = new ChargeFrame(chargeFrame);
                newChargeFrame.endTime = cycleEnd;
                cycleFrame.addChargeFrame(newChargeFrame);

                chargeFrame.beginTime = cycleEnd + 1;
                cycleEnd += cycleLength * Util.minuteSeconds;
                cycleFrames.addLast(cycleFrame);
                cycleFrame = new CycleFrame(++beginSeq, chargeFrame.beginTime);
            }
        }
        if (!cycleFrame.isFrameEmpty()) {
            cycleFrames.addLast(cycleFrame);
            beginSeq += cycleFrame.cycleNumber;
        }
        return beginSeq;
    }

    private int buildSingleCycle(int beginSeq) {
        CycleFrame cycleFrame = new CycleFrame(beginSeq, beginTime, endTime);
        for (ChargeFrame chargeFrame: chargeFrames) {
            cycleFrame.addChargeFrame(chargeFrame);
        }
        return ++beginSeq;
    }

    @Override
    public String toString() {
        return "ChargeFrameInfo{" + "cycleType=" + cycleType + ", dayStartMinute=" + dayStartMinute + ", cycleLength=" +
               cycleLength + ", cycleFreeMinutes=" + cycleFreeMinutes + ", cycleFreeInCharge=" + cycleFreeInCharge +
               ", cycleMaxAmount=" + cycleMaxAmount + ", cycleMinAmount=" + cycleMinAmount + ", begin=" +
               beginTime + ", end=" + endTime + ", totalCycleMinutes=" + totalCycleMinutes + ", chargeFrames=" +
               chargeFrames + ", cycleFrames=" + cycleFrames + ", balancedMinutes=" + balancedMinutes +
               ", discountedMinutes=" + discountedMinutes + '}';
    }
}
