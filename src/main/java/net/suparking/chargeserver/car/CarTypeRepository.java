package net.suparking.chargeserver.car;
import org.bson.types.ObjectId;

public interface CarTypeRepository {
    void reloadAll();
    void reload(CarType carType);
    void unloadById(ObjectId id);
    CarType findById(ObjectId id);
    CarType findByUserId(String userId);
    boolean tempType(ObjectId id);
}
