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
@Table(name = "t_kettle_manager_log")
public class MKettleLog {

    @Id
    private String id;
    @Column
    private String jobName;
    @Column
    private String path;
    @Column
    private String jobRepositoryName;
    @Column
    private Date startTime;
    @Column
    private Date endTime;
    @Column
    private String runStatus;
    @Column
    private String jobLogText;

}
