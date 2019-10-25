package com.kettle.manager.web.service.impl;

import com.kettle.manager.web.dao.QuartzJobDao;
import com.kettle.manager.web.entity.QuartzJob;
import com.kettle.manager.web.service.QuartzJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("QuartzJobService")
public class QuartzJobServiceImpl implements QuartzJobService {

    @Autowired
    private QuartzJobDao quartzJobDao;


    @Override
    public void saveQuartzJob(QuartzJob job) {
        job.setId(UUID.randomUUID().toString());
        quartzJobDao.save(job);
    }

    @Override
    public void deleteQuartzJob(String jobId) {
        quartzJobDao.deleteById(jobId);
    }

    @Override
    public List<QuartzJob> listQuartzJobs() {
        return quartzJobDao.findAll();
    }

    @Override
    public QuartzJob getQuartzJob(String jobId) {
        return quartzJobDao.getOne(jobId);
    }

    @Override
    public void updateQuartzJob(String status, String jobId) {
        quartzJobDao.updateQuartzJobStatus(status,jobId);
    }
}
