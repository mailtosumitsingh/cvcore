package com.sumit.cv.cvcore.dro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FixImage {
	public static void main(String[] args) {
		System.loadLibrary("opencv_java341");
		Mat src = Imgcodecs.imread("d:\\meters\\meter2.png", Imgcodecs.IMREAD_COLOR);
		int nums = 6;// 6 numbers on display
		int sx = 121, sy = 442;
		int ox = 157, oy = 488;
		int dx = ox - sx;
		int dy = oy - sy;
		int cx = sx, cy = sy;
		for (int i = 1; i <= 6; i++) {
			saveCutImage(src, "" + i, cx, cy, dx, dy);
			cx = cx + dx;
		}
		int px = 115, py = 212, px2 = 174, py2 = 262;
		saveCutImage("d:\\meters\\water-meter.png", "ar", px, py, px2 - px, py2 - py);
		Mat src2 = Imgcodecs.imread("d:\\meters\\part-ar.png", Imgcodecs.IMREAD_COLOR);
		Mat src21 = new Mat();
		Imgproc.cvtColor(src2, src21, Imgproc.COLOR_BGR2HSV);
		Imgcodecs.imwrite("d:\\meters\\countour2.png", src21);
		List<Mat> mv = new ArrayList<Mat>();
		Core.split(src21, mv);

		Mat shiftedH = mv.get(0).clone();
		int shift = 25; // in openCV hue values go from 0 to 180 (so have to be doubled to get to 0 ..
						// 360) because of byte range from 0 to 255

		Mat src3 = new Mat();
		Imgproc.threshold(src2, src3, 100, 255, Imgproc.THRESH_BINARY);
		src2 = src3;
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat countour_result = Mat.zeros(src2.size(), CvType.CV_8UC3);
		Imgproc.findContours(src2, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(countour_result, contours, -1, new Scalar(100, 100, 255));
		for (MatOfPoint contour : contours)
			Imgproc.fillPoly(countour_result, Arrays.asList(contour), new Scalar(255, 255, 255));

		Scalar green = new Scalar(81, 190, 0);

		for (MatOfPoint contour : contours) {
			RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
			drawRotatedRect(countour_result, rotatedRect, green, 4);
		}
		Imgcodecs.imwrite("d:\\meters\\countour.png", countour_result);

	}

	public static void drawRotatedRect(Mat image, RotatedRect rotatedRect, Scalar color, int thickness) {
		Point[] vertices = new Point[4];
		rotatedRect.points(vertices);
		MatOfPoint points = new MatOfPoint(vertices);
		Imgproc.drawContours(image, Arrays.asList(points), -1, color, thickness);
	}

	private static void saveCutImage(String s, String w, int cx, int cy, int dx, int dy) {
		Mat src = Imgcodecs.imread(s, Imgcodecs.IMREAD_COLOR);
		saveCutImage(src, w, cx, cy, dx, dy);
	}

	private static void saveCutImage(Mat img, String w, int cx, int cy, int dx, int dy) {
		Rect r = new Rect(cx, cy, dx, dy);
		Mat image_roi = new Mat(img, r);
		Imgcodecs.imwrite("d:\\meters\\part-" + w + ".png", image_roi);
	}
}
