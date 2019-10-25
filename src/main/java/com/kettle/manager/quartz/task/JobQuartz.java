package com.kettle.manager.quartz.task;

import java.util.Date;

import com.kettle.manager.quartz.QuartzManager;
import com.kettle.manager.web.config.SpringUtil;
import com.kettle.manager.web.service.MKettleLogService;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.kettle.manager.core.Constants;
import com.kettle.manager.web.entity.MKettleLog;
import com.kettle.manager.kettle.util.KettleUtil;
import com.kettle.manager.kettle.util.RepositoryUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 此处执行kettle的job,需要在JobExecutionContext中获取kettle的job 的相关信息，然后调用具体kettle执行任务的逻辑
 */
@Slf4j
public class JobQuartz implements Job {

	private MKettleLogService mKettleLogService = SpringUtil.getBean(MKettleLogService.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {

		JobDetail jobDetail = jobExecutionContext.getJobDetail();

		String jobName = jobDetail.getKey().getName();
		String jobGroup = jobDetail.getKey().getGroup(); // 此group为在添加任务是自定义的jobGroupName
															// 格式为自定义格式

		if (StringUtils.isNoneBlank(jobGroup)) {
			String[] split = jobGroup.split(";");

			String path = split[0];
			String repositoryId = split[1];

			if (StringUtils.isNoneBlank(repositoryId)) {
				try {
					KettleDatabaseRepository repository = RepositoryUtil
							.getKettleDatabaseRepository(repositoryId);

					if (repository != null && repository.isConnected()) {
						runRepositoryJob(repository, path, jobName);
					}
				} catch (KettleException e) {
					log.error("{}任务执行出错", jobName);
				}
			}
		}
	}

	private void runRepositoryJob(KettleDatabaseRepository repository,
			String path, String jobName) throws KettleException {

		if (StringUtils.isNoneBlank(path, jobName)) {

			if (jobName.contains(QuartzManager.BY_HAND)){
				jobName = jobName.replaceAll(QuartzManager.BY_HAND,"");
			}

			org.pentaho.di.job.Job repositoryJob = KettleUtil
					.getRepositoryJob(repository, path, jobName);

			String exception = "";
			String logText = "";
			Date startTime = new Date();
			Date endTime = null;
			String runStatus = "";

			if (repositoryJob != null) {
				try {
					String jobStatus = KettleUtil.getRepositoryJobStatus(repositoryJob);
					if (!Constants.JOB_RUNNING.equals(jobStatus)){
						KettleUtil.executeRepositoryJob(repositoryJob);
					}
					endTime = new Date();
					runStatus = Constants.NORMAL_FINISH;
				} catch (Exception e) {
					exception = e.getMessage();
					runStatus = Constants.ABNORMAL_FINISH;
					log.error("任务执行失败", e.getMessage());
				} finally {
					if (repositoryJob.isFinished()) {
						int errors = repositoryJob.getErrors();

						if (errors > 0) {
							logText = exception;
						} else {
							Result result = repositoryJob.getResult();
							logText = result.getLogText();
						}

						MKettleLog log = new MKettleLog();
						log.setJobLogText(logText);
						log.setJobName(jobName);
						log.setPath(path);
						log.setJobRepositoryName(repository.getRepositoryMeta()
								.getConnection().getName());
						log.setStartTime(startTime);
						log.setEndTime(endTime);
						log.setRunStatus(runStatus);
						mKettleLogService.saveMKettleLog(log);
					}
				}
			}
		}
	}

}
