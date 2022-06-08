package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.List;

public interface ProtocolRepository {
    void reloadAll();
    void reload(Protocol protocol);
    void unloadById(ObjectId id);
    Protocol findById(ObjectId id);
    List<Protocol> findAll();
}
