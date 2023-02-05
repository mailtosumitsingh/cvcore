/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package com.sumit.cv.cvcore.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.models.ConnDef;
import com.sumit.cv.cvcore.models.PortObj;
import com.sumit.cv.cvcore.models.Shape;

public class FPGraph2 {

	String name;
	Map<String, ConnDef> forward = new HashMap<String, ConnDef>();
	Map<String,PortObj> ports = new LinkedHashMap<String,PortObj>();
	List<AnonDefObj> anonDefs = new ArrayList<AnonDefObj>();
	Map<String,Shape>shapes = new HashMap<String,Shape>();
	
	public FPGraph2() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, ConnDef> getForward() {
		return forward;
	}

	public void setForward(Map<String, ConnDef> forward) {
		this.forward = forward;
	}


	public Map<String, PortObj> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, PortObj> ports) {
		this.ports = ports;
	}

	public List<AnonDefObj> getAnonDefs() {
		return anonDefs;
	}

	public void setAnonDefs(List<AnonDefObj> anonDefs) {
		this.anonDefs = anonDefs;
	}

	public Map<String, Shape> getShapes() {
		return shapes;
	}

	public void setShapes(Map<String, Shape> shapes) {
		this.shapes = shapes;
	}



}
