package net.suparking.chargeserver.parking;


import static net.suparking.chargeserver.parking.ParkingOrder.serverTermNo;

public class PayInfo {
    private String payChannel;
    private String termNo;
    private Integer payAmount;
    private PayType payType;
    private String operator;
    private String remark;

    private static final String defaultPayChannel = "OFFLINE";

    public PayInfo(String remark) {
        this.payChannel = defaultPayChannel;
        this.termNo = serverTermNo;
        this.payAmount = 0;
        this.payType = PayType.CASH;
        this.operator = "system";
        this.remark = remark;
    }

    public PayInfo(String payChannel, String termNo, Integer payAmount, PayType payType, String operator, String remark) {
        this.payChannel = payChannel;
        this.termNo = termNo;
        this.payAmount = payAmount != null ? payAmount : 0;
        this.payType = payType;
        this.operator = operator;
        this.remark = remark;
    }

    public String getTermNo() {
        return termNo;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public PayType getPayType() {
        return payType;
    }

    public Integer getPayAmount() {
        return payAmount;
    }

    public String getRemark() {
        return remark;
    }

    public String getOperator() {
        return operator;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public void setPayAmount(Integer payAmount) {
        this.payAmount = payAmount;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "PayInfo{" + "payChannel='" + payChannel + '\'' + ", termNo='" + termNo + '\'' + ", payAmount=" +
               payAmount + ", payType=" + payType + ", operator='" + operator + '\'' + ", remark='" + remark + '\'' +
               '}';
    }
}
