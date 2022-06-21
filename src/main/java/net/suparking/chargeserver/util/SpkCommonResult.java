package net.suparking.chargeserver.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Shared Trading Result.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class SpkCommonResult implements Serializable {

    private static final long serialVersionUID = 1219598492776934916L;

    private Integer code;

    private String message;

    private Object data;

    /**
     * return success.
     * @return {@linkplain SpkCommonResult}
     */
    public static SpkCommonResult success() {
        return success("");
    }

    /**
     * return success.
     * @param msg success msg
     * @return {@link SpkCommonResult}
     */
    public static SpkCommonResult success(final String msg) {
        return success(msg, null);
    }

    /**
     * return success.
     * @param data result data
     * @return {@link SpkCommonResult}
     */
    public static SpkCommonResult success(final Object data) {
        return success(null, data);
    }

    /**
     * return success.
     * @param msg success msg
     * @param data result data
     * @return {@link SpkCommonResult}
     */
    public static SpkCommonResult success(final String msg, final Object data) {
        return get(SpkCommonCode.SUCCESS, msg, data);
    }

    /**
     * return error.
     * @param msg error msg
     * @return {@link SpkCommonResult}
     */
    public static SpkCommonResult error(final String msg) {
        return error(SpkCommonCode.ERROR, msg);
    }

    /**
     * return error.
     * @param code error code
     * @param msg error msg
     * @return {@linkplain SpkCommonResult}
     */
    public static SpkCommonResult error(final int code, final String msg) {
        return get(code, msg, null);
    }

    private static SpkCommonResult get(final int code, final String msg, final Object data) {
        return new SpkCommonResult(code, msg, data);
    }
}
