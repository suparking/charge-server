package net.suparking.chargeserver.car;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recog {

    private String id;
    private ObjectId parkId;
    private String parkNo;
    private String parkName;
    private String deviceNo;
    private String userId;
    private Long recogTime;

    private String projectNo;
}
