package com.kettle.manager.web.dao;

import com.kettle.manager.web.entity.MKettleLog;

import java.util.List;

public interface MKettleLogDao extends BaseDao<MKettleLog,String>{

    public List<MKettleLog> findAllByJobNameAndJobRepositoryName(String jobName, String jobRepositoryName);

}
