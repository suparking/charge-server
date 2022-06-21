package net.suparking.chargeserver.service;

import com.alibaba.fastjson.JSONArray;
import net.suparking.chargeserver.util.SpkCommonResult;

public interface SyncService {
    SpkCommonResult syncConfig(String type, JSONArray projectNoList);
}
