package net.suparking.chargeserver.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * get request.
     * @param url url.
     * @param param params.
     * @return {@link JSONObject}
     */
    public static JSONObject sendGet(final String url, final Map<String, Object> param) {
        String lastUrl = url;
        if (Objects.nonNull(param)) {
            List<Map.Entry<String, Object>> params = new ArrayList<>(param.entrySet());
            StringBuilder tempParam = new StringBuilder();
            for (Map.Entry<String, Object> p : params) {
                String key = p.getKey();
                Object value = p.getValue();
                tempParam.append(key).append("=").append(value).append("&");
            }
            String result = "";
            result = tempParam.toString();
            result = result.substring(0, result.length() - 1);
            lastUrl = lastUrl + "?" + result;
        }

        try {
            HttpResponse httpResponse = HttpRequest.get(lastUrl).timeout(15000).execute();
            if (httpResponse != null && httpResponse.isOk()) {
                String resultBody = httpResponse.body();
                return JSON.parseObject(resultBody);
            }
        } catch (Exception ex) {
            Arrays.stream(ex.getStackTrace()).forEach(item -> LOG.error(item.toString()));
        }
        return null;
    }

    /**
     * httpClient post.
     *
     * @param url request url
     * @param param request body
     * @return {@link JSONObject}
     */
    public static JSONObject sendPost(final String url, final String param) {
        try {
            HttpResponse httpResponse = HttpRequest.post(url).body(param).timeout(3000).execute();
            if (httpResponse != null && httpResponse.isOk()) {
                String result = httpResponse.body();
                return JSON.parseObject(result);
            }
        } catch (Exception ex) {
            Arrays.stream(ex.getStackTrace()).forEach(item -> LOG.error(item.toString()));
        }
        return null;
    }

}
