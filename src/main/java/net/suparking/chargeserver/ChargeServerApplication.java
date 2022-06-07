package net.suparking.chargeserver;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		MongoAutoConfiguration.class,
		MongoDataAutoConfiguration.class,
		RabbitAutoConfiguration.class
})
public class ChargeServerApplication implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(ChargeServerApplication.class, args);
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		applicationContext = ctx;
	}

	public static <T> T getBean(final String name, final Class<T> clazz) {
		return applicationContext.getBean(name, clazz);
	}

	public static boolean containsBean(final String name) {
		return applicationContext.containsBean(name);
	}
}
