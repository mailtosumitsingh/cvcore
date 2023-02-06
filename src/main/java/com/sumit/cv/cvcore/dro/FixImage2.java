package com.sumit.cv.cvcore.dro;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class FixImage2 {
	public static void main(String[] args) {
		System.loadLibrary("opencv_java341");
		Mat src = Imgcodecs.imread("d:\\meters\\meter2.png", Imgcodecs.IMREAD_COLOR);
		Mat dst = new Mat();
		double threshold = Imgproc.threshold(src, dst, 123, 255, Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite("d:\\meters\\meter.jpg", dst);
		System.out.println(threshold); // 117
		int nums = 6;//6 numbers on display
		int sx = 122,sy=442;
		int ox = 156,oy = 488;
		int dx = ox-sx;
		int dy  = oy-sy;
		int cx = sx,cy=sy;
		for(int i=1;i<=6;i++) {
			saveCutImage(src,i,cx,cy,dx,dy);
			cx = cx+dx;
		}
	}

	private static void saveCutImage(Mat img, int i, int cx, int cy, int dx, int dy) {
		 Rect r  = new Rect(0,0,500,500);
		 Mat image_roi = new Mat(img,r);
		 Imgcodecs.imwrite("d:\\meters\\part-"+i+".jpg",image_roi);
	}
	}
