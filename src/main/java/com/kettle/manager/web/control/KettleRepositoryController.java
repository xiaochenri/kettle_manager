package com.kettle.manager.web.control;

import javax.servlet.http.HttpServletRequest;

import com.kettle.manager.core.Constants;
import com.kettle.manager.core.entity.MJob;
import com.kettle.manager.web.entity.MRepository;
import com.kettle.manager.core.entity.MTrans;
import com.kettle.manager.kettle.config.KettleRepositoryLoadConfigure;
import com.kettle.manager.kettle.util.KettleUtil;
import com.kettle.manager.kettle.util.RepositoryUtil;
import com.kettle.manager.web.entity.MKettleLog;
import com.kettle.manager.web.service.MKettleLogService;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kettle.manager.core.ResultModel;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RestController
@RequestMapping("/kettle/repository")
public class KettleRepositoryController {

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private MKettleLogService logService;

    /**查询资源库
     * @param request
     * @return
     */
    @RequestMapping("/getRepositoryList")
	public ResultModel getRepositoryList(HttpServletRequest request) {

		ResultModel rm = new ResultModel();
        rm.setState("200");

		try {
		    List<MRepository> repositoryList = new ArrayList<>();

            Map<String, MRepository> repositoryMap = KettleRepositoryLoadConfigure.repositoryMap;
            for (MRepository mRepository : repositoryMap.values()) {

                if (mRepository.getUsable().equals("1")){
                    repositoryList.add(mRepository);
                }
            }

            rm.setData(repositoryList);
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("资源库列表获取失败");
            log.error(ex.getMessage());
        }

		return rm;
	}


    /**登录资源库
     * @param repositoryId
     * @param user
     * @param password
     * @return
     */
    @RequestMapping("/loginRepository")
	public ResultModel loginRepository(String repositoryId,String user,String password){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try {

            Map<String, MRepository> repositoryMap = KettleRepositoryLoadConfigure.repositoryMap;
            MRepository mRepository = repositoryMap.get(repositoryId);

            if (mRepository.getRepositoryUser().equals(user) && mRepository.getRepositoryPassword().equals(password)){
                KettleDatabaseRepository repository = RepositoryUtil.connectRepository(mRepository);
                if (repository != null){
                    rm.setMsgInfo("资源库登录成功");
                }else {
                    rm.setState("500");
                    rm.setMsgInfo("资源库登录失败");
                }
            }else {
                rm.setState("500");
                rm.setMsgInfo("资源库登录失败");
            }
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("资源库登录失败");
            log.error(ex.getMessage());
        }

        return rm;
    }

    /**退出资源库
     * @param repositoryId
     * @return
     */
    @RequestMapping("/exitRepository")
    public ResultModel exitRepository(String repositoryId){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{

            Map<String, KettleDatabaseRepository> repositoryCatch = RepositoryUtil.KettleDatabaseRepositoryCache;

            if (repositoryCatch.containsKey(repositoryId)){

                KettleDatabaseRepository repository = repositoryCatch.get(repositoryId);

                RepositoryUtil.disConnectRepository(repository,repositoryId);
            }
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("资源库退出失败");
            log.error(ex.getMessage());
        }
        return rm;
    }

    /**查询资源库的所有目录
     * @param repositoryId
     * @return
     */
    @RequestMapping("/getRepositoryDirList")
    public ResultModel getRepositoryDirList(String repositoryId){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{

            List<String> dirList = new ArrayList<>();

            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);

            if (repository != null) {

                String rootDir = RepositoryUtil.getRepositoryRootDir(repository);

                dirList = RepositoryUtil.getRepositoryDir(repository, rootDir);
            }

            rm.setData(dirList);

        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("获取资源库目录失败");
            log.error(ex.getMessage());
        }
        return rm;

    }

    /**查询资源库指定目录下的作业
     * @param repositoryId
     * @param path
     * @return
     */
    @RequestMapping("/getRepositoryJobs")
    public ResultModel getRepositoryJobs(String repositoryId,String path){
        ResultModel rm = new ResultModel();
        rm.setState("200");

        try {
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);

            List<MJob> repositoryJobs = RepositoryUtil.getRepositoryJobs(repository, path, repositoryId);

            for (MJob mJob : repositoryJobs) {

                Job job = KettleUtil.getRepositoryJob(repository, mJob.getPath(), mJob.getJobName());

                mJob.setStatus(KettleUtil.getRepositoryJobStatus(job));
            }

            rm.setData(repositoryJobs);

        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("获取资源库作业列表失败");
            log.error(ex.getMessage());
        }
        return rm;
    }

    /**查询资源库指定目录下的转换
     * @param repositoryId
     * @param path
     * @return
     */
    @RequestMapping("/getRepositoryTrans")
    public ResultModel getRepositoryTrans(String repositoryId,String path){
        ResultModel rm = new ResultModel();
        rm.setState("200");

        try {
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);

            List<MTrans> repositoryTrans = RepositoryUtil.getRepositoryTrans(repository, path, repositoryId);

            for (MTrans mTrans : repositoryTrans) {

                Trans trans = KettleUtil.getRepositoryTrans(repository, mTrans.getPath(), mTrans.getTransName());

                mTrans.setStatus(KettleUtil.getRepositoryTransStatus(trans));
            }
            rm.setData(repositoryTrans);

        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("获取资源库转换列表失败");
            log.error(ex.getMessage());
        }
        return rm;
    }

    /**启动任务  只有当任务处于停止状态且可运行状态时可启动，否者启动失败
     * @param repositoryId
     * @param path
     * @param jobName
     * @return
     */
    @RequestMapping("/executeRepositoryJob")
    public ResultModel executeRepositoryJob(String repositoryId,String path,String jobName){
        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{

            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Job job = KettleUtil.getRepositoryJob(repository, path, jobName);

            MKettleLog log = new MKettleLog();
            log.setPath(path);
            log.setJobName(jobName);
            log.setJobRepositoryName(repository.getRepositoryMeta().getConnection().getName());

            executor.submit(new JobTask(job,log));
            rm.setMsgInfo("启动成功");
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("启动失败");
            log.error(ex.getMessage());
        }

        return rm;
    }

    public class JobTask implements Runnable{

        private Job job;
        private MKettleLog kettleLog;

        public JobTask(Job job,MKettleLog log){
            this.job = job;
            this.kettleLog = log;
        }

        @Override
        public void run() {

            Date startTime = new Date();

            String type = "1";
            if (!Constants.JOB_RUNNING.equals(KettleUtil.getRepositoryJobStatus(job))){
                job.run();
                job.waitUntilFinished();
                type = "2";
            }

            Date endTime = new Date();

            if (job.isFinished() && "2".equals(type)){

                kettleLog.setJobLogText(job.getResult().getLogText());
                kettleLog.setStartTime(startTime);
                kettleLog.setEndTime(endTime);
                kettleLog.setRunStatus(Constants.NORMAL_FINISH);
                if (job.getErrors() > 0){
                    kettleLog.setRunStatus(Constants.ABNORMAL_FINISH);
                }

                logService.saveMKettleLog(kettleLog);
            }
        }
    }

    /**停止任务
     * @param repositoryId
     * @param path
     * @param jobName
     * @return
     */
    @RequestMapping("/stopRepositoryJob")
    public ResultModel stopRepositoryJob(String repositoryId,String path,String jobName){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Job job = KettleUtil.getRepositoryJob(repository, path, jobName);

            KettleUtil.stopRepositoryJob(job);
            rm.setMsgInfo("任务已停止");

        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("停止失败");
            log.error(ex.getMessage());
        }

        return rm;
    }

    /**查询任务状态
     * @param repositoryId
     * @param path
     * @param jobName
     * @return
     */
    @RequestMapping("/getRepositoryJobStatus")
    public ResultModel getRepositoryJobStatus(String repositoryId,String path,String jobName){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Job job = KettleUtil.getRepositoryJob(repository, path, jobName);

            String transStatus = KettleUtil.getRepositoryJobStatus(job);

            rm.setData(transStatus);

        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("获取状态失败");
            log.error(ex.getMessage());
        }

        return rm;
    }

    /**启动转换
     * @param repositoryId
     * @param path
     * @param transName
     * @return
     */
    @RequestMapping("/executeRepositoryTrans")
    public ResultModel executeRepositoryTrans(String repositoryId,String path,String transName){
        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{

            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Trans trans = KettleUtil.getRepositoryTrans(repository, path, transName);

            MKettleLog log = new MKettleLog();
            log.setPath(path);
            log.setJobName(transName);
            log.setJobRepositoryName(repository.getRepositoryMeta().getConnection().getName());

            executor.submit(new TransTask(trans,log));
            rm.setMsgInfo("启动成功");
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("启动失败");
            log.error(ex.getMessage());
        }

        return rm;
    }

    public class TransTask implements Runnable{

        private Trans trans;
        private MKettleLog kettleLog;

        public TransTask(Trans trans,MKettleLog log){
            this.trans = trans;
            this.kettleLog = log;
        }

        @Override
        public void run() {
            String status = Constants.NORMAL_FINISH;
            Date startTime = null,endTime = null;
            String  type = "1";
            try {
                startTime = new Date();

                if (!Constants.TRANS_RUNNING.equals(KettleUtil.getRepositoryTransStatus(trans))){
                    trans.execute(null);
                    trans.waitUntilFinished();
                    type = "2";
                }
                endTime = new Date();
            } catch (KettleException e) {
                status = Constants.ABNORMAL_FINISH;
            } finally {
                if (trans.isFinished() && "2".equals(type)){
                    kettleLog.setRunStatus(status);
                    kettleLog.setStartTime(startTime);
                    kettleLog.setEndTime(endTime);
                    kettleLog.setJobLogText(trans.getResult().getLogText());
                    logService.saveMKettleLog(kettleLog);
                }
            }
        }
    }

    /**停止转换
     * @param repositoryId
     * @param path
     * @param transName
     * @return
     */
    @RequestMapping("/stopRepositoryTrans")
    public ResultModel stopRepositoryTrans(String repositoryId,String path,String transName){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Trans trans = KettleUtil.getRepositoryTrans(repository, path, transName);

            KettleUtil.stopRepositoryTrans(trans);

            rm.setMsgInfo("任务已停止");
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("停止失败");
            log.error(ex.getMessage());
        }

        return rm;
    }

    /**暂停转换
     * @param repositoryId
     * @param path
     * @param transName
     * @return
     */
    @RequestMapping("/pauseRepositoryTrans")
    public ResultModel pauseRepositoryTrans(String repositoryId,String path,String transName){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Trans trans = KettleUtil.getRepositoryTrans(repository, path, transName);

            KettleUtil.pauseRepositoryTrans(trans);
            rm.setMsgInfo("任务已暂停");
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("暂停失败");
            log.error(ex.getMessage());
        }

        return rm;
    }


    /**恢复暂停的转换
     * @param repositoryId
     * @param path
     * @param transName
     * @return
     */
    @RequestMapping("/resumeRepositoryTrans")
    public ResultModel resumeRepositoryTrans(String repositoryId,String path,String transName){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Trans trans = KettleUtil.getRepositoryTrans(repository, path, transName);

            KettleUtil.resumeRepositoryTrans(trans);
            rm.setMsgInfo("任务已恢复");
        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("恢复失败");
            log.error(ex.getMessage());
        }

        return rm;
    }


    /**查询转换的状态
     * @param repositoryId
     * @param path
     * @param transName
     * @return
     */
    @RequestMapping("/getRepositoryTransStatus")
    public ResultModel getRepositoryTransStatus(String repositoryId,String path,String transName){

        ResultModel rm = new ResultModel();
        rm.setState("200");

        try{
            KettleDatabaseRepository repository = RepositoryUtil.getKettleDatabaseRepository(repositoryId);
            Trans trans = KettleUtil.getRepositoryTrans(repository, path, transName);

            String transStatus = KettleUtil.getRepositoryTransStatus(trans);

            rm.setData(transStatus);

        }catch (Exception ex){
            rm.setState("500");
            rm.setMsgInfo("获取状态失败");
            log.error(ex.getMessage());
        }

        return rm;
    }



}
