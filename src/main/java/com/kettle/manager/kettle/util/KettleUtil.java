package com.kettle.manager.kettle.util;

import com.kettle.manager.core.Constants;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.util.concurrent.ConcurrentHashMap;

public class KettleUtil {

	private static ConcurrentHashMap<String,Trans> TransCache = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String,Job> JobCache = new ConcurrentHashMap<>();

	/**得到job
	 * @param repository
	 * @param path
	 * @param jobName
	 * @return
	 * @throws KettleException
	 */
	public static Job getRepositoryJob(KettleDatabaseRepository repository,
			String path, String jobName) throws KettleException {

		RepositoryDirectoryInterface directory = repository.findDirectory(path);

		ObjectId jobId = repository.getJobId(jobName, directory);

		JobMeta jobMeta = repository.loadJob(jobId, null);

		Job job = null;

		if (JobCache.containsKey(jobId.getId())){

			job = JobCache.get(jobId.getId());
		}else {
			job = new Job(repository,jobMeta);
			job.setLogLevel(LogLevel.BASIC);

			JobCache.put(jobId.getId(),job);
		}

		return job;
	}

	/**得到trans
	 * @param repository
	 * @param path
	 * @param transName
	 * @return
	 * @throws KettleException
	 */
	public static Trans getRepositoryTrans(KettleDatabaseRepository repository,
			String path, String transName) throws KettleException {
		RepositoryDirectoryInterface directory = repository.findDirectory(path);// 根据指定的字符串路径找到目录

		TransMeta transMeta = repository.loadTransformation(
				repository.getTransformationID(transName, directory), null);


		Trans trans = null;

		ObjectId objectId = transMeta.getObjectId();
		if (TransCache.containsKey(objectId.getId())){

			trans = TransCache.get(objectId.getId());

		}else {

			trans = new Trans(transMeta);
			trans.setLogLevel(LogLevel.BASIC);

			TransCache.put(objectId.getId(),trans);
		}
		return trans;
	}

	/**执行任务
	 * @param job
	 * @return
	 */
	public static String executeRepositoryJob(Job job){

		job.run();
		job.waitUntilFinished();

		if (job.isFinished()){
			return job.getResult().getLogText();
		}
		return "";
	}

	/**停止任务
	 * @param job
	 */
	public static void stopRepositoryJob(Job job){

		if (job.isActive()){
			job.stopAll();
		}

	}

	public static String getRepositoryJobStatus(Job job){

		if (job.isActive()){
			return Constants.JOB_RUNNING;
		}

		if (job.isFinished()){
			return Constants.JOB_FINISHED;
		}

		if (job.isStopped()){
			return Constants.JOB_STOP;
		}

		return Constants.JOB_PREPARE;

	}

	public static String executeRepositoryTrans(Trans trans) throws KettleException {

		trans.execute(null);
		trans.waitUntilFinished();

		if (trans.isFinished()){
			return trans.getResult().getLogText();
		}
		return "";
	}

	public static void stopRepositoryTrans(Trans trans){

		if (trans.isRunning()){
			trans.stopAll();
		}

	}

	public static void pauseRepositoryTrans(Trans trans){

		if (trans.isRunning()){
			trans.pauseRunning();
		}

	}

	public static void resumeRepositoryTrans(Trans trans){

		if (trans.isPaused()){
			trans.resumeRunning();
		}

	}

	public static String getRepositoryTransStatus(Trans trans){

		if (trans.isRunning() && !trans.isPaused()){
			return Constants.TRANS_RUNNING;
		}

		if (trans.isFinished()){
			return Constants.TRANS_FINISHED;
		}

		if (trans.isStopped()){
			return Constants.TRANS_STOP;
		}

		if (trans.isPaused()){
			return Constants.TRANS_PAUSE;
		}

		return Constants.TRANS_PREPARE;

	}

}
