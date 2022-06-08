package net.suparking.chargeserver.mq.cloud.consumber;

import net.suparking.chargeserver.cmd.ParkingOrderQueryCommand;
import net.suparking.chargeserver.cmd.ParkingOrderQueryIn;
import net.suparking.chargeserver.cmd.ParkingOrderQueryOut;
import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.mq.BasicMQMessage;
import net.suparking.chargeserver.mq.BasicMQMessageRet;
import net.suparking.chargeserver.mq.cloud.CloudConsumer;
import net.suparking.chargeserver.parking.Parking;
import net.suparking.chargeserver.parking.ParkingOrder;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("CLOUD_MQ_PARKING_ORDER_QUERY")
public class ParkingOrderQueryConsumer extends CloudConsumer {

    private static class ParkingOrderQuery extends BasicMQMessage {

        // 用户驶入信息
        public Parking parking;

        public DiscountInfo discountInfo;

        public ObjectId tempCarTypeId;
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
        in.parking = parkingOrderQuery.parking;
        in.discountInfo = parkingOrderQuery.discountInfo;
        in.tempCarTypeId = parkingOrderQuery.tempCarTypeId;
        ParkingOrderQueryOut out = cmd.execute(in);

        ParkingOrderQueryRetMQ ret = new ParkingOrderQueryRetMQ(out.code);
        ret.parking = in.parking;
        ret.parkingOrder = out.parkingOrder;
        return mapper.writeValueAsString(ret);
    }
}
