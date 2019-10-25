package com.kettle.manager.web.dao;

import com.kettle.manager.web.entity.QuartzJob;

public interface QuartzJobDao extends BaseDao<QuartzJob,String>{

    public void updateQuartzJobStatus(String status,String id);

}
