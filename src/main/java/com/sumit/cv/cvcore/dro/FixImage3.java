package com.sumit.cv.cvcore.dro;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FixImage3 {
	public static void main(String[] args) {
		System.loadLibrary("opencv_java341");
		Mat src = Imgcodecs.imread("d:\\meters\\meter2.png", Imgcodecs.IMREAD_COLOR);
		int nums = 6;//6 numbers on display
		int sx = 121,sy=451;
		int ox = 157,oy = 488;
		int dx = ox-sx;
		int dy  = oy-sy;
		int cx = sx,cy=sy;
		for(int i=1;i<=6;i++) {
			saveCutImage(src,""+i,cx,cy,dx,dy);
			cx = cx+dx;
		}
		int px = 115,py=212,px2 = 174,py2 = 262;
		saveCutImage("d:\\meters\\water-meter.png", "ar",px, py, px2-px, py2-py);
		Mat src2 = Imgcodecs.imread("d:\\meters\\part-ar.png", Imgcodecs.IMREAD_COLOR);
		Mat src3 = Imgcodecs.imread("d:\\meters\\part-ar2.png", Imgcodecs.IMREAD_COLOR);
		
		Mat src22 = new Mat();
		Mat src32 = new Mat();
		Imgproc.resize(src2, src22, new Size(64,64));

		Imgproc.resize(src3, src32, new Size(64,64));

		src2 = src22;
		src3 = src32;

		Imgcodecs.imwrite("d:\\meters\\out1.0.png",src2);
		Imgcodecs.imwrite("d:\\meters\\out2.0.png",src3);

		
		for(int i=0;i<10;i++)
			Imgproc.blur(src2, src2, new Size(3.0, 3.0));
		for(int i=0;i<10;i++)
			Imgproc.blur(src3, src3, new Size(3.0, 3.0));
		
		Imgcodecs.imwrite("d:\\meters\\out1.1.png",src2);
		Imgcodecs.imwrite("d:\\meters\\out2.1.png",src3);
		
		
		Mat dst = new Mat();
		Core.absdiff(src2, src3, dst);
		
		Imgcodecs.imwrite("d:\\meters\\out1.png",dst);
		for(int i=0;i<10;i++)
		Imgproc.blur(dst, dst, new Size(3.0, 3.0));
		Imgcodecs.imwrite("d:\\meters\\out.png",dst);
		int count = 0;
		int mx  = dst.rows()*dst.cols();
		for( int y = 0; y < dst.rows(); y++ ) 
        {
          for( int x = 0; x < dst.cols(); x++ ) 
          {
        	  double[] a = dst.get(y, x);
        	  boolean change = false;
        	  for(int i=0;i<a.length;i++) {
        		  if(a[i]!=0) {
        			  change = true;
        		  }
        	  }
        	  if(change) {
        		 count++; 
        	  }
          }
        }
		if(count>(mx/10)) {
			System.out.println("images are diff: "+count+" "+mx);
		}else {
			System.out.println("images are same: "+count+" "+mx	);
		}
		
	}
	public static void drawRotatedRect(Mat image, RotatedRect rotatedRect, 
			 Scalar color, int thickness) {
			      Point[] vertices = new Point[4];
			      rotatedRect.points(vertices);
			      MatOfPoint points = new MatOfPoint(vertices);
			      Imgproc.drawContours(image, Arrays.asList(points), -1, color, 
			      thickness);
			    }

	private static void saveCutImage(String s, String w, int cx, int cy, int dx, int dy) {
		Mat src = Imgcodecs.imread(s, Imgcodecs.IMREAD_COLOR);
		saveCutImage(src, w, cx, cy, dx, dy);
	}
	private static void saveCutImage(Mat img, String w, int cx, int cy, int dx, int dy) {
		 Rect r  = new Rect(cx,cy,dx,dy);
		 Mat image_roi = new Mat(img,r);
		 Imgcodecs.imwrite("d:\\meters\\part-"+w+".png",image_roi);
	}
	}
