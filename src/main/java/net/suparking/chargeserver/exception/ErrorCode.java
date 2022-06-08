package net.suparking.chargeserver.exception;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "error_code")
public class ErrorCode {
    @Id
    public ObjectId id;
    public String code;
    public String msg;

    public static final String SUCCESS = "00000";
    public static final String COMM_NET_ERROR = "11000";
    public static final String COMM_NET_SOCKET_HEAD_FAILED = "11002";
    public static final String COMM_PARA_ERROR = "12000";
    public static final String BIZ_ERROR = "20000";
    public static final String BIZ_PARKING_ORDER_EXPIRED = "20001";
    public static final String BIZ_PARKING_ORDER_UNPAYABLE = "20002";
    public static final String BIZ_TERM_OFF_LINE = "20003";
    public static final String BIZ_MATCH_NOT_AVAILABLE = "20004";
    public static final String BIZ_CARGROUP_NOT_EXIST = "20005";
    public static final String BIZ_NO_MORE_DATA_AVAILABLE = "20006";
    public static final String BIZ_SCAN_FOR_PLATE_ENTER_WAIT = "20007";
    public static final String BIZ_SCAN_FOR_ENTERED = "20008";
    public static final String BIZ_SCAN_NO_DATA = "20009";
    public static final String BIZ_SCAN_FOR_LEFT = "20010";
    public static final String BIZ_UNFINISHED_JOB = "20011";
    public static final String BIZ_PAID_FOR_LEFT = "20015";
    public static final String BIZ_SCAN_UNMATCHED = "20016";
    public static final String BIZ_SCAN_PLATE_UNMATCHED = "20017";
    public static final String BIZ_INVALID_MATCH_CANDIDATE = "20018";
    public static final String BIZ_INVALID_PARKING_STATE = "20019";
    public static final String BIZ_MAKE_ENTER_TIME_ERROR = "20021";
    public static final String BIZ_CORRECT_FOR_LEFT = "20022";
    public static final String BIZ_INVALID_CORRECT = "20023";
    public static final String BIZ_CORRECT_FOR_SAME_PLATE = "20024";
    public static final String BIZ_CORRECT_FOR_ENTERED = "20025";
    public static final String BIZ_OPERATION_OUT_OF_DATE = "20026";
    public static final String BIZ_SCAN_FOR_UNLIC_STRICT = "20027";
    public static final String BIZ_DB_ERROR = "21000";
    public static final String BIZ_DEV_ERROR = "22000";
    public static final String BIZ_DEV_OPENGATE_FAILED = "22001";
    public static final String BIZ_DEV_CLOSEGATE_FAILED = "22002";
    public static final String BIZ_DEV_TRIGGER_FAILED = "22003";
    public static final String BIZ_DEV_CAPTURE_FAILED = "22004";
    public static final String BIZ_DEV_LCD_CAPABILITY_FAILED = "22005";
    public static final String BIZ_DEV_LCD_INFO_FAILED = "22006";
    public static final String BIZ_DEV_LCD_VOICE_CAPABILITY_FAILED = "22007";
    public static final String BIZ_DEV_LCD_VOICE_PUT_FAILED = "22008";
    public static final String AUTH_PAYER_IP_INCORRECT = "30001";
    public static final String EXCEPTION = "40000";
    public static final String EXCEPTION_PARKING_ID_NOT_EXIST = "40001";
    public static final String EXCEPTION_INVALID_MATCH = "40002";
    public static final String EXCEPTION_PARKING_ORDER_NOT_EXIST = "40003";
    public static final String EXCEPTION_RECOG_ID_NOT_EXIST = "40004";
    public static final String EXCEPTION_CHANNEL_ID_NOT_EXIST = "40005";
    public static final String EXCEPTION_INVALID_OPENGATE = "40006";
    public static final String EXCEPTION_INVALID_MAKE_ENTERED = "40007";
    public static final String EXCEPTION_PAYER_ALREADY_LOGIN = "40009";
    public static final String EXCEPTION_SENTRY_ALREADY_LOGIN = "40010";
    public static final String EXCEPTION_PARAM_NOT_SUPPORTED = "40011";
    public static final String EXCEPTION_RECOG_ID_NOT_ATTACHED = "40012";
    public static final String EXCEPTION_CONFIG_TYPE_NOT_SUPPORT = "40013";
    public static final String EXCEPTION_METHOD_NOT_SUPPORT = "40014";
    public static final String EXCEPTION_SUBAREA_ID_NOT_EXIST = "40015";
    public static final String EXCEPTION_CAR_GROUP_PARKING_NOT_EXIST = "40016";
    public static final String EXCEPTION_CAR_TYPE_ID_NOT_EXIST = "40017";
    public static final String EXCEPTION_PROTOCAL_ID_NOT_EXIST = "40018";
    public static final String EXCEPTION_NULL_PARAM = "40019";
    public static final String EXCEPTION_TIME_BALANCE_ACCOUNT_ID_NOT_EXIST = "40020";
    public static final String EXCEPTION_WALLET_ACCOUNT_ID_NOT_EXIST = "40021";
    public static final String EXCEPTION_DATA_HANDLE_NOT_FOUND = "40022";
    public static final String EXCEPTION_DATA_METHOD_NOT_SUPPORTED = "40023";

    public static boolean isSuccess(String code) {
        return code.equals(SUCCESS);
    }
}
