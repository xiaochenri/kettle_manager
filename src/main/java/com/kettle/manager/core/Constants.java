package com.kettle.manager.core;

public class Constants {

    public final static String NORMAL_FINISH="1";
    public final static String ABNORMAL_FINISH="2";

    //任务状态
    public final static String JOB_RUNNING="1";
    public final static String JOB_ABNORMAL="2";
    public final static String JOB_PAUSE="3";
    public final static String JOB_FINISHED="4";
    public final static String JOB_STOP="5";
    public final static String JOB_PREPARE="0";

    //转换状态
    public final static String TRANS_RUNNING="1";          //运行中
    public final static String TRANS_ABNORMAL="2";     //异常
    public final static String TRANS_PAUSE="3";      //暂停
    public final static String TRANS_FINISHED="4";   //完成
    public final static String TRANS_STOP="5";       //停止
    public final static String TRANS_PREPARE="0";    //准备好

}
