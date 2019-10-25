package com.kettle.manager.core.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MJob {

    private String jobName;       //job名称
    private String path;          //job所在路径
    private String repositoryId;  //job所属资源库的ID
    private String repositoryName;//job所属资源库的名称

    private String status;       //job此时状态

}

