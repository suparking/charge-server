package net.suparking.chargeserver.car.repository;

import net.suparking.chargeserver.car.CarGroup;
import net.suparking.chargeserver.car.CarGroupRepository;
import net.suparking.chargeserver.repository.BasicRepositoryImpl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CarGroupRepositoryImpl")
public class CarGroupRepositoryImpl extends BasicRepositoryImpl implements CarGroupRepository {
    @Autowired
    public CarGroupRepositoryImpl(@Qualifier("MongoTemplate")MongoTemplate template) {
        super(template);
    }

    @Override
    public void save(CarGroup carGroup) {
        template.save(carGroup);
    }

    @Override
    public CarGroup findByUserId(String userId) {
        return template.findOne(Query.query(
                Criteria.where("userIds").is(userId)
        ), CarGroup.class);
    }

    @Override
    public CarGroup findById(ObjectId id) {
        return template.findById(id, CarGroup.class);
    }

    @Override
    public List<CarGroup> findByProtocolId(ObjectId id) {
        return template.find(Query.query(
                Criteria.where("protocolId").is(id)
        ), CarGroup.class);
    }
}
