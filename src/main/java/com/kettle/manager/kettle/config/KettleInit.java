package com.kettle.manager.kettle.config;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KettleInit implements ApplicationRunner {

	public static void main(String[] str) throws KettleException {

	}

	public static void init() throws KettleException {

		if (!KettleEnvironment.isInitialized()) {

			KettleEnvironment.init();

		}

	}

	public static KettleDatabaseRepository repositoryCon()
			throws KettleException {

		//
		DatabaseMeta databaseMeta = new DatabaseMeta("trs", "oracle",
				"Native(JDBC)", "192.168.200.37", "orcl", "1521", "kettle_test",
				"trsadmin");

		// 继承与RepositoryMeta，分为数据库/文件
		KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta = new KettleDatabaseRepositoryMeta();
		kettleDatabaseRepositoryMeta.setConnection(databaseMeta);

		KettleDatabaseRepository kettleDatabaseRepository = new KettleDatabaseRepository();

		kettleDatabaseRepository.init(kettleDatabaseRepositoryMeta);

		kettleDatabaseRepository.connect("admin", "admin");

		if (kettleDatabaseRepository.isConnected()) {
			System.out.println("连接资源库成功");
			return kettleDatabaseRepository;

		} else {
			System.out.println("错误");

		}
		return null;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		init();
	}
}
