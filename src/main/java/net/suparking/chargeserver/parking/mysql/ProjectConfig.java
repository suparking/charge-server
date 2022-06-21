package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectConfig {

    // 费用查询订单超时时间
    private Integer txTTL;

    // 车位重复上报最小时间
    private Integer minIntervalForDupPark;

    // 付费免费时间
    private Integer prepayFreeMinutes;

    // 绑定劵使用免费时间段
    // TODO add bind discount free time
    private Integer bindDiscountFreeMinutes;

}
