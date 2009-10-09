package com.mmstart.core;

public class Param {

	private String col;
	private String op;
	private String value;
	private String type;

	public Param() {
	}

	public Param(String col, String op, String value, String type) {
		this.col = col;
		this.op = op;
		this.value = value;
		this.type = type;
	}

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
