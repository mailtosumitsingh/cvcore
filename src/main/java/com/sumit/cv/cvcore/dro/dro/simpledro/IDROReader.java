package com.sumit.cv.cvcore.dro.dro.simpledro;

import java.util.List;

import org.bytedeco.opencv.opencv_core.Mat;

public interface IDROReader {
	List<Integer> read(String imagePath);
	List<Integer> read(Mat img) ;
	List<Integer> read(Mat img,String imageName);

}
