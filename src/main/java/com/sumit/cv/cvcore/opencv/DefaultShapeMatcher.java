package com.sumit.cv.cvcore.opencv;

import org.bytedeco.opencv.opencv_core.Mat;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.models.Shape;

public abstract class DefaultShapeMatcher {
	
	public abstract double score(Mat in1, Mat in2, AnonDefObj box,Model m);


}
