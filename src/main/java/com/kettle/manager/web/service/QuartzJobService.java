package com.kettle.manager.web.service;

import com.kettle.manager.web.entity.QuartzJob;
import org.springframework.stereotype.Service;

import java.util.List;

public interface QuartzJobService {

    public void saveQuartzJob(QuartzJob job);

    public void deleteQuartzJob(String jobId);

    public List<QuartzJob> listQuartzJobs();

    public QuartzJob getQuartzJob(String jobId);

    public void updateQuartzJob(String status,String jobId);

}
