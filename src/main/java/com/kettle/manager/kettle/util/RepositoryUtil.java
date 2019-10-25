package com.kettle.manager.kettle.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.kettle.manager.kettle.config.KettleRepositoryLoadConfigure;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;

import com.kettle.manager.core.entity.MJob;
import com.kettle.manager.web.entity.MRepository;
import com.kettle.manager.core.entity.MTrans;
import com.kettle.manager.kettle.config.KettleInit;

/**
 * 资源库相关操作
 */
public class RepositoryUtil {

	public static ConcurrentHashMap<String, KettleDatabaseRepository> KettleDatabaseRepositoryCache = new ConcurrentHashMap<>();

	/**
	 * 获取支持的资源库类型 dbAccessTypeDesc = new String[]{"Native (JDBC)", "ODBC",
	 * "OCI", "Plugin specific access method", "JNDI", "Custom"};
	 * 
	 * @return
	 */
	public static String[] getDbAccess() {

		return DatabaseMeta.dbAccessTypeCode;
	}

	public static KettleDatabaseRepository getKettleDatabaseRepository(String repositoryId) throws KettleException {

		KettleDatabaseRepository kettleDatabaseRepository = null;
		if (StringUtils.isNoneBlank(repositoryId)){

			if (KettleDatabaseRepositoryCache.containsKey(repositoryId)){
				kettleDatabaseRepository = KettleDatabaseRepositoryCache.get(repositoryId);
			}else {
				Map<String, MRepository> repositoryMap = KettleRepositoryLoadConfigure.repositoryMap;
				MRepository mRepository = repositoryMap.get(repositoryId);
				kettleDatabaseRepository = connectRepository(mRepository);
			}
		}

		return kettleDatabaseRepository;
	}

	/**
	 * 根据连接信息连接资源库
	 * 
	 * @param mRepository
	 * @return
	 * @throws KettleException
	 */
	public static KettleDatabaseRepository connectRepository(
			MRepository mRepository) throws KettleException {

		if (mRepository != null) {

			DatabaseMeta databaseMeta = new DatabaseMeta(mRepository.getName(),
					mRepository.getRepositoryType(), mRepository.getRepositoryAccess(),
					mRepository.getHost(), mRepository.getDb(),
					mRepository.getPort(), mRepository.getTableUser(),
					mRepository.getTablePassword());

			// 继承与RepositoryMeta，分为数据库/文件
			KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta = new KettleDatabaseRepositoryMeta();
			kettleDatabaseRepositoryMeta.setConnection(databaseMeta);

			KettleDatabaseRepository kettleDatabaseRepository = new KettleDatabaseRepository();

			kettleDatabaseRepository.init(kettleDatabaseRepositoryMeta);

			kettleDatabaseRepository.connect(mRepository.getRepositoryUser(),
					mRepository.getRepositoryPassword());

			if (kettleDatabaseRepository.isConnected()) {

				String repositoryId = mRepository.getId();
				if (StringUtils.isNoneBlank(repositoryId)) {
					if (!KettleDatabaseRepositoryCache
							.containsKey(repositoryId)) {

						KettleDatabaseRepositoryCache.put(repositoryId,
								kettleDatabaseRepository);
					}
				}

				return kettleDatabaseRepository;
			}

		}

		return null;
	}

	/**
	 * 清除某资源库连接
	 * 
	 * @param repository
	 * @param id
	 */
	public static void disConnectRepository(KettleDatabaseRepository repository,
			String id) {

		if (repository != null) {
			repository.disconnect();

			KettleDatabaseRepositoryCache.remove(id);
		}

	}

	/**
	 * 查询资源库的根目录
	 * 
	 * @param repository
	 * @return
	 * @throws KettleException
	 */
	public static String getRepositoryRootDir(
			KettleDatabaseRepository repository) throws KettleException {

		if (repository != null) {
			RepositoryDirectoryInterface repositoryDirectoryInterface = repository
					.loadRepositoryDirectoryTree();

			RepositoryDirectoryInterface root = repositoryDirectoryInterface
					.findRoot();
			return root.getPath();
		}

		return "";
	}

	/**
	 * 获取指定资源库从指定目录开始的 指定目录以及下层目录
	 */
	public static List<String> getRepositoryDir(
			KettleDatabaseRepository repository, String path)
			throws KettleException {

		List<String> dirList = new ArrayList<>();

		if (repository != null) {
			// 从根目录开始找
			RepositoryDirectoryInterface directory = repository
					.findDirectory(path);

			if (directory != null) {
				dirList.add(directory.getPath());

				List<RepositoryDirectoryInterface> children = directory
						.getChildren();
				if (children.size() > 0) {

					for (RepositoryDirectoryInterface child : children) {

						dirList.add(child.getPath());
						recursionDir(child, dirList);
					}
				}
			}
		}

		return dirList;
	}

	/**
	 * 递归查找所有的子目录
	 * 
	 * @param child
	 */
	private static void recursionDir(RepositoryDirectoryInterface child,
			List<String> dirList) {

		List<RepositoryDirectoryInterface> children = child.getChildren();
		if (children.size() > 0) {

			for (RepositoryDirectoryInterface repositoryDirectoryInterface : children) {

				dirList.add(repositoryDirectoryInterface.getPath());

				recursionDir(repositoryDirectoryInterface, dirList);
			}
		}
	}

	/**
	 * 获取资源库下指定路径的所有job 包含下级目录
	 * 
	 * @param repository
	 * @param path
	 * @param repositoryId
	 * @return
	 * @throws KettleException
	 */
	public static List<MJob> getRepositoryJobs(
			KettleDatabaseRepository repository, String path,
			String repositoryId) throws KettleException {

		List<MJob> mJobs = new ArrayList<>();

		if (repository != null) {

			List<String> repositoryDir = getRepositoryDir(repository, path);

			String repositoryName = repository.getRepositoryMeta()
					.getConnection().getName();

			for (String dir : repositoryDir) {
				RepositoryDirectoryInterface rDirectory = repository
						.findDirectory(dir);

				List<RepositoryElementMetaInterface> list = repository
						.getJobAndTransformationObjects(rDirectory.getObjectId(),
								false);

				for (RepositoryElementMetaInterface metaInterface : list) {

					if (metaInterface.getObjectType()
							.equals(RepositoryObjectType.JOB)) {

						MJob mJob = new MJob();
						mJob.setJobName(metaInterface.getName());
						mJob.setPath(dir);
						mJob.setRepositoryName(repositoryName);
						mJob.setRepositoryId(repositoryId);
						mJobs.add(mJob);
					}
				}
			}
		}

		return mJobs;
	}

	/**
	 * 获取资源库下指定路径的所有trans 包含下级目录
	 * 
	 * @param repository
	 * @param path
	 * @param repositoryId
	 * @return
	 * @throws KettleException
	 */
	public static List<MTrans> getRepositoryTrans(
			KettleDatabaseRepository repository, String path,
			String repositoryId) throws KettleException {

		List<MTrans> mTransList = new ArrayList<>();

		if (repository != null) {

			List<String> repositoryDir = getRepositoryDir(repository, path);

			String repositoryName = repository.getRepositoryMeta()
					.getConnection().getName();

			for (String dir : repositoryDir) {
				RepositoryDirectoryInterface rDirectory = repository
						.findDirectory(dir);

				List<RepositoryElementMetaInterface> list = repository
						.getJobAndTransformationObjects(rDirectory.getObjectId(),
								false);

				for (RepositoryElementMetaInterface metaInterface : list) {

					if (metaInterface.getObjectType()
							.equals(RepositoryObjectType.TRANSFORMATION)) {

						MTrans mTrans = new MTrans();
						mTrans.setTransName(metaInterface.getName());
						mTrans.setPath(dir);
						mTrans.setRepositoryName(repositoryName);
						mTrans.setRepositoryId(repositoryId);
						mTransList.add(mTrans);
					}
				}
			}
		}
		return mTransList;
	}

	public static void main(String[] str) throws KettleException {

		KettleInit.init();

		KettleDatabaseRepository kettleDatabaseRepository = KettleInit
				.repositoryCon();

		getRepositoryRootDir(kettleDatabaseRepository);

	}

}
