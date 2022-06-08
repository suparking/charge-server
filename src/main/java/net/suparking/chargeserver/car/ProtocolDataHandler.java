package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.ServerException;
import net.suparking.chargeserver.mq.BasicMQData;
import net.suparking.chargeserver.mq.cloud.consumber.BasicDataHandler;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.suparking.chargeserver.exception.ErrorCode.EXCEPTION_DATA_METHOD_NOT_SUPPORTED;


@Component("online_protocol_data_handler")
public class ProtocolDataHandler extends BasicDataHandler {
    public static class ProtocolRemove extends BasicMQData {
        public Object data;

        @Override
        public String toString() {
            return "ProtocolRemove{" + "data=" + data + "} " + super.toString();
        }
    }

    public static class ProtocolSave extends BasicMQData {
        public Object data;

        @Override
        public String toString() {
            return "ProtocolSave{" + "data=" + data + "} " + super.toString();
        }
    }

    public static class ProtocolUpdate extends BasicMQData {
        public Object update;
        public Object data;

        @Override
        public String toString() {
            return "ProtocolUpdate{" + "update=" + update + ", data=" + data + "} " + super.toString();
        }
    }

    @Override
    public void handle(String type, String message) throws Exception {
        switch (type) {
            case "save":
                ProtocolSave protocolSave = mapper.readValue(message, ProtocolSave.class);
                Protocol protocol = readAsObject(protocolSave.data, Protocol.class);
                Protocol.reload(protocol);
                break;
            case "update":
                //TODO: Do not support now
                log.warn("ProtocolUpdate is not supported now");
                break;
            case "remove":
                ProtocolRemove protocolRemove = mapper.readValue(message, ProtocolRemove.class);
                List<ObjectId> ids = readAsObject(protocolRemove.data, mapper.getType(List.class, ObjectId.class));
                for (ObjectId id: ids) {
                    Protocol.unloadById(id);
                }
                break;
            default:
                throw new ServerException(EXCEPTION_DATA_METHOD_NOT_SUPPORTED);
        }
    }
}
