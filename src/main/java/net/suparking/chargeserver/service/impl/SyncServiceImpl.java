package net.suparking.chargeserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.car.CarTypeRepository;
import net.suparking.chargeserver.car.ChargeCalenderDateRepository;
import net.suparking.chargeserver.car.ChargeTypeRepository;
import net.suparking.chargeserver.service.SyncService;
import net.suparking.chargeserver.util.SpkCommonResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service("SyncServiceImpl")
public class SyncServiceImpl implements SyncService {

    private static final CarTypeRepository carTypeRepository = ChargeServerApplication.getBean(
            "CarTypeRepositoryImpl", CarTypeRepository.class);

    private static ChargeTypeRepository chargeTypeRepository = ChargeServerApplication.getBean(
            "ChargeTypeRepositoryImpl", ChargeTypeRepository.class);

    private static ChargeCalenderDateRepository chargeCalenderDateRepository = ChargeServerApplication.getBean(
            "ChargeCalenderDateRepositoryImpl", ChargeCalenderDateRepository.class);
    @Override
    public SpkCommonResult syncConfig(String type, JSONArray projectNoList) {
        log.info("接收到基础数据更新事件 => " + type + " 数据为: " + projectNoList);
        try {
            if (type.equals("carType")) {
                projectNoList.forEach(item -> {
                    carTypeRepository.reloadByProjectNo((String) item);
                });
            } else if(type.equals("chargeType")) {
                projectNoList.forEach(item -> {
                    chargeTypeRepository.reloadByProjectNo((String) item);
                });
            } else if(type.equals("dateType")) {
                projectNoList.forEach(item -> {
                    chargeCalenderDateRepository.reloadByProjectNo((String) item);
                });
            } else if(type.equals("all"))  {
                projectNoList.forEach(item -> {
                    chargeCalenderDateRepository.reloadByProjectNo((String) item);
                });

                projectNoList.forEach(item -> {
                    chargeTypeRepository.reloadByProjectNo((String) item);
                });

                projectNoList.forEach(item -> {
                    carTypeRepository.reloadByProjectNo((String) item);
                });
            }
        } catch (Exception ex) {
            return SpkCommonResult.error(type + " 同步失败 => " + ex);
        }

        return SpkCommonResult.success(type + " 同步成功 =>" + projectNoList);
    }
}
