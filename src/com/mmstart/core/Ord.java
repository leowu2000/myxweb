package com.mmstart.core;

public class Ord {

	private String col;
	private boolean ascending;

	public Ord() {
	}

	public Ord(String col, boolean ascending) {
		this.col = col;
		this.ascending = ascending;
	}

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public boolean getAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

}
