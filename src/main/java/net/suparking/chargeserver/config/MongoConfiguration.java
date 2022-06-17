package net.suparking.chargeserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Order(10)
@Configuration("MongoConfiguration")
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoConfiguration extends AbstractMongoConfig {

    @Bean(name = "MongoTemplate")
    @Primary
    @Override
    public MongoTemplate getMongoTemplate(final MongoMappingContext context) throws Exception {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory()), context);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(mongoDbFactory(), converter);
    }

    @Bean
    public MongoMappingContext mongoMappingContext() throws ClassNotFoundException {
        return new MongoMappingContext();
    }
}
