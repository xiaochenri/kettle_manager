package com.kettle.manager;

import com.kettle.manager.web.dao.impl.BaseJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableJpaRepositories(repositoryFactoryBeanClass = BaseJpaRepositoryFactoryBean.class)
@SpringBootApplication
public class ManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagerApplication.class, args);
	}

	private static int corePoolSize = Runtime.getRuntime()
			.availableProcessors();

	//初始化线程池
	@Bean
	public ThreadPoolExecutor threadPoolExecutor(){
		return new ThreadPoolExecutor(
				corePoolSize, corePoolSize * 2, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(1000));
	}
}
