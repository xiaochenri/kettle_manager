package com.kettle.manager.web.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.kettle.manager.web.dao.BaseDao;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;


public class BaseDaoImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements BaseDao<T, ID> {

    private final EntityManager em;

    public BaseDaoImpl(Class<T> clazz,EntityManager em) {
        super(clazz,em);
        this.em = em;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Map<String, Object> findForObject(String sql, Object... param) {
        Query query = em.createNativeQuery(sql);
        bindingParameters(query, param);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (Map<String, Object>) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String, Object>> findForList(String sql, Object... param) {
        Query query = em.createNativeQuery(sql);
        bindingParameters(query, param);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
	public List<T> findAll(String sql, Object... param) {
        Query query = em.createNativeQuery(sql, getDomainClass());
        bindingParameters(query, param);
        return (List<T>) query.getResultList();
    }

    @Override
    public long count(String sql, Object... param) {
        Query query = em.createNativeQuery(sql);
        bindingParameters(query, param);
        Number totals = (Number) query.getSingleResult();
        return totals.longValue();
    }


    @Override
    public int update(String sql, Object... param) {
        Query query = em.createNativeQuery(sql);
        bindingParameters(query, param);
        return query.executeUpdate();
    }

    private void bindingParameters(Query query, Object... parameters) {
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
        }
    }

}


