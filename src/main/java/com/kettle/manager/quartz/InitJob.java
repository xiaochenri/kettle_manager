package com.kettle.manager.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.kettle.manager.web.entity.QuartzJob;
import com.kettle.manager.web.service.QuartzJobService;


/**
 * 初始化的时候将所有任务的状态更改为未启动，避免系统直接关闭导致任务不正常结束的情况
 */
@Component
public class InitJob implements ApplicationRunner {

	@Resource
	private QuartzJobService jobService;

	@Override
	public void run(ApplicationArguments arg0) throws Exception {

		List<QuartzJob> quartzJobs = jobService.listQuartzJobs();

		for (QuartzJob quartzJob : quartzJobs) {
			if (quartzJob.getJobStatus().equals("1")) {
				jobService.updateQuartzJob("0", quartzJob.getId());
			}
		}

	}

}
