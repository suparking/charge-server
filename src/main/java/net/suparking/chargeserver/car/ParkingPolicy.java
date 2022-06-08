package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.suparking.chargeserver.car.ShareLevel.PROJECT;
import static net.suparking.chargeserver.car.ShareLevel.SUBAREA;

public class ParkingPolicy extends FieldValidator {
    @ParamNotNull
    public ShareLevel shareLevel;
    public Integer spaceQuantity;
    public List<ParkingSpaceInfo> parkingSpaceInfos;

    private static final Logger log = LoggerFactory.getLogger(ParkingPolicy.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (PROJECT.equals(shareLevel)) {
            if (spaceQuantity == null) {
                log.error("spaceQuantity cannot be null");
                return false;
            }
        } else if (SUBAREA.equals(shareLevel)) {
            if (parkingSpaceInfos == null) {
                log.error("parkingSpaceInfos cannot be null");
                return false;
            } else {
                for (ParkingSpaceInfo psi: parkingSpaceInfos) {
                    if (!psi.validate()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int spaceQuantityInSubArea(ObjectId subAreaId) {
        if (PROJECT.equals(shareLevel)) {
            return spaceQuantity;
        } else {
            for (ParkingSpaceInfo psi : parkingSpaceInfos) {
                if (psi.subAreaId.equals(subAreaId)) {
                    return psi.spaceQuantity;
                }
            }
        }
        return 0;
    }

    public int totalSpaceQuantity() {
        if (PROJECT.equals(shareLevel)) {
            return spaceQuantity;
        } else {
            int qty = 0;
            for (ParkingSpaceInfo psi : parkingSpaceInfos) {
                qty += psi.spaceQuantity;
            }
            return qty;
        }
    }

    @Override
    public String toString() {
        return "ParkingPolicy{" + "shareLevel=" + shareLevel + ", spaceQuantity=" + spaceQuantity +
               ", parkingSpaceInfos=" + parkingSpaceInfos + '}';
    }
}
