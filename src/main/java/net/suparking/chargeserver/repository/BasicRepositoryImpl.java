package net.suparking.chargeserver.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BasicRepositoryImpl {

    protected MongoTemplate template;

    @Autowired
    public BasicRepositoryImpl(@Qualifier("MongoTemplate") MongoTemplate template) {
        this.template = template;
    }
}
