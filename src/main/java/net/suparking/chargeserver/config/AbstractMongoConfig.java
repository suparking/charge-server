package net.suparking.chargeserver.config;

import lombok.Data;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Data
public abstract class AbstractMongoConfig {
    private String uri;

    /**
     * 创建 mongo 抽象工厂
     * @return
     * @throws Exception
     */
    public MongoDatabaseFactory mongoDbFactory() throws Exception{
        return new SimpleMongoClientDatabaseFactory(uri);
    }

    abstract public MongoTemplate getMongoTemplate(MongoMappingContext context) throws Exception;
}
