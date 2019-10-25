package com.kettle.manager.web.dao.impl;

import com.kettle.manager.web.dao.QuartzJobDao;
import com.kettle.manager.web.entity.QuartzJob;

import javax.persistence.EntityManager;

public class QuartzJobDaoImpl extends BaseDaoImpl<QuartzJob,String> implements QuartzJobDao {

    public QuartzJobDaoImpl(EntityManager em) {
        super(QuartzJob.class, em);
    }


    @Override
    public void updateQuartzJobStatus(String status,String id) {
        String sql = "update t_kettle_manager_Job set job_status = '"+status+"' where id = '"+id+"'";
        this.update(sql);
    }
}
