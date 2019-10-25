package com.kettle.manager.web.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 自定义的资源库实体类，用于存储连接资源库的各种信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "t_kettle_manager_repository")
public class MRepository {

	@Id
    private String id;    //添加唯一主键，用于缓存存储标识
	@Column
	private String name;  //资源库名称
	@Column
	private String repositoryType;  //资源库类型
	@Column
	private String repositoryAccess; //资源库access
	@Column
	private String host;  //资源库host
	@Column
	private String db;   //数据库名称
	@Column
	private String port; //数据库端口
	@Column
	private String tableUser; //数据库用户名
	@Column
	private String tablePassword; //数据库用户密码
	@Column
	private String repositoryUser;  //连接用户
	@Column
	private String repositoryPassword; //连接密码
	@Column
	private String usable="0";  //是否可用，默认不可用，0 不可用 1可用

}
