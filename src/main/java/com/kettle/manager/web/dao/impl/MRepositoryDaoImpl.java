package com.kettle.manager.web.dao.impl;

import com.kettle.manager.web.dao.MRepositoryDao;
import com.kettle.manager.web.entity.MRepository;

import javax.persistence.EntityManager;

public class MRepositoryDaoImpl extends BaseDaoImpl<MRepository,String> implements MRepositoryDao {

    public MRepositoryDaoImpl(EntityManager em) {
        super(MRepository.class, em);
    }


}
