package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.ServerException;
import net.suparking.chargeserver.mq.BasicMQData;
import net.suparking.chargeserver.mq.cloud.consumber.BasicDataHandler;
import org.springframework.stereotype.Component;

import static net.suparking.chargeserver.exception.ErrorCode.EXCEPTION_DATA_METHOD_NOT_SUPPORTED;

@Component("charge_calender_data_handler")
public class ChargeCalenderDataHandler extends BasicDataHandler {
    public static class ChargeCalenderRemove extends BasicMQData {
        public Object data;

        @Override
        public String toString() {
            return "ChargeCalenderRemove{" + "data=" + data + "} " + super.toString();
        }
    }

    public static class ChargeCalenderSave extends BasicMQData {
        public Object data;

        @Override
        public String toString() {
            return "ChargeCalenderSave{" + "data=" + data + "} " + super.toString();
        }
    }

    public static class ChargeCalenderUpdate extends BasicMQData {
        public Object update;
        public Object data;

        @Override
        public String toString() {
            return "ChargeCalenderUpdate{" + "update=" + update + ", data=" + data + "} " + super.toString();
        }
    }

    @Override
    public void handle(String type, String message) throws Exception {
        switch (type) {
            case "save":
                //TODO: Do not support now
                log.warn("CarTypeSave is not supported now");
//                ChargeCalenderSave chargeCalenderSave = mapper.readValue(message, ChargeCalenderSave.class);
//                ChargeCalender chargeCalender = readAsObject(chargeCalenderSave.data, ChargeCalender.class);
//                ChargeCalender.reload(chargeCalender);
                break;
            case "update":
                //TODO: Do not support now
                log.warn("CarTypeUpdate is not supported now");
                break;
            case "remove":
                //TODO: Do not support now
                log.warn("CarTypeRemove is not supported now");
//                ChargeCalenderRemove chargeCalenderRemove = mapper.readValue(message, ChargeCalenderRemove.class);
//                List<ObjectId> ids = readAsObject(chargeCalenderRemove.data, mapper.getType(List.class, ObjectId.class));
//                for (ObjectId id: ids) {
//                    ChargeCalender.unloadById(id);
//                }
                break;
            default:
                throw new ServerException(EXCEPTION_DATA_METHOD_NOT_SUPPORTED);
        }
    }
}
