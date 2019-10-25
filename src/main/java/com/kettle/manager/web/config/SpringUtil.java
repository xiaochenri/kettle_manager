package com.kettle.manager.web.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("springUtils")
@Lazy(false)
public final class SpringUtil implements ApplicationContextAware, DisposableBean {
	/** applicationContext */
	private static ApplicationContext applicationContext;

	/**
	 * 不可实例化
	 */
	private SpringUtil() {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringUtil.applicationContext = applicationContext;
	}

	public void destroy() throws Exception {
		applicationContext = null;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> type) {

		return applicationContext.getBean(type);
	}

	public static <T> Map<String, T> getBeansMap(Class<T> type) {

		return applicationContext.getBeansOfType(type);
	}

	public static <T> List<T> getBeans(Class<T> type) {

		Map<String, T> map = getBeansMap(type);

		List<T> result = new ArrayList<T>(map.size());
		for (T entity : map.values()) {
			result.add(entity);
		}

		return result;
	}

	public static <T> T getBean(String name, Class<T> type) {
		return applicationContext.getBean(name, type);
	}

}
