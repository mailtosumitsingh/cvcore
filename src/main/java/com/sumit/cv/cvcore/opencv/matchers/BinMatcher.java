package com.sumit.cv.cvcore.opencv.matchers;

import org.bytedeco.opencv.opencv_core.Mat;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.opencv.DefaultShapeMatcher;
import com.sumit.cv.cvcore.opencv.Model;
import com.sumit.cv.cvcore.opencv.OpencvUtils;

public class BinMatcher  extends DefaultShapeMatcher{
	
	public double score(Mat in1, Mat in2, AnonDefObj box,Model m) {
		Mat left = OpencvUtils.getImageRoiRect(in1, (int)box.getX(),(int)box.getY(),(int)box.getR(),(int)box.getB());
		Mat right = OpencvUtils.getImageRoiRect(in2,(int) box.getX(),(int)box.getY(),(int)box.getR(),(int)box.getB());
		Mat left2 = new Mat();
		Mat right2 = new Mat();
		OpencvUtils.toGrayImage(left, left2);
		OpencvUtils.toGrayImage(right, right2);
		OpencvUtils.byteDecoSaveImage(left2, "c:\\temp\\"+box.getId()+"_"+m.getExecutionId()+"-grayLeft.jpg");
		OpencvUtils.byteDecoSaveImage(right2, "c:\\temp\\"+box.getId()+"_"+m.getExecutionId()+"-grayRight.jpg");
		OpencvUtils.convertToBinImage(left2, 20, 255);
		OpencvUtils.convertToBinImage(right2, 20, 255);
		OpencvUtils.byteDecoSaveImage(left2, "c:\\temp\\"+box.getId()+"_"+m.getExecutionId()+"-leftbin.jpg");
		OpencvUtils.byteDecoSaveImage(right2, "c:\\temp\\"+box.getId()+"_"+m.getExecutionId()+"-rightbin.jpg");
		
		double score = OpencvUtils.getPixelDiffCountGray(left2, right2, "c:\\temp\\"+box.getId()+"_"+m.getExecutionId()+"{0}.jpg", 100);
		return score;
	}


}
