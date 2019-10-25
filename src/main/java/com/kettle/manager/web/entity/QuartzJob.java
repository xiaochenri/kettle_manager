package com.kettle.manager.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "t_kettle_manager_Job")
public class QuartzJob {

    @Id
    private String id;

    @Column
    private String jobName;
    @Column
    private String path;
    @Column
    private String repositoryId;
    @Column
    private String triggerName;
    @Column
    private String triggerGroupName;
    @Column
    private String cron;       //触发器时间
    @Column
    private Date createDate;   //创建时间
    @Column
    private String jobType;    //job的类型，1 kettle的job，2 kettle的trans
    @Column
    private String jobStatus; //job的状态   0未启动 1已启动

}
