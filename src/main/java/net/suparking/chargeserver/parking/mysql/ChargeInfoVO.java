package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeInfoVO extends ChargeInfoDO {

    private static final long serialVersionUID = -1834086592109022344L;

    private LinkedList<ChargeDetailDO> chargeDetailDOList;

}
