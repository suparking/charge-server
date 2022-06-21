package net.suparking.chargeserver.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.suparking.chargeserver.service.SyncService;
import net.suparking.chargeserver.util.SpkCommonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("config")
public class SyncController {

    private final SyncService syncService;

    public SyncController(final SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("sync")
    public SpkCommonResult syncConfig(@RequestBody JSONObject params) {
        return Optional.ofNullable(params).map(item -> syncService.syncConfig(item.getString("type"), item.getJSONArray("projectNoList"))).orElseGet(() -> SpkCommonResult.error("error params."));
    }
}
