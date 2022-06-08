package net.suparking.chargeserver.mq;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.ErrorMsgRepository;

import static net.suparking.chargeserver.exception.ErrorCode.SUCCESS;

public class BasicMQMessageRet {
    public String code;
    public String msg;

    private static final ErrorMsgRepository errorMsgRepository = ChargeServerApplication.getBean(
            "ErrorMsgRepositoryImpl", ErrorMsgRepository.class);

    public BasicMQMessageRet() {
        code = SUCCESS;
    }

    public BasicMQMessageRet(String code) {
        this.code = code;
        msg = errorMsgRepository.getMsg(code);
    }

    public BasicMQMessageRet(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
