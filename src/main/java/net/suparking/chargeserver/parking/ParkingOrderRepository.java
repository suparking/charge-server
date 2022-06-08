package net.suparking.chargeserver.parking;

import org.bson.types.ObjectId;

import java.util.List;

public interface ParkingOrderRepository {
    void save(ParkingOrder parkingOrder);
    ParkingOrder findHistoryByPayParkingId(String payParkingId);
    List<ParkingOrder> findByPlateNosAndEndTimeRange(List<String> userIds, long begin, long end);
    List<ParkingOrder> findByPlateNosAndBeginTimeOrEndTimeRange(List<String> userIds, long begin, long end);
    ParkingOrder findNextAggregateBeginTime(List<String> userIds);
}
