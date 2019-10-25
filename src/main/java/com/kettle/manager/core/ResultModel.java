package com.kettle.manager.core;

/**
 * hqc 返回操作结果
 * */
public class ResultModel {

	public ResultModel(){

	}

	public ResultModel(int count,int pageIndex,int pageSize){
		int totalPage = 0;
		if(count > 0 && pageIndex > 0){
			int a = count%pageSize;
			if(a != 0){
				totalPage = count/pageSize + 1;
			}else{
				totalPage = count/pageSize;
			}
		}
		this.page = Page.newBuilder().totalPage(totalPage).currentPageIndex(pageIndex).totalCount(count).build();
	}

	//状态
	private String state;
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return state;
	}
	
	//返回信息
	private String msgInfo;
	public void setMsgInfo(String msgInfo){
		this.msgInfo = msgInfo;
	}
	public String getMsgInfo() {
		return msgInfo;
	}
	
	//数据
	private Object data;
	public void setData(Object data) {
		this.data = data;
	}
	public Object getData() {
		return data;
	}

	//分页时用到
	private Page page;
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
