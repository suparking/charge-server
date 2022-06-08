package net.suparking.chargeserver.charge;

import java.util.LinkedList;

class CycleFrame {
    public int beginCycleSeq;
    public int cycleNumber;
    public long beginTime;
    public long endTime;
    public int cycleChargeMinutes;
    public LinkedList<ChargeFrame> chargeFrames = new LinkedList<>();

    public CycleFrame() {}

    public CycleFrame(int beginSeq, long beginTime) {
        this.beginCycleSeq = beginSeq;
        this.cycleNumber = 1;
        this.beginTime = beginTime;
        this.cycleChargeMinutes = 0;
    }

    public CycleFrame(int beginSeq, long beginTime, long endTime) {
        this(beginSeq, beginTime);
        this.endTime = endTime;
    }

    public void addChargeFrame(ChargeFrame chargeFrame) {
        this.chargeFrames.addLast(chargeFrame);
        this.endTime = chargeFrame.endTime;
        this.cycleChargeMinutes += chargeFrame.calculateChargeMinute();
    }

    public boolean isFrameEmpty() {
        return chargeFrames.isEmpty();
    }

    @Override
    public String toString() {
        return "CycleFrame{" + "beginCycleSeq=" + beginCycleSeq + ", cycleNumber=" + cycleNumber + ", begin=" +
               beginTime + ", end=" + endTime + ", cycleChargeMinutes=" + cycleChargeMinutes + ", chargeFrames=" +
               chargeFrames + '}';
    }
}
