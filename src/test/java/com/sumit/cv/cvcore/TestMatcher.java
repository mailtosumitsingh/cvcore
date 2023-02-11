package com.sumit.cv.cvcore;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.jupiter.api.Test;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.opencv.Model;
import com.sumit.cv.cvcore.opencv.OpencvUtils;
import com.sumit.cv.cvcore.opencv.matchers.BinMatcher;

public class TestMatcher {
	@Test
public void test() {
		BinMatcher matcher= new BinMatcher();
	Mat _1 = OpencvUtils.readColorImage("c:\\temp\\default.jpg");
	Mat _2 = OpencvUtils.readColorImage("c:\\temp\\default1.jpg");
	Model m = new Model();
	m.setExecutionId("123");
	AnonDefObj box = new AnonDefObj();
	box.setId("a");
	box.setX(200);
	box.setY(200);
	box.setR(200);
	box.setB(200);
	double score = matcher.score(_1, _2, box, m);
	System.out.println(score);
	}
}
