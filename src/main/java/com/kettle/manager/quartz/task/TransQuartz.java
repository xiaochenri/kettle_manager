package com.kettle.manager.quartz.task;

import java.util.Date;

import com.kettle.manager.quartz.QuartzManager;
import com.kettle.manager.web.config.SpringUtil;
import com.kettle.manager.web.service.MKettleLogService;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.Trans;
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
 * execute（）方法由调度程序的一个工作线程调用
 */
@Slf4j
public class TransQuartz implements Job {

	private MKettleLogService mKettleLogService = SpringUtil.getBean(MKettleLogService.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {

		JobDetail jobDetail = jobExecutionContext.getJobDetail();

		String jobName = jobDetail.getKey().getName();
		String jobGroup = jobDetail.getKey().getGroup();

		if (StringUtils.isNoneBlank(jobGroup)) {
			String[] split = jobGroup.split(";");

			String path = split[0];
			String repositoryId = split[1];

			if (StringUtils.isNoneBlank(repositoryId)) {

				try {
					KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);

					if (repository != null && repository.isConnected()) {
						runRepositoryTrans(repository, path, jobName);
					}
				} catch (KettleException e) {
					log.error("{}转换执行出错", jobName);
				}
			}
		}
	}

	private void runRepositoryTrans(KettleDatabaseRepository repository,
			String path, String jobName) throws KettleException {

		if (StringUtils.isNoneBlank(path, jobName)) {

			if (jobName.contains(QuartzManager.BY_HAND)){
				jobName = jobName.replaceAll(QuartzManager.BY_HAND,"");
			}

			Trans trans = KettleUtil.getRepositoryTrans(repository, path,
					jobName);

			String exception = "";
			String logText = "";
			Date startTime = new Date();
			Date endTime = null;
			String runStatus = "";

			if (trans != null) {
				try {

					String transStatus = KettleUtil.getRepositoryTransStatus(trans);

					if (!transStatus.equals(Constants.TRANS_RUNNING)){
						KettleUtil.executeRepositoryTrans(trans);
					}
					endTime = new Date();
					runStatus = Constants.NORMAL_FINISH;
				} catch (Exception e) {
					exception = e.getMessage();
					runStatus = Constants.ABNORMAL_FINISH;
					log.error("任务执行失败", e.getMessage());
				} finally {
					if (trans.isFinished()) {
						int errors = trans.getErrors();

						if (errors > 0) {
							logText = exception;
						} else {
							Result result = trans.getResult();
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
