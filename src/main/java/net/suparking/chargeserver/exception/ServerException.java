package net.suparking.chargeserver.exception;

import net.suparking.chargeserver.ChargeServerApplication;

public class ServerException extends RuntimeException {
    private String code;
    private String msg;

    private static ErrorMsgRepository errorMsgRepository = ChargeServerApplication.getBean(
            "ErrorMsgRepositoryImpl", ErrorMsgRepository.class);

    public ServerException(String code) {
        this.code = code;
        msg = errorMsgRepository.getMsg(code);
    }

    public ServerException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ServerException{" + "code='" + code + '\'' + ", msg='" + msg + '\'' + "} " + super.toString();
    }
}
