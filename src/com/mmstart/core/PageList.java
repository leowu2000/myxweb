package com.mmstart.core;

import java.util.List;

public class PageList {

	private PageInfo pageInfo;
	private List<?> list;

	public PageList() {
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public PageList(PageInfo info, List<?> list) {
		this.pageInfo = info;
		this.list = list;
	}
}
