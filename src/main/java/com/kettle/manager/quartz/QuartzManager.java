package com.kettle.manager.quartz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import com.kettle.manager.quartz.task.JobQuartz;
import com.kettle.manager.quartz.task.TransQuartz;
import com.kettle.manager.web.entity.QuartzJob;

import lombok.extern.slf4j.Slf4j;
import org.quartz.impl.matchers.KeyMatcher;

/**
 * quartz 管理，quartz讲任务实体与触发器绑定在一起，当Scheduler处于启动状态时，触发器来决定任务的调度
 */
@Slf4j
public class QuartzManager {

	private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

	public static String BY_HAND = "byHand";     //手动执行任务，需要在添加 任务的基础上修改任务名称，group名称等，来达到及时启动的目的

	/**
	 * @param job
	 *            kettle的job类，cron是前端传递过来的定时参数
	 */
	public static void addQuartzJob(QuartzJob job) throws SchedulerException {

		if (job != null) {

			String jobName = job.getJobName();
			String path = job.getPath();
			String repositoryId = job.getRepositoryId();

			String jobGroupName = path + ";" + repositoryId;

			String triggerName = job.getTriggerName();
			String triggerGroupName = job.getTriggerGroupName();

			String cron = job.getCron();

			Map<String, Object> parameter = new HashMap<>();

			addQuartzJob(jobName, jobGroupName, triggerName, triggerGroupName,
					JobQuartz.class, cron, parameter);
		}
	}

	public static void addQuartzTrans(QuartzJob trans) throws SchedulerException {

		if (trans != null) {

			String transName = trans.getJobName();
			String path = trans.getPath();
			String repositoryId = trans.getRepositoryId();

			String transGroupName = path + ";" + repositoryId;

			String triggerName = trans.getTriggerName();
			String triggerGroupName = trans.getTriggerGroupName();

			String cron = trans.getCron();

			Map<String, Object> parameter = new HashMap<>();

			addQuartzJob(transName, transGroupName, triggerName,
					triggerGroupName, TransQuartz.class, cron, parameter);
		}

	}

	/**
	 * 增加Quartz的任务，可能存在两种任务 1：kettle的job；2：kettle的trans
	 * 
	 * @param jobName
	 * @param jobGroupName
	 *            //jobGroupName需要使用特定的格式来存储kettle任务的信息，path;datareposityId的字符串格式
	 * @param triggerName
	 * @param triggerGroupName
	 * @param jobClass
	 * @param cron
	 * @param parameter
	 */
	private static void addQuartzJob(String jobName, String jobGroupName,
			String triggerName, String triggerGroupName,
			Class<? extends Job> jobClass, String cron,
			Map<String, Object> parameter) throws SchedulerException {

		Scheduler scheduler = schedulerFactory.getScheduler();

		// 任务名，任务组，任务执行类
		JobDetail jobDetail = JobBuilder.newJob(jobClass)
				.withIdentity(jobName, jobGroupName).build();

		// 添加任务执行的参数
		parameter.forEach((k, v) -> {
			jobDetail.getJobDataMap().put(k, v);
		});
		// 触发器
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		// 触发器名,触发器组
		triggerBuilder.withIdentity(triggerName, triggerGroupName);
		triggerBuilder.startNow();
		// 触发器时间设定
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
		// 创建Trigger对象
		CronTrigger trigger = (CronTrigger) triggerBuilder.build();
		// 调度容器设置JobDetail和Trigger
		scheduler.scheduleJob(jobDetail, trigger);
		// 启动
		if (!scheduler.isShutdown()) {
			scheduler.start();
		}
	}

	public static void removeJob(QuartzJob job) throws SchedulerException {

		String jobName = job.getJobName();
		String path = job.getPath();
		String repositoryId = job.getRepositoryId();

		String jobGroupName = path + ";" + repositoryId;

		removeJob(jobName, jobGroupName, job.getTriggerName(),
				job.getTriggerGroupName());
	}

	/**
	 * 移除某一任务
	 * 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 */
	private static void removeJob(String jobName, String jobGroupName,
			String triggerName, String triggerGroupName) throws SchedulerException {
			Scheduler sched = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,
					triggerGroupName);
			sched.pauseTrigger(triggerKey);// 停止触发器
			sched.unscheduleJob(triggerKey);// 移除触发器
			sched.interrupt(JobKey.jobKey(jobName, jobGroupName));
			sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务
	}

    /**
     * 判断定时任务是否存在
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public static boolean existsJob(String jobName, String jobGroup) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            return sched.checkExists(JobKey.jobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 手动执行任务
     */
    public static String executeJob(QuartzJob quartzJob) throws SchedulerException {

        Scheduler sched = schedulerFactory.getScheduler();

        String jobType = quartzJob.getJobType();

        Class tClass = null;

        switch (jobType) {

            case "1":
                tClass = JobQuartz.class;
                break;
            case "2":
                tClass = TransQuartz.class;
                break;
        }

        String group = quartzJob.getPath() + ";" + quartzJob.getRepositoryId();

        JobBuilder jobBuilder = JobBuilder.newJob(tClass)
                .withIdentity(quartzJob.getJobName()+BY_HAND, group);

        JobDetail jobDetail = jobBuilder.build();
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity(quartzJob.getTriggerName()+BY_HAND, quartzJob.getTriggerGroupName());
        triggerBuilder.startNow();

        Trigger trigger = triggerBuilder.build();
        try {
            sched.scheduleJob(jobDetail, trigger);

            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }

        } catch (ObjectAlreadyExistsException e) {
            log.error(e.getMessage());
            return "该任务已在手动执行过程中，请稍后再试！";
        }
        return "手动执行成功";
    }

    /**
	 * 开始所有任务
	 */
	public static void startJobs() {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			scheduler.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 停止所有任务
	 */
	public static void shutdownJobs() {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			if (!scheduler.isShutdown()) {
				scheduler.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
