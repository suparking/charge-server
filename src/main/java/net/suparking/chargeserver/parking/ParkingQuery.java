package net.suparking.chargeserver.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingQuery {

    private String projectNo;

    private List<String> userIds;

    private long begin;

    private long end;
}
