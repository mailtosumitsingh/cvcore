package com.sumit.cv.cvcore.dro.dro.simpledro;

import java.util.List;

import com.google.common.collect.Lists;

public class SimpleSegment {
	int x;
	int y;
	int w;
	int h;
	String label;
	public SimpleSegment(int x, int y, int w, int h, int label) {
		this(x,y,w,h,""+label);
	}
	public SimpleSegment(int x, int y, int w, int h, String label) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.label = label;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	private SimpleSegment() {
	}
	public SimpleSegment clone() {
		SimpleSegment d = new SimpleSegment(this.x,this.y,this.w,this.h,this.label);
		return d;
		
	}
}
