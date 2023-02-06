package com.sumit.cv.cvcore.dro;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class ImageTest {
public static void main(String[] args) {
	System.loadLibrary("opencv_java341");
	Mat img = Imgcodecs.imread("d:\\IMG_20171208_130711.jpg", Imgcodecs.IMREAD_COLOR);
	Mat resizeimage = new Mat();
	Size sz = new Size(1000,1000);
	Imgproc.resize( img, resizeimage, sz );
	Rect r  = new Rect(0,0,500,500);
	 Mat image_roi = new Mat(img,r);
	 Imgcodecs.imwrite("d:\\resize_912.jpg",resizeimage);
	 Imgcodecs.imwrite("d:\\cropimage_912.jpg",image_roi);
	 Point center = new Point(500,500);
	 rotateImage(img,center);

}

private static void rotateImage(Mat img, Point center) {
	 Mat rotation_matrix = Imgproc.getRotationMatrix2D(center, 30.00, 1.0);
	 Mat destinationMat = new Mat();
	 Imgproc.warpAffine(img, destinationMat, rotation_matrix, img.size());
	 Imgcodecs.imwrite("d:\\rotateimage_912.jpg",destinationMat);
}
}
