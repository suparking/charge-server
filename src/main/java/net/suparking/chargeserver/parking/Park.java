package net.suparking.chargeserver.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Park extends FieldValidator {

    public ObjectId id;

    @ParamNotNull
    public String parkName;

    @ParamNotNull
    public String parkNo;

    public String deviceNo;

    // 地锁强制升降
    public Boolean forceOpen = false;

    public String projectNo;

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        return true;
    }
}
