package com.kettle.manager.web.service;

import com.kettle.manager.web.entity.MKettleLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface MKettleLogService {

    public void saveMKettleLog(MKettleLog log);

    public List<MKettleLog> listMKettleLog();

    public List<MKettleLog> listMKettleLogByJobNameAndJobRepositoryName(String jobName,String jobRepositoryName);

    public List<MKettleLog> listMKettleLogByParam(Specification<MKettleLog> specification, Pageable pageable);

    public long countMKettleLog(Specification<MKettleLog> specification);

}
