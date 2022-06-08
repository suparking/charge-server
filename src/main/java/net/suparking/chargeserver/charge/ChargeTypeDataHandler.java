package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.exception.ServerException;
import net.suparking.chargeserver.mq.BasicMQData;
import net.suparking.chargeserver.mq.cloud.consumber.BasicDataHandler;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.suparking.chargeserver.exception.ErrorCode.EXCEPTION_DATA_METHOD_NOT_SUPPORTED;

@Component("online_charge_type_data_handler")
public class ChargeTypeDataHandler extends BasicDataHandler {
    public static class ChargeTypeRemove extends BasicMQData {
        public Object data;

        @Override
        public String toString() {
            return "ChargeTypeRemove{" + "data=" + data + "} " + super.toString();
        }
    }

    public static class ChargeTypeSave extends BasicMQData {
        public Object data;

        @Override
        public String toString() {
            return "ChargeTypeSave{" + "data=" + data + "} " + super.toString();
        }
    }

    public static class ChargeTypeUpdate extends BasicMQData {
        public Object update;
        public Object data;

        @Override
        public String toString() {
            return "ChargeTypeUpdate{" + "update=" + update + ", data=" + data + "} " + super.toString();
        }
    }

    @Override
    public void handle(String type, String message) throws Exception {
        switch (type) {
            case "save":
                ChargeTypeSave chargeTypeSave = mapper.readValue(message, ChargeTypeSave.class);
                ChargeType chargeType = readAsObject(chargeTypeSave.data, ChargeType.class);
                ChargeType.reload(chargeType);
                break;
            case "update":
                //TODO: Do not support now
                log.warn("ChargeTypeUpdate is not supported now");
                break;
            case "remove":
                ChargeTypeRemove remove = mapper.readValue(message, ChargeTypeRemove.class);
                List<ObjectId> ids = readAsObject(remove.data, mapper.getType(List.class, ObjectId.class));
                for (ObjectId id: ids) {
                    ChargeType.unloadById(id);
                }
                break;
            default:
                throw new ServerException(EXCEPTION_DATA_METHOD_NOT_SUPPORTED);
        }
    }
}
