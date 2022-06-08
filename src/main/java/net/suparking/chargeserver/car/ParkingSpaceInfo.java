package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;

@ParamNotNull
public class ParkingSpaceInfo extends FieldValidator {
    public ObjectId subAreaId;
    public Integer spaceQuantity;

    @Override
    public String toString() {
        return "ParkingSpaceInfo{" + "subAreaId='" + subAreaId + '\'' + ", spaceQuantity=" + spaceQuantity + '}';
    }
}
