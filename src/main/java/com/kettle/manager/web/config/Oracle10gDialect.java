package com.kettle.manager.web.config;

import java.sql.Types;

import org.hibernate.type.StandardBasicTypes;

public class Oracle10gDialect extends org.hibernate.dialect.Oracle10gDialect {

	public Oracle10gDialect() {
		// 兼容执行nativeQuery时，抓取列类型为nvarchar并将记录放入java.util.Map
		registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
	}
	
}
