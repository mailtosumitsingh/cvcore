package com.sumit.cv.cvcore.opencv;

import static org.bytedeco.opencv.global.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.opencv.global.opencv_core.cvAbsDiff;
import static org.bytedeco.opencv.global.opencv_core.cvCopy;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvSetImageROI;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.helper.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.CvRect;
import org.bytedeco.opencv.opencv_core.CvScalar;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RotatedRect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_imgproc.CvFont;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

public class OpencvUtils {
	public static final Map<Integer, Scalar> colorMap = new HashMap<Integer, Scalar>();
	public static final Scalar white = rgb(255, 255, 255);
	public static final Scalar black = rgb(0, 0, 0);
	public static final Scalar gray = rgb(128, 128, 128);
	public static final Scalar red = rgb(255, 0, 0);
	public static final Scalar blue = rgb(0, 0, 255);
	public static final Scalar green = rgb(0, 255, 0);
	public static final Scalar yellow = rgb(255, 255, 0);
	public static final Scalar lightBlue = rgb(0, 255, 255);
	public static final Scalar magenta = rgb(255, 0, 255);
	public static final Scalar pink = rgb(0xFF, 0x66, 0xFF);
	public static final Scalar orange = rgb(0xFF, 0x99, 0x33);

	public static final CvScalar cvwhite = cvrgb(255, 255, 255);
	public static final CvScalar cvblack = cvrgb(0, 0, 0);
	public static final CvScalar cvgray = cvrgb(128, 128, 128);
	public static final CvScalar cvred = cvrgb(255, 0, 0);
	public static final CvScalar cvblue = cvrgb(0, 0, 255);
	public static final CvScalar cvgreen = cvrgb(0, 255, 0);
	public static final CvScalar cvyellow = cvrgb(255, 255, 0);
	public static final CvScalar cvlightBlue = cvrgb(0, 255, 255);
	public static final CvScalar cvmagenta = cvrgb(255, 0, 255);
	public static final CvScalar cvpink = cvrgb(0xFF, 0x66, 0xFF);
	public static final CvScalar cvorange = cvrgb(0xFF, 0x99, 0x33);

	static {
		colorMap.put(0, white);
		colorMap.put(1, black);
		colorMap.put(2, gray);
		colorMap.put(3, red);
		colorMap.put(4, blue);
		colorMap.put(5, green);
		colorMap.put(6, yellow);
		colorMap.put(7, lightBlue);
		colorMap.put(8, magenta);
		colorMap.put(9, pink);
		colorMap.put(10, orange);
	}
	static OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
	static OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();

	public static void drawRect(Mat image, Point p1, Point p2, Scalar color) {
		opencv_imgproc.rectangle(image, p1, p2, color);
	}

	public static void saveCutImage(String s, String w, int cx, int cy, int dx, int dy) {
		Mat src = org.bytedeco.opencv.global.opencv_imgcodecs.imread(s,
				org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR);
		Mat mat = getImageRoiRect(src, cx, cy, dx, dy);
		org.bytedeco.opencv.global.opencv_imgcodecs.imwrite(w, mat);
	}

	public static Mat byteDecoReadImage(String s) {
		return org.bytedeco.opencv.global.opencv_imgcodecs.imread(s,
				org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR);
	}

	public static void byteDecoSaveImage(Mat src, String name) {
		org.bytedeco.opencv.global.opencv_imgcodecs.imwrite(name, src);
	}

	public static int getPixelDiffCount(Mat src2, Mat src3, String tempImg, int threashold) {
		Mat dst = new Mat();
		if (tempImg != null) {
			byteDecoSaveImage(src2, tempImg.replace("{0}", "left"));
			byteDecoSaveImage(src3, tempImg.replace("{0}", "right"));

		}
		opencv_core.absdiff(src2, src3, dst);
		if (tempImg != null)
			byteDecoSaveImage(dst, tempImg.replace("{0}", "diff"));
		Indexer indexer = dst.createIndexer();
		int count = 0;
		for (int x = 0; x < indexer.sizes()[0]; x++) {
			for (int y = 0; y < indexer.sizes()[1]; y++) {
				double b = indexer.getDouble(x, y, 2);
				double g = indexer.getDouble(x, y, 1);
				double r = indexer.getDouble(x, y, 0);
				if (b + g + r > threashold) {
					System.out.println("Diff: " + (b + g + r));
					count++;
					indexer.putDouble(new long[] { x, y, 0 }, 255);
					indexer.putDouble(new long[] { x, y, 1 }, 255);
					indexer.putDouble(new long[] { x, y, 2 }, 255);
				}
			}
		}

		return count;
	}

	public static int getPixelDiffCountGray(Mat src2, Mat src3, String tempImg, int threashold) {
		Mat dst = new Mat();
		if (tempImg != null) {
			byteDecoSaveImage(src2, tempImg.replace("{0}", "countGrayleft"));
			byteDecoSaveImage(src3, tempImg.replace("{0}", "countGrayright"));

		}
		opencv_core.absdiff(src2, src3, dst);
		if (tempImg != null)
			byteDecoSaveImage(dst, tempImg.replace("{0}", "diff"));
		Indexer indexer = dst.createIndexer();
		int count = 0;
		for (int x = 0; x < indexer.sizes()[0]; x++) {
			for (int y = 0; y < indexer.sizes()[1]; y++) {
				double g = indexer.getDouble(x, y, 0);
				if (g > threashold) {
					System.out.println("Diff: " + (g));
					count++;
					indexer.putDouble(new long[] { x, y, 0 }, 255);
				}
			}
		}
		if (tempImg != null)
			byteDecoSaveImage(dst, tempImg.replace("{0}", "diffout"));
		return count;
	}

	public static Mat getImageRoiRect(Mat img, int x, int y, int w, int h) {
		Rect r = new Rect(x, y, w, h);
		Mat image_roi = new Mat(img, r);
		return image_roi;
	}

	public static Mat rotateImage(Mat img, org.bytedeco.opencv.opencv_core.Point2f center, double angle) {
		Mat rotation_matrix = opencv_imgproc.getRotationMatrix2D(center, angle, 1.0);
		Mat destinationMat = new Mat();
		opencv_imgproc.warpAffine(img, destinationMat, rotation_matrix, img.size());
		return destinationMat;
	}

	public static IplImage resizeImage(IplImage orig, int wnew, int hnew) {
		IplImage img = clone(orig);
		IplImage clone = IplImage.create(wnew, hnew, img.depth(), img.nChannels());
		Mat imgMat = iplConverter.convertToMat(iplConverter.convert(orig));
		Mat cloneMat = iplConverter.convertToMat(iplConverter.convert(img));

		opencv_imgproc.resize(imgMat, cloneMat, new org.bytedeco.opencv.opencv_core.Size(wnew, hnew));
		return iplConverter.convertToIplImage(iplConverter.convert(imgMat));
	}

	public static void resizeImage(String name, String to, int x, int y) {
		IplImage i = loadColorImage(name);
		IplImage i2 = resizeImage(i, x, y);
		saveImage(to, i2);
	}

	public static Scalar rgb(double r, double g, double b) {
		return new Scalar(b, g, r, 0);
	}

	public static CvScalar cvrgb(double r, double g, double b) {
		CvScalar cv = new CvScalar();
		cv.red(r);
		cv.green(g);
		cv.blue(b);
		return cv;
	}

	public static int getPercentageChangeBinary(String first, String second) {
		IplImage orig = loadColorImage(first);
		IplImage imglast = loadColorImage(second);
		return getPercentageChangeBinary(orig, imglast);

	}

	public static int getTotalChangeBinary(String first, String second) {
		IplImage orig = loadColorImage(first);
		IplImage imglast = loadColorImage(second);
		return getTotalChangeBinary(orig, imglast);

	}

	public static IplImage loadColorImage(String fileName) {
		return opencv_imgcodecs.cvLoadImage(fileName, org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR);
	}

	public static IplImage loadGrayScaleImage(String fileName) {
		return opencv_imgcodecs.cvLoadImage(fileName, org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE);
	}

	public static int getPercentageChangeBinary(IplImage orig, IplImage imglast) {
		IplImage img = clone(orig);
		gaussSmooth(imglast, 9, 9, 2, 2);
		gaussSmooth(img, 9, 9, 2, 2);
		img = convertToGrayImage(img);
		imglast = convertToGrayImage(imglast);
		IplImage diffImg = createGrayImage(img);
		absDiff(imglast, img, diffImg);
		convertToBinImage(diffImg, 3, 255);
		int hist = calculateWhitePixelCount(diffImg);
		int percentageChange = calculatePercChange(diffImg, hist);
		return percentageChange;
	}

	public static int getTotalChangeBinary(IplImage orig, IplImage imglast) {
		IplImage img = clone(orig);
		gaussSmooth(imglast, 9, 9, 2, 2);
		gaussSmooth(img, 9, 9, 2, 2);
		img = convertToGrayImage(img);
		imglast = convertToGrayImage(imglast);

		IplImage diffImg = createGrayImage(img);
		absDiff(imglast, img, diffImg);
		convertToBinImage(diffImg, 3, 255);
		int hist = calculateWhitePixelCount(diffImg);
		return hist;
	}

	public static void cropImage(String name, String to, Rectangle r) {
		IplImage i = loadColorImage(name);
		IplImage i2 = cropImage(i, r);
		saveImage(to, i2);
	}

	public static IplImage cropImage(IplImage orig, Rectangle r) {
		IplImage img = clone(orig);
		CvRect rect = new CvRect();
		IplImage clone = IplImage.create(r.width, r.height, img.depth(), img.nChannels());
		cvSetImageROI(img, rect);
		cvCopy(img, clone);
		return clone;
	}

	public static void saveImage(String name, IplImage img) {
		opencv_imgcodecs.cvSaveImage(name, img);
	}

	public String getDateString() {
		DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		String d = format.format(new Date());
		return d;
	}

	public CanvasFrame createCanvas(String name) {
		CanvasFrame canvas = new CanvasFrame(name);
		return canvas;
	}

	public static void showImage(CanvasFrame canvas, IplImage orig) {
		canvas.showImage(iplConverter.convert(orig));
	}

	public static void setText(IplImage orig, String perc, CvFont font, int x, int y, CvScalar color) {
		opencv_imgproc.cvPutText(orig, perc, cvPoint(x, y), font, color);
	}

	public static CvFont getFont(double fontScale, int thickness) {
		CvFont font = new CvFont();
		int fontFace = opencv_imgproc.FONT_HERSHEY_SIMPLEX;
		opencv_imgproc.cvInitFont(font, fontFace, fontScale, fontScale, 0, thickness, opencv_imgproc.CV_AA);
		return font;
	}

	public static int calculatePercChange(IplImage diffImg, int hist) {
		int percentageChange = hist * 100 / (diffImg.height() * diffImg.width());
		return percentageChange;
	}

	public static int calculateWhitePixelCount(IplImage diffImg) {
		int hist = 0;
		ByteBuffer buffer = diffImg.getByteBuffer();
		for (int y = 0; y < diffImg.height(); y++) {
			for (int x = 0; x < diffImg.width(); x++) {
				int index = y * diffImg.widthStep() + x * diffImg.nChannels();
				int value = buffer.get(index) & 0xFF;
				if (value > 0) {
					hist++;
				}

			}
		}
		return hist;
	}

	public void showPercChangeImage(IplImage img, int percentageChange, CanvasFrame canvas) {
		IplImage orig = clone(img);
		String perc = percentageChange + "% Percent.";
		System.out.println("Perc Movement: " + perc);
		CvFont font = getFont(1.5, 2);
		if (percentageChange < 5) {
			setText(orig, perc, font, 20, 60, cvgreen);
		} else {
			setText(orig, perc, font, 20, 60, cvred);
		}

		String d = getDateString();
		setText(orig, d, font, 20, 180, cvyellow);
		showImage(canvas, orig);
	}

	public static IplImage convertToGrayImage(IplImage imglast) {
		IplImage grayImglast = createGrayImage(imglast);
		toGrayImage(imglast, grayImglast);
		return grayImglast;
	}

	public static void convertToBinImage(IplImage diffImg, double threshold, double maxVal) {
		opencv_imgproc.cvThreshold(diffImg, diffImg, threshold, maxVal, opencv_imgproc.CV_THRESH_BINARY);
	}

	public static void convertToBinImage2(IplImage diffImg, double threshold, double maxVal) {
		opencv_imgproc.cvThreshold(diffImg, diffImg, threshold, maxVal,
				opencv_imgproc.CV_THRESH_BINARY | opencv_imgproc.CV_THRESH_OTSU);
	}

	public static void absDiff(IplImage imglast, IplImage grayImg, IplImage diffImg) {
		cvAbsDiff(grayImg, imglast, diffImg);
	}

	public static void convertToBinImage(Mat diffImg, double threshold, double maxVal) {
		opencv_imgproc.threshold(diffImg, diffImg, threshold, maxVal, opencv_imgproc.CV_THRESH_BINARY);
	}

	public static void convertToBinImage2(Mat diffImg, double threshold, double maxVal) {
		opencv_imgproc.threshold(diffImg, diffImg, threshold, maxVal,
				opencv_imgproc.CV_THRESH_BINARY | opencv_imgproc.CV_THRESH_OTSU);
	}

	public static void absDiff(Mat imglast, Mat grayImg, Mat diffImg) {
		opencv_core.absdiff(grayImg, imglast, diffImg);
	}

	public static void toGrayImage(IplImage img, IplImage grayImg) {
		opencv_imgproc.cvCvtColor(img, grayImg, opencv_imgproc.COLOR_BGR2GRAY);
	}

	public static void toGrayImage(Mat img, Mat grayImg) {
		opencv_imgproc.cvtColor(img, grayImg, opencv_imgproc.COLOR_BGR2GRAY);
	}

	public static IplImage createGrayImage(IplImage img) {
		IplImage grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
		return grayImg;
	}

	public static void gaussSmooth(IplImage img, int apWidth, int apHeight, int colorSigma, int spatSigma) {
		opencv_imgproc.cvSmooth(img, img, spatSigma, apWidth, apHeight, colorSigma, spatSigma);
	}

	public static void gaussSmooth(Mat img, Size ksize, int sigX, int sigY, int borderW) {
		opencv_imgproc.GaussianBlur(img, img, ksize, sigX, sigY, borderW);
	}

	public static IplImage clone(IplImage img) {
		IplImage orig;
		orig = img.clone();
		return orig;
	}

	public static void findContours(Mat in, MatVector vector) {
		opencv_imgproc.findContours(in, vector, opencv_imgproc.CV_RETR_TREE, opencv_imgproc.CV_CHAIN_APPROX_NONE);
		for (Mat v : vector.get()) {
			double val = opencv_imgproc.contourArea(v);
			RotatedRect box = new RotatedRect();
			Point2f center = new Point2f();
			FloatPointer radius = new FloatPointer();
			opencv_imgproc.minEnclosingCircle(v, center, radius);
			opencv_imgproc.boxPoints(box, v);
			System.out.println(box.boundingRect());
			System.out.println(val);
			System.out.println("Circle:" + center + ": " + radius.get());
		}
	}

	public static void saveImage(Mat src, String name) {
		org.bytedeco.opencv.global.opencv_imgcodecs.imwrite(name, src);
	}

	public static Mat readColorImage(String name) {
		Mat img = org.bytedeco.opencv.global.opencv_imgcodecs.imread(name, IMREAD_COLOR);
		return img;
	}

	public static Mat resizePerct(Mat in, int xPerct, int yPerct) {
		Mat ret = new Mat();
		int x = (in.rows() * xPerct) / 100;
		int y = (in.cols() * yPerct) / 100;
		opencv_imgproc.resize(in, ret, new Size(x, y));
		return ret;
	}

	public static Mat resize(Mat in, int x, int y) {
		Mat ret = new Mat();
		opencv_imgproc.resize(in, ret, new Size(x, y));
		return ret;
	}

	public static Mat canny(Mat in, int low, int high) {
		Mat ret = in.clone();
		opencv_imgproc.Canny(in, ret, low, high);
		return ret;
	}

	public static Mat threashold(Mat in, int t1, int t2) {
		Mat dst = new Mat();
		opencv_imgproc.threshold(in, dst, t1, t2, Imgproc.THRESH_BINARY);
		return dst;
	}

	public static void saveImage(String dst, Mat img) {
		org.bytedeco.opencv.global.opencv_imgcodecs.imwrite(dst, img);
	}

	public static void movToFrames(String file, String output) {
		VideoCapture vc = new VideoCapture();
		System.out.println(new File(file).exists());
		if (vc.open(file)) {
			System.out.println("Success");
		} else {
			System.out.println("Failure");
		}
		int frameCount = (int) vc.get(Videoio.CAP_PROP_FRAME_COUNT);
		int fps = (int) vc.get(Videoio.CAP_PROP_FPS);
		int framePos = (int) vc.get(Videoio.CAP_PROP_POS_FRAMES);

		Mat frame = new Mat();

		if (vc.isOpened()) {
			System.out.println("Video is opened");
			System.out.println("Number of Frames: " + frameCount);
			System.out.println(fps + " Frames per Second");
			System.out.println("Converting Video...");

			vc.read(frame);

			while (framePos <= frameCount) {
				org.bytedeco.opencv.global.opencv_imgcodecs.imwrite(output + "/" + framePos + ".jpg", frame);
				framePos++;
				vc.read(frame);
			}
			vc.release();

			System.out.println(frameCount + " Frames extracted");

		}
	}

	public static boolean colorMatch(double[] a, Scalar testColor, Scalar colorMatchTolerance) {
		if ((Math.abs(a[0] - testColor.get(0)) < colorMatchTolerance.get(0))
				&& (Math.abs(a[1] - testColor.get(1)) < colorMatchTolerance.get(1))
				&& (Math.abs(a[2] - testColor.get(2)) < colorMatchTolerance.get(2)))
			return true;

		return false;
	}
}
