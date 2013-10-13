package edu.bistu.hich.autoservice.services;

public class QueryCriteria {
	private String keyword;
	private int type;
	private int bound;
	private static QueryCriteria instance;
	
	private QueryCriteria(String keyword, int type, int bound) {
		this.keyword = keyword;
		this.type = type;
		this.bound = bound;
	}
	
	public static QueryCriteria getInstance(){
		if (instance == null) {
			instance = new QueryCriteria("", 0, 0);
		}
		return instance;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getBound() {
		return bound;
	}

	public void setBound(int bound) {
		this.bound = bound;
	}
}
