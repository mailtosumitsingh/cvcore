package com.sumit.cv.cvcore.opencv.matchers;

import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_features2d;
import org.bytedeco.opencv.opencv_core.DMatchVector;
import org.bytedeco.opencv.opencv_core.KeyPointVector;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_features2d.BFMatcher;
import org.bytedeco.opencv.opencv_features2d.ORB;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.opencv.DefaultShapeMatcher;
import com.sumit.cv.cvcore.opencv.Model;
import com.sumit.cv.cvcore.opencv.OpencvUtils;

public class SimilarityMatcher  extends DefaultShapeMatcher{
	
	public double score(Mat in1, Mat in2, AnonDefObj box,Model m) {
		Mat left = OpencvUtils.getImageRoiRect(in1, (int)box.getX(),(int)box.getY(),(int)box.getR(),(int)box.getB());
		Mat right = OpencvUtils.getImageRoiRect(in2,(int) box.getX(),(int)box.getY(),(int)box.getR(),(int)box.getB());
//		(int nfeatures/*=500*/, float scaleFactor/*=1.2f*/, int nlevels/*=8*/, int edgeThreshold/*=31*/,
	//            int firstLevel/*=0*/, int WTA_K/*=2*/, @Cast("cv::ORB::ScoreType") int scoreType/*=cv::ORB::HARRIS_SCORE*/, int patchSize/*=31*/, int fastThreshold/*=20*/);

		ORB orbLeft =  ORB.create(500,1.2f,8,32,0,2,ORB.HARRIS_SCORE,32,20);
		ORB orbRight = ORB.create(500,1.2f,8,32,0,2,ORB.HARRIS_SCORE,32,20);
		KeyPointVector vectorL = new KeyPointVector();
		KeyPointVector vectorR = new KeyPointVector();
		Mat descL = new Mat();
		Mat descR = new Mat();
		Mat maskL = new Mat();
		Mat maskR = new Mat();
		orbLeft.detectAndCompute(left,maskL,vectorL,descL,false);
		orbLeft.detectAndCompute(right,maskR,vectorR,descR,false);
		DMatchVector vector = new DMatchVector();
		BFMatcher matcher = new BFMatcher(org.bytedeco.opencv.global.opencv_core.NORM_HAMMING,true);
		Mat output = new Mat();
		opencv_features2d.drawMatches(left,vectorL,right, vectorR,vector,output,1);
		OpencvUtils.byteDecoSaveImage(output, "c:\\temp\\"+box.getId()+"_"+m.getExecutionId()+"similar.jpg");
		orbLeft.close();
		orbRight.close();
		matcher.close();
		return vector.get().length;
	}


}
