package net.suparking.chargeserver.car;
import org.bson.types.ObjectId;

public interface CarTypeRepository {
    void reloadAll();
    void reload(CarType carType);
    void unloadById(String projectNo, ObjectId id);
    CarType findByProjectNoAndUserId(String projectNo, String userId);
    CarType findById(String projectNo, ObjectId id);
    boolean tempType(String projectNo, ObjectId id);
}
