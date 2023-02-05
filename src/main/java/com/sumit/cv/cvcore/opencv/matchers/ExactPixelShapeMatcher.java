package com.sumit.cv.cvcore.opencv.matchers;

import org.bytedeco.opencv.opencv_core.Mat;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.opencv.DefaultShapeMatcher;
import com.sumit.cv.cvcore.opencv.Model;
import com.sumit.cv.cvcore.opencv.OpencvUtils;

public class ExactPixelShapeMatcher  extends DefaultShapeMatcher{
	
	public double score(Mat in1, Mat in2, AnonDefObj box,Model m) {
		Mat left = OpencvUtils.getImageRoiRect(in1, (int)box.getX(),(int)box.getY(),(int)box.getR(),(int)box.getB());
		Mat right = OpencvUtils.getImageRoiRect(in2,(int) box.getX(),(int)box.getY(),(int)box.getR(),(int)box.getB());
		double score = OpencvUtils.getPixelDiffCount(left, right, "c:\\temp\\"+box.getId()+"_"+m.getExecutionId()+"{0}.jpg", 100);
		return score;
	}


}
