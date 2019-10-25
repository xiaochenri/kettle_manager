package com.kettle.manager.web.dao.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**hqc 注入自定义jpa dao repository
 * @param <T>
 * @param <S>
 * @param <ID>
 */
public class BaseJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends JpaRepositoryFactoryBean<T, S, ID> {

	public BaseJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new BaseJpaRepositoryFactory(em);
	}

	private static class BaseJpaRepositoryFactory extends JpaRepositoryFactory {


		public BaseJpaRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
		}

		//重写getTargetRepository方法并且获得当前自定义的Repository实现
		@Override
		protected BaseDaoImpl<?,?> getTargetRepository(RepositoryInformation information, EntityManager entityManager){
			return new BaseDaoImpl(information.getDomainType(), entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return BaseDaoImpl.class;
		}
	}
}
