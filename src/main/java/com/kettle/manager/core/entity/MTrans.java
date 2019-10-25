package com.kettle.manager.core.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MTrans {

    private String transName;       //trans名称
    private String path;            //trans所在路径
    private String repositoryId;    //trans所属资源库的ID
    private String repositoryName;  //trans所属资源库的名称

    private String status;       //trans此时状态
}
