package com.sumit.cv.cvcore.opencv.examples;

import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.Core;

import com.sumit.cv.cvcore.opencv.OpencvUtils;

public class DiffImageTest {
public static void main(String[] args) {
	 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	 String s1="c:/projects/images/chromeicon.jpg",s2="c:/projects/images/chromeicon - Copy.jpg",sdiff="c:/projects/images/chromeicon-Diff{0}.jpg";
	Mat im1 = OpencvUtils.byteDecoReadImage(s1);	
	Mat im2 = OpencvUtils.byteDecoReadImage(s2);
	int  count = OpencvUtils.getPixelDiffCount(im1, im2,sdiff,50);
	System.out.println("count: "+count);
}
}
