package com.sumit.cv.cvcore.engine;

import java.util.HashMap;
import java.util.Map;

import org.bytedeco.opencv.opencv_core.Mat;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.opencv.DefaultShapeMatcher;
import com.sumit.cv.cvcore.opencv.OpencvUtils;
import com.sumit.cv.cvcore.opencv.matchers.ExactPixelShapeMatcher;

public  class OpencvVnodeHandler extends DefaultVNodeHandler {

	Map<String, Map<String,Double>> scores = new HashMap<String, Map<String,Double>>();
	
	Mat left, right;
	Map<String, DefaultShapeMatcher> matchers = new HashMap<>();
	{
		matchers.put("match", new ExactPixelShapeMatcher());
		matchers.put("exact", new ExactPixelShapeMatcher());
		matchers.put("exactmatch", new ExactPixelShapeMatcher());
		matchers.put("default", new ExactPixelShapeMatcher());
		matchers.put("pixel", new ExactPixelShapeMatcher());
	}

	@Override
	public void handleAsParentWithoutChilds(FPGraph2 o, Map<String, Object> ctx, AnonDefObj self) {
               for(String s : self.getTags()) {
            	   DefaultShapeMatcher matcher = matchers.get(s);
            	  double score = matcher.score(left, right, self, model);
            	  Map<String, Double> map = scores.getOrDefault(self.getId(), new HashMap<String, Double>());
            	  map.put(s, score);
            	  scores.put(self.getId(), map);
               }                
	}

	@Override
	public void handleChild(FPGraph2 o, Map<String, Object> ctx, VNode<AnonDefObj> vnodeSelf) {

	}

	@Override
	public void handleAsParentWithChilds(FPGraph2 o, Map<String, Object> ctx, AnonDefObj self) {

	}
	@Override
	public void postChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode,
			VNode<AnonDefObj> vnodeChild) {
	}
	@Override
	public void beforeChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode,
			VNode<AnonDefObj> vnodeChild) {
	}

	@Override
	public void init() {
		left = OpencvUtils.byteDecoReadImage(model.getTargetImage());
		right = OpencvUtils.byteDecoReadImage(model.getTagTrainImage());
	}

	public Map<String, Map<String, Double>> getScores() {
		return scores;
	}

	public void setScores(Map<String, Map<String, Double>> scores) {
		this.scores = scores;
	}	

}
