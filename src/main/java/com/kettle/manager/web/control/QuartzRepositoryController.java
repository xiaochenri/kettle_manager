package com.kettle.manager.web.control;

import com.kettle.manager.quartz.QuartzManager;
import com.kettle.manager.web.service.QuartzJobService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kettle.manager.core.ResultModel;
import com.kettle.manager.web.entity.QuartzJob;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/kettle/quartz/repository")
public class QuartzRepositoryController {

    @Autowired
    private QuartzJobService jobService;

    /**添加Quartz的job
     * @param quartzJob
     * @return
     */
    @RequestMapping("/addQuartzJob")
	public ResultModel addQuartzJob(QuartzJob quartzJob) {
		ResultModel rm = new ResultModel();
		rm.setState("200");
		try {

			if (quartzJob != null) {

                String triggerName = quartzJob.getJobName()+"触发器";
                String triggerGroupName = "Trigger" + new Date().toString().replaceAll("[^0-9a-zA-Z_]", "");
                quartzJob.setTriggerName(triggerName);
                quartzJob.setTriggerGroupName(triggerGroupName);
                quartzJob.setCreateDate(new Date());

				String transGroupName = quartzJob.getPath() + ";" + quartzJob.getRepositoryId();

				boolean existsJob = QuartzManager.existsJob(quartzJob.getJobName(), transGroupName);

				if (existsJob){
					rm.setMsgInfo("任务已经添加，无需重新添加");
					return rm;
				}

				String jobType = quartzJob.getJobType();

				switch (jobType) {

				case "1":
                    QuartzManager.addQuartzJob(quartzJob);
					break;
				case "2":
                    QuartzManager.addQuartzTrans(quartzJob);
					break;
				default:
				    rm.setMsgInfo("任务类型系统不匹配");
					break;
				}
				quartzJob.setJobStatus("1");

				jobService.saveQuartzJob(quartzJob);

				rm.setMsgInfo("添加任务成功");
			}

		} catch (Exception ex) {
			rm.setState("500");
			rm.setMsgInfo("添加任务失败");
			log.error(ex.getMessage());
		}

		return rm;
	}

    /**移除Quartz的job
     * @param quartzId
     * @return
     */
    @RequestMapping("/removeQuartzJob")
	public ResultModel removeQuartzJob(String quartzId) {
		ResultModel rm = new ResultModel();
		rm.setState("200");
        rm.setMsgInfo("移除成功");

		try {

		    if (StringUtils.isNoneBlank(quartzId)){

                QuartzJob quartzJob = jobService.getQuartzJob(quartzId);

                if (quartzJob != null){

					QuartzManager.removeJob(quartzJob);

                	jobService.deleteQuartzJob(quartzId);
				}
            }

		} catch (Exception ex) {
			rm.setState("500");
			rm.setMsgInfo("移除任务失败");
			log.error(ex.getMessage());
		}

		return rm;
	}

	/**启动定时任务
	 * @param quartzId
	 * @return
	 */
	@RequestMapping("/executeJob")
	public ResultModel executeJob(String quartzId){
		ResultModel rm = new ResultModel();
		rm.setState("200");

		try {

			if (StringUtils.isNoneBlank(quartzId)){

				QuartzJob quartzJob = jobService.getQuartzJob(quartzId);

				if (quartzJob != null){

					QuartzManager.addQuartzJob(quartzJob);

					jobService.updateQuartzJob("1",quartzId);

					rm.setMsgInfo("启动成功");
				}else {
					rm.setMsgInfo("任务不存在");
				}
			}else {
				rm.setMsgInfo("任务不存在");
			}

		} catch (Exception ex) {
			rm.setState("500");
			rm.setMsgInfo("启动失败");
			log.error(ex.getMessage());
		}

		return rm;
	}

	@RequestMapping("/stopJob")
	public ResultModel stopJob(String quartzId){
		ResultModel rm = new ResultModel();
		rm.setState("200");

		try {

			if (StringUtils.isNoneBlank(quartzId)){

				QuartzJob quartzJob = jobService.getQuartzJob(quartzId);

				if (quartzJob != null){

					QuartzManager.removeJob(quartzJob);

					jobService.updateQuartzJob("0",quartzId);

					rm.setMsgInfo("任务已停止");
				}else {
					rm.setMsgInfo("任务不存在");
				}
			}else {
				rm.setMsgInfo("任务不存在");
			}

		} catch (Exception ex) {
			rm.setState("500");
			rm.setMsgInfo("任务停止失败");
			log.error(ex.getMessage());
		}

		return rm;
	}

	@RequestMapping("/executeJobByHand")
	public ResultModel executeJobByHand(String quartzId){
		ResultModel rm = new ResultModel();
		rm.setState("200");

		try {

			if (StringUtils.isNoneBlank(quartzId)){

				QuartzJob quartzJob = jobService.getQuartzJob(quartzId);

				if (quartzJob != null){

					String job = QuartzManager.executeJob(quartzJob);
					rm.setMsgInfo(job);
				}
			}

		} catch (Exception ex) {
			rm.setState("500");
			rm.setMsgInfo("手动执行失败");
			log.error(ex.getMessage());
		}

		return rm;
	}

	/**
	 * @return
	 */
	@RequestMapping("/getQuartzJobList")
	public ResultModel getQuartzJobList(){
		ResultModel rm = new ResultModel();
		rm.setState("200");

		try {

			List<QuartzJob> quartzJobs = jobService.listQuartzJobs();
			rm.setData(quartzJobs);

		}catch (Exception ex){
			rm.setState("500");
			rm.setMsgInfo("获取任务列表失败");
			log.error(ex.getMessage());
		}

		return rm;
	}

}
