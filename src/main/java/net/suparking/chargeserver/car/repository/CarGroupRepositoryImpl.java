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
    public CarGroupRepositoryImpl(@Qualifier("MongoTemplate") MongoTemplate template) {
        super(template);
    }

    @Override
    public CarGroup findByProjectNoAndUserId(String projectNo, String userId) {
        return template.findOne(Query.query(
                Criteria.where("userIds").is(userId)
        ), CarGroup.class);
    }

    @Override
    public CarGroup findByProjectNoAndId(String projectNo, ObjectId id) {
        return template.findById(id, CarGroup.class);
    }

    @Override
    public List<CarGroup> findByProjectNoAndProtocolId(String projectNo, ObjectId id) {
        return template.find(Query.query(
                Criteria.where("protocolId").is(id)
        ), CarGroup.class);
    }
}
