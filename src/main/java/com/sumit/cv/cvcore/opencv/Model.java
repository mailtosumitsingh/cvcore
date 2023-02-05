package com.sumit.cv.cvcore.opencv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.models.ConnDef;
import com.sumit.cv.cvcore.models.Shape;

public class Model {
	
	String name;
	String executionId;
	String tagTrainImage;
	String targetImage;

	List<ConnDef> connections = new ArrayList<>();
	List<AnonDefObj> anonDefs = new ArrayList<AnonDefObj>();
	Map<String,Shape>shapes = new HashMap<String,Shape>();
	
	
	public Map<String, Shape> getShapes() {
		return shapes;
	}

	public void setShapes(Map<String, Shape> shapes) {
		this.shapes = shapes;
	}

	public List<AnonDefObj> getAnonDefs() {
		return anonDefs;
	}

	public void setAnonDefs(List<AnonDefObj> anonDefs) {
		this.anonDefs = anonDefs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getTagTrainImage() {
		return tagTrainImage;
	}

	public void setTagTrainImage(String tagTrainImage) {
		this.tagTrainImage = tagTrainImage;
	}

	public String getTargetImage() {
		return targetImage;
	}

	public void setTargetImage(String targetImage) {
		this.targetImage = targetImage;
	}

	public List<ConnDef> getConnections() {
		return connections;
	}

	public void setConnections(List<ConnDef> connections) {
		this.connections = connections;
	}

}
