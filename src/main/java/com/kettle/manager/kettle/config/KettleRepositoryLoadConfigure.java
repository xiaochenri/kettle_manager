package com.kettle.manager.kettle.config;

import java.io.File;
import java.util.*;

import com.kettle.manager.web.dao.MRepositoryDao;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.kettle.manager.web.entity.MRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 从xml文件中读取Repository的相关信息,启动自动加载，获取有关的资源库配置信息
 */
@Configuration
@Slf4j
public class KettleRepositoryLoadConfigure implements ApplicationRunner {

	public static final Map<String,MRepository> repositoryMap = new HashMap<>();

	@Override
	public void run(ApplicationArguments args) throws Exception {
		readRepository();
	}


	@Qualifier("MRepositoryDao")
	@Autowired
	private MRepositoryDao mRepositoryDao;

	private void readRepository(){

		List<MRepository> mRepositories = mRepositoryDao.findAll();

		for (MRepository mRepository : mRepositories) {

			repositoryMap.put(mRepository.getId(),mRepository);

		}
	}

	

//	private void readXml() throws DocumentException {
//
//		File file = new File(xmlAddress);
//		SAXReader read = new SAXReader();
//		Document document = read.read(file);
//
//		Element rootElement = document.getRootElement();
//
//		List connection = rootElement.elements("connection");
//
//		List<MRepository> mRepositories = getRepositoryFromXml(rootElement);
//
//		for (MRepository mRepository : mRepositories) {
//
//			String name = mRepository.getName();
//			String id = UUID.randomUUID().toString();
//			mRepository.setId(id);
//			if (StringUtils.isNoneBlank(name)) {
//
//				for (Object o : connection) {
//					Element element = (Element) o;
//
//					Element elementName = element.element("name");
//					if (name.equals(elementName.getText())) {
//
//						Element server = element.element("server");
//						Element type = element.element("type");
//						Element access = element.element("access");
//						Element database = element.element("database");
//						Element port = element.element("port");
//						Element username = element.element("username");
//						Element password = element.element("password");
//
//						if (server != null) {
//							mRepository.setHost(server.getText());
//						}
//						if (type != null) {
//							mRepository.setType(type.getText());
//						}
//						if (access != null) {
//							mRepository.setAccess(access.getText());
//						}
//						if (database != null) {
//							mRepository.setDb(database.getText());
//						}
//						if (port != null) {
//							mRepository.setPort(port.getText());
//						}
//						if (username != null) {
//							mRepository.setTableUser(username.getText());
//						}
//						if (password != null) {
//							mRepository.setTablePassword(password.getText());
//						}
//
//						mRepository.setUsable(true);
//					}
//				}
//			}
//			repositoryMap.put(id,mRepository);
//		}
//	}
//
//	private List<MRepository> getRepositoryFromXml(Element rootElement) {
//
//		List<MRepository> mRepositoryList = new ArrayList<>();
//
//		List repository = rootElement.elements("repository");
//
//		for (Object o : repository) {
//
//			Element element = (Element) o;
//
//			Element description = element.element("description");
//			if (description != null
//					&& "Database repository".equals(description.getText())) {
//
//				Element name = element.element("name");
//				MRepository mRepository = new MRepository();
//				mRepository.setName(name.getText());
//				mRepositoryList.add(mRepository);
//			}
//		}
//
//		return mRepositoryList;
//	}

	public static void main(String[] str) throws DocumentException {


	}

}
