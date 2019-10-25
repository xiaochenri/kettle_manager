package com.kettle.manager.web.service.impl;

import com.kettle.manager.web.dao.MKettleLogDao;
import com.kettle.manager.web.entity.MKettleLog;
import com.kettle.manager.web.service.MKettleLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("MKettleLogService")
public class MKettleLogServiceImpl implements MKettleLogService {

    @Qualifier("MKettleLogDao")
    @Autowired
    private MKettleLogDao mKettleLogDao;

    @Override
    public void saveMKettleLog(MKettleLog log) {
        log.setId(UUID.randomUUID().toString());
        mKettleLogDao.save(log);
    }

    @Override
    public List<MKettleLog> listMKettleLog() {
        return mKettleLogDao.findAll();
    }

    @Override
    public List<MKettleLog> listMKettleLogByJobNameAndJobRepositoryName(String jobName, String jobRepositoryName) {
        return mKettleLogDao.findAllByJobNameAndJobRepositoryName(jobName, jobRepositoryName);
    }

    @Override
    public List<MKettleLog> listMKettleLogByParam(Specification<MKettleLog> specification, Pageable pageable) {
        return mKettleLogDao.findAll(specification,pageable).getContent();
    }

    @Override
    public long countMKettleLog(Specification<MKettleLog> specification) {
        return mKettleLogDao.count(specification);
    }
}
