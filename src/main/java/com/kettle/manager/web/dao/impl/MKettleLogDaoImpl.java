package com.kettle.manager.web.dao.impl;

import com.kettle.manager.web.dao.MKettleLogDao;
import com.kettle.manager.web.entity.MKettleLog;
import com.kettle.manager.web.service.MKettleLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class MKettleLogDaoImpl extends BaseDaoImpl<MKettleLog,String> implements MKettleLogDao {


    public MKettleLogDaoImpl(EntityManager em) {
        super(MKettleLog.class, em);
    }

    @Override
    public List<MKettleLog> findAllByJobNameAndJobRepositoryName(String jobName, String jobRepositoryName) {

        Specification<MKettleLog> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> list =  new ArrayList<>();

            if (StringUtils.isNoneBlank(jobName)){
                list.add(criteriaBuilder.like(root.get("jobName"),"%"+jobName+"%"));
            }
            list.add(criteriaBuilder.equal(root.get("jobRepositoryName"),jobRepositoryName));

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        });

        return this.findAll(specification);
    }

}
