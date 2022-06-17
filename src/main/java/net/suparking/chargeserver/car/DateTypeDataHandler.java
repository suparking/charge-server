package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.ServerException;
import net.suparking.chargeserver.mq.BasicMQData;
import net.suparking.chargeserver.mq.cloud.consumber.BasicDataHandler;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.suparking.chargeserver.exception.ErrorCode.EXCEPTION_DATA_METHOD_NOT_SUPPORTED;


@Component("date_type_data_handler")
public class DateTypeDataHandler extends BasicDataHandler {
    public static class DateTypeRemove extends BasicMQData {
        public String projectNo;
        public Object data;

        @Override
        public String toString() {
            return "DateTypeRemove{" + "projectNo=" + projectNo + "data=" + data + "} " + super.toString();
        }
    }

    public static class DateTypeSave extends BasicMQData {
        public Object data;

        @Override
        public String toString() {
            return "DateTypeSave{" + "data=" + data + "} " + super.toString();
        }
    }

    public static class DateTypeUpdate extends BasicMQData {
        public Object update;
        public Object data;

        @Override
        public String toString() {
            return "DateTypeUpdate{" + "update=" + update + ", data=" + data + "} " + super.toString();
        }
    }

    @Override
    public void handle(String type, String message) throws Exception {
        switch (type) {
            case "save":
                DateTypeSave dateTypeSave = mapper.readValue(message, DateTypeSave.class);
                DateType dateType = readAsObject(dateTypeSave.data, DateType.class);
                DateType.reload(dateType);
                break;
            case "update":
                //TODO: Do not support now
                log.warn("DateTypeUpdate is not supported now");
                break;
            case "remove":
                DateTypeRemove dateTypeRemove = mapper.readValue(message, DateTypeRemove.class);
                List<ObjectId> ids = readAsObject(dateTypeRemove.data, mapper.getType(List.class, ObjectId.class));
                for (ObjectId id: ids) {
                    DateType.unloadById(dateTypeRemove.projectNo, id);
                }
                break;
            default:
                throw new ServerException(EXCEPTION_DATA_METHOD_NOT_SUPPORTED);
        }
    }
}
