package com.sumit.cv.cvcore.dro;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Scalar;

import com.google.common.collect.Lists;
import com.sumit.cv.cvcore.dro.dro.simpledro.DynaModelSupport;
import com.sumit.cv.cvcore.dro.dro.simpledro.IDROReader;
import com.sumit.cv.cvcore.dro.dro.simpledro.SimpleDigit;
import com.sumit.cv.cvcore.dro.dro.simpledro.SimpleModel;
import com.sumit.cv.cvcore.dro.dro.simpledro.SimpleSegment;
import com.sumit.cv.cvcore.opencv.OpencvUtils;

public class ReadDROOverHTTP  implements IDROReader,DynaModelSupport {
	private static boolean DEBUG = true, TRACE = true;
	public static String base = "";
	public SimpleModel dynaModel = null; 

	public SimpleModel getDynaModel() {
		return dynaModel;
	}

	public void setDynaModel(SimpleModel dynaModel) {
		this.dynaModel = dynaModel;
	}

	public List<Integer> handle(String imageName) throws Exception {
		List<Integer> lst = Lists.newLinkedList();
		if (imageName != null) {
			lst = read(imageName);
		} else {
			Mat img = getFromCamera();
			imageName = base + File.separator + "uploaded" + File.separator + "extraimages" + File.separator + "ReadDROOverHTTP.jpg";
			lst = evaluateInternal(getModel(), img, imageName);
		}
		return lst;
	}

	private Mat getFromCamera() {
		return null;
	}


	private  List<Integer> evaluate(SimpleModel model, String imageName) {
		Mat img = org.bytedeco.opencv.global.opencv_imgcodecs.imread(imageName, org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR);
		return evaluateInternal(model, img, imageName);
	}
	private  List<Integer> evaluateInternal(SimpleModel model, Mat img, String imageName) {
		List<Integer> ret = new java.util.LinkedList<>();
		
		if (Math.abs(model.getDisplayAngle() - 0) > .0001) {
			img = OpencvUtils.rotateImage(img, new Point2f(0, 0), model.getDisplayAngle());
		}
		Mat imgDebug = img.clone();
		if (DEBUG) {
			OpencvUtils.drawRect(imgDebug, new Point(model.getX(), model.getY()), new Point(model.getX() + model.getW(), model.getY() + model.getH()), new Scalar(255, 255, 255, 1));
		}
		int[][] val = new int[model.getDigits().size()][7];
		for (int digitScroll = 0; digitScroll < model.getDigits().size(); digitScroll++) {
			
			
			SimpleDigit d = model.getDigits().get(digitScroll);
			int digitLabel = Integer.parseInt(d.getLabel());
			System.out.println("Now evaluating digit: " + digitLabel);
			if (DEBUG) {
				Point digitStartLoc = new Point(d.getX(), d.getY());
				OpencvUtils.drawRect(imgDebug, digitStartLoc, new Point(d.getW(), d.getH()), new Scalar(255, 0, 128, .4));
				opencv_imgproc.putText(imgDebug, d.getLabel(), digitStartLoc, 1, 1, new Scalar(128, 0, 128,1));
			}
			Mat digitMat = OpencvUtils.getImageRoiRect(img, d.getX(), d.getY(), d.getW(), d.getH());
			for (int k = 0; k < d.getSegments().size(); k++) {
				SimpleSegment rr = d.getSegments().get(k);
				if (DEBUG) {
					Point startLoc = new Point(d.getX() + rr.getX(), d.getY() + rr.getY());
					OpencvUtils.drawRect(imgDebug, startLoc, new Point(d.getX() + rr.getX() + rr.getW(), d.getY() + rr.getY() + rr.getH()), new Scalar(128, 128, 128, 1));
					opencv_imgproc.putText(imgDebug, ">" + rr.getLabel(), startLoc, 1, 1, new Scalar(128, 0, 128,1));
				}
				Mat img2 = OpencvUtils.getImageRoiRect(img, (int) (d.getX() + rr.getX()), (int) (d.getY() + rr.getY()), (int) rr.getW(), (int) rr.getH());
				double count = img2.rows() * img2.cols();
				double countMatch = 0;
				Indexer indexer = img2.createIndexer();
				for (int x = 0; x < indexer.sizes()[0]; x++) {
			        for (int y = 0; y < indexer.sizes()[1]; y++) {
							double[] a = new double[3];
							a[2]= indexer.getDouble(x, y, 2) ;
			                a[1]= indexer.getDouble(x, y, 1) ;
			                a[0]= indexer.getDouble(x, y, 0) ;	  
							if(OpencvUtils.colorMatch(a,model.getDisplayColor(),model.getColorMatchTolerance())) {
								countMatch++;
							}
							
					}	
				}
				String status = "off";
				double matchPerc = countMatch;
				if (matchPerc > model.getModelSegmentClusterTolerance()) {
					status = "on";
					val[digitLabel][k] = 1;
				} else {
					status = "off";
					val[digitLabel][k] = 0;
				}
				if (TRACE) {
					System.out.println(digitLabel + "(" + k + ")" + " is  " + status+  " "+val[digitLabel][k]+ " " + matchPerc + " tol:  " + model.getModelSegmentClusterTolerance());
				}
			}
		}

		Mat view = OpencvUtils.getImageRoiRect(imgDebug, model.getX(), model.getY(), model.getW(), model.getH());
		OpencvUtils.saveImage(imgDebug, imageName + "_changed.jpg");
		for (int i = 0; i < model.getDigits().size(); i++) {
			int numApprox = numApprox(val[i]);
			ret.add(numApprox);
			System.out.println(Arrays.toString(val[i]) + " : " + numApprox);
		}
		return ret;
	}

	static public int num(int[] v) {
		int[] z0 = new int[] { 1, 1, 1, 1, 1, 1, 0 };
		int[] z1 = new int[] { 0, 1, 1, 0, 0, 0, 0 };
		int[] z2 = new int[] { 1, 1, 0, 1, 1, 0, 1 };
		int[] z3 = new int[] { 1, 1, 1, 1, 0, 0, 1 };
		int[] z4 = new int[] { 0, 1, 1, 0, 0, 1, 1 };
		int[] z5 = new int[] { 1, 0, 1, 1, 0, 1, 1 };
		int[] z6 = new int[] { 0, 0, 1, 1, 1, 1, 1 };
		int[] z7 = new int[] { 1, 1, 1, 0, 0, 0, 0 };
		int[] z8 = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		int[] z9 = new int[] { 1, 1, 1, 1, 0, 1, 1 };
		int[] sign = new int[] { 0, 0, 0, 0, 0, 0, 1 };
		int[][] m = new int[11][];
		m[0] = z0;
		m[1] = z1;
		m[2] = z2;
		m[3] = z3;
		m[4] = z4;
		m[5] = z5;
		m[6] = z6;
		m[7] = z7;
		m[8] = z8;
		m[9] = z9;
		m[10] = sign;
		int c = 0;
		for (int[] mm : m) {
			boolean match = true;
			for (int i = 0; i < 7; i++) {
				if (v[i] != mm[i])
					match = false;
			}
			if (match)
				return c > 9 ? -1 : c;
			c++;
		}
		return 10;
	}

	static public int numApprox(int[] v) {
		int[] z0 = new int[] { 1, 1, 1, 1, 1, 1, 0 };
		int[] z1 = new int[] { 0, 1, 1, 0, 0, 0, 0 };
		int[] z2 = new int[] { 1, 1, 0, 1, 1, 0, 1 };
		int[] z3 = new int[] { 1, 1, 1, 1, 0, 0, 1 };
		int[] z4 = new int[] { 0, 1, 1, 0, 0, 1, 1 };
		int[] z5 = new int[] { 1, 0, 1, 1, 0, 1, 1 };
		int[] z6 = new int[] { 0, 0, 1, 1, 1, 1, 1 };
		int[] z7 = new int[] { 1, 1, 1, 0, 0, 0, 0 };
		int[] z8 = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		int[] z9 = new int[] { 1, 1, 1, 1, 0, 1, 1 };
		int[] sign = new int[] { 0, 0, 0, 0, 0, 0, 1 };
		int[][] m = new int[11][];
		m[0] = z0;
		m[1] = z1;
		m[2] = z2;
		m[3] = z3;
		m[4] = z4;
		m[5] = z5;
		m[6] = z6;
		m[7] = z7;
		m[8] = z8;
		m[9] = z9;
		m[10] = sign;
		int c = 0;
		int[] cc = new int[11];
		for (int[] mm : m) {
			int count = 0;
			for (int i = 0; i < 7; i++) {
				if (v[i] == mm[i])
					count++;
			}
			cc[c] = count;
			c++;
		}
		int max = 0;
		int idx = -1;
		for (int i = 0; i < 11; i++) {
			if (cc[i] > max) {
				max = cc[i];
				idx = i;
			} else if (cc[i] == max) {
				if (idx > -1 && flags(m[i]) > flags(m[idx])) {
					idx = i;
				}
			}
		}

		return idx;
	}

	private static int flags(int[] is) {
		int count = 0;
		for (int i : is) {

			if (i == 1)
				count++;
		}
		return count;
	}

	
	private  SimpleModel getModel() {
		if(dynaModel!=null) return dynaModel;
		SimpleModel model = new SimpleModel();
		int xoff = 5, yoff = 153;
		model.setX(398 + xoff);
		model.setY(173 + yoff);
		model.setW(413);
		model.setH(51);
		model.setDisplayAngle(0);
		model.setDigitWidth(43);
		model.setDigitHeight(56);
		model.setDisplayColor(new Scalar(251, 255, 249, 1));
		model.setColorMatchTolerance(new Scalar(50, 50, 30, 1));
		model.setModelSegmentClusterTolerance(4);

		SimpleDigit digit0 = new SimpleDigit(404, 142, 34, 66, 0);
		SimpleSegment sm00 = new SimpleSegment(15, 13, 6, 6, 0);
		digit0.getSegments().set(0, sm00);
		SimpleSegment sm01 = new SimpleSegment(27, 26, 6, 6, 1);
		digit0.getSegments().set(1, sm01);
		SimpleSegment sm02 = new SimpleSegment(25, 45, 6, 6, 2);
		digit0.getSegments().set(2, sm02);
		SimpleSegment sm03 = new SimpleSegment(12, 55, 6, 6, 3);
		digit0.getSegments().set(3, sm03);
		SimpleSegment sm04 = new SimpleSegment(4, 47, 6, 6, 4);
		digit0.getSegments().set(4, sm04);
		SimpleSegment sm05 = new SimpleSegment(6, 24, 6, 6, 5);
		digit0.getSegments().set(5, sm05);
		SimpleSegment sm06 = new SimpleSegment(14, 35, 6, 6, 6);
		digit0.getSegments().set(6, sm06);
		model.getDigits().add(digit0);
		SimpleDigit digit1 = new SimpleDigit(452, 148, 41, 60, 1);
		SimpleSegment sm10 = new SimpleSegment(23, 6, 5, 5, 0);
		digit1.getSegments().set(0, sm10);
		SimpleSegment sm11 = new SimpleSegment(31, 14, 5, 5, 1);
		digit1.getSegments().set(1, sm11);
		SimpleSegment sm12 = new SimpleSegment(30, 41, 6, 6, 2);
		digit1.getSegments().set(2, sm12);
		SimpleSegment sm13 = new SimpleSegment(17, 49, 6, 6, 3);
		digit1.getSegments().set(3, sm13);
		SimpleSegment sm14 = new SimpleSegment(7, 43, 6, 6, 4);
		digit1.getSegments().set(4, sm14);
		SimpleSegment sm15 = new SimpleSegment(12, 16, 6, 6, 5);
		digit1.getSegments().set(5, sm15);
		SimpleSegment sm16 = new SimpleSegment(19, 29, 6, 6, 6);
		digit1.getSegments().set(6, sm16);
		model.getDigits().add(digit1);
		SimpleDigit digit2 = new SimpleDigit(503, 144, 46, 62, 2);
		SimpleSegment sm20 = new SimpleSegment(21, 11, 6, 6, 0);
		digit2.getSegments().set(0, sm20);
		SimpleSegment sm21 = new SimpleSegment(31, 23, 6, 6, 1);
		digit2.getSegments().set(1, sm21);
		SimpleSegment sm22 = new SimpleSegment(30, 42, 6, 6, 2);
		digit2.getSegments().set(2, sm22);
		SimpleSegment sm23 = new SimpleSegment(17, 52, 6, 6, 3);
		digit2.getSegments().set(3, sm23);
		SimpleSegment sm24 = new SimpleSegment(7, 43, 6, 6, 4);
		digit2.getSegments().set(4, sm24);
		SimpleSegment sm25 = new SimpleSegment(10, 20, 6, 6, 5);
		digit2.getSegments().set(5, sm25);
		SimpleSegment sm26 = new SimpleSegment(19, 34, 6, 6, 6);
		digit2.getSegments().set(6, sm26);
		model.getDigits().add(digit2);
		SimpleDigit digit3 = new SimpleDigit(557, 140, 46, 62, 3);
		SimpleSegment sm30 = new SimpleSegment(21, 11, 6, 6, 0);
		digit3.getSegments().set(0, sm30);
		SimpleSegment sm31 = new SimpleSegment(31, 23, 6, 6, 1);
		digit3.getSegments().set(1, sm31);
		SimpleSegment sm32 = new SimpleSegment(30, 42, 6, 6, 2);
		digit3.getSegments().set(2, sm32);
		SimpleSegment sm33 = new SimpleSegment(17, 52, 6, 6, 3);
		digit3.getSegments().set(3, sm33);
		SimpleSegment sm34 = new SimpleSegment(7, 43, 6, 6, 4);
		digit3.getSegments().set(4, sm34);
		SimpleSegment sm35 = new SimpleSegment(10, 20, 6, 6, 5);
		digit3.getSegments().set(5, sm35);
		SimpleSegment sm36 = new SimpleSegment(19, 34, 6, 6, 6);
		digit3.getSegments().set(6, sm36);
		model.getDigits().add(digit3);
		SimpleDigit digit4 = new SimpleDigit(611, 139, 46, 62, 4);
		SimpleSegment sm40 = new SimpleSegment(21, 11, 6, 6, 0);
		digit4.getSegments().set(0, sm40);
		SimpleSegment sm41 = new SimpleSegment(31, 23, 6, 6, 1);
		digit4.getSegments().set(1, sm41);
		SimpleSegment sm42 = new SimpleSegment(30, 42, 6, 6, 2);
		digit4.getSegments().set(2, sm42);
		SimpleSegment sm43 = new SimpleSegment(17, 52, 6, 6, 3);
		digit4.getSegments().set(3, sm43);
		SimpleSegment sm44 = new SimpleSegment(7, 43, 6, 6, 4);
		digit4.getSegments().set(4, sm44);
		SimpleSegment sm45 = new SimpleSegment(10, 20, 6, 6, 5);
		digit4.getSegments().set(5, sm45);
		SimpleSegment sm46 = new SimpleSegment(19, 34, 6, 6, 6);
		digit4.getSegments().set(6, sm46);
		model.getDigits().add(digit4);
		SimpleDigit digit5 = new SimpleDigit(661, 139, 46, 62, 5);
		SimpleSegment sm50 = new SimpleSegment(21, 11, 6, 6, 0);
		digit5.getSegments().set(0, sm50);
		SimpleSegment sm51 = new SimpleSegment(31, 23, 6, 6, 1);
		digit5.getSegments().set(1, sm51);
		SimpleSegment sm52 = new SimpleSegment(30, 42, 6, 6, 2);
		digit5.getSegments().set(2, sm52);
		SimpleSegment sm53 = new SimpleSegment(17, 52, 6, 6, 3);
		digit5.getSegments().set(3, sm53);
		SimpleSegment sm54 = new SimpleSegment(7, 43, 6, 6, 4);
		digit5.getSegments().set(4, sm54);
		SimpleSegment sm55 = new SimpleSegment(10, 20, 6, 6, 5);
		digit5.getSegments().set(5, sm55);
		SimpleSegment sm56 = new SimpleSegment(19, 34, 6, 6, 6);
		digit5.getSegments().set(6, sm56);
		model.getDigits().add(digit5);
		SimpleDigit digit6 = new SimpleDigit(716, 137, 46, 62, 6);
		SimpleSegment sm60 = new SimpleSegment(21, 11, 6, 6, 0);
		digit6.getSegments().set(0, sm60);
		SimpleSegment sm61 = new SimpleSegment(31, 23, 6, 6, 1);
		digit6.getSegments().set(1, sm61);
		SimpleSegment sm62 = new SimpleSegment(30, 42, 6, 6, 2);
		digit6.getSegments().set(2, sm62);
		SimpleSegment sm63 = new SimpleSegment(17, 52, 6, 6, 3);
		digit6.getSegments().set(3, sm63);
		SimpleSegment sm64 = new SimpleSegment(7, 43, 6, 6, 4);
		digit6.getSegments().set(4, sm64);
		SimpleSegment sm65 = new SimpleSegment(10, 20, 6, 6, 5);
		digit6.getSegments().set(5, sm65);
		SimpleSegment sm66 = new SimpleSegment(19, 34, 6, 6, 6);
		digit6.getSegments().set(6, sm66);
		model.getDigits().add(digit6);
		SimpleDigit digit7 = new SimpleDigit(770, 134, 46, 62, 7);
		SimpleSegment sm70 = new SimpleSegment(21, 11, 6, 6, 0);
		digit7.getSegments().set(0, sm70);
		SimpleSegment sm71 = new SimpleSegment(31, 23, 6, 6, 1);
		digit7.getSegments().set(1, sm71);
		SimpleSegment sm72 = new SimpleSegment(30, 42, 6, 6, 2);
		digit7.getSegments().set(2, sm72);
		SimpleSegment sm73 = new SimpleSegment(17, 52, 6, 6, 3);
		digit7.getSegments().set(3, sm73);
		SimpleSegment sm74 = new SimpleSegment(7, 43, 6, 6, 4);
		digit7.getSegments().set(4, sm74);
		SimpleSegment sm75 = new SimpleSegment(10, 20, 6, 6, 5);
		digit7.getSegments().set(5, sm75);
		SimpleSegment sm76 = new SimpleSegment(19, 34, 6, 6, 6);
		digit7.getSegments().set(6, sm76);
		model.getDigits().add(digit7);

		return model;
	}

	private static void prepareDigit(SimpleDigit d1, int xd, int yd, SimpleSegment... sg) {
		d1.getSegments().clear();
		for (SimpleSegment g : sg) {
			SimpleSegment sgg = g.clone();
			sgg.setX(sgg.getX() + xd);
			sgg.setY(sgg.getY() + yd);
			d1.getSegments().add(sgg);
		}
	}

	private static SimpleSegment getSegment(SimpleDigit d1, int i, int j, int k, int l, String label) {
		int w = k - i;
		int h = l - j;
		int x = i - d1.getX();
		int y = j - d1.getY();
		SimpleSegment sg = new SimpleSegment(x, y, w, h, label);

		return sg;
	}



	@Override
	public List<Integer> read(String imagePath) {
		return evaluate(getModel(), imagePath);
	}
	@Override
	public List<Integer> read(Mat img) {
		return evaluateInternal(getModel(), img,null);
	}
	@Override
	public List<Integer> read(Mat img,String imageName) {
		return evaluateInternal(getModel(), img, imageName);
		
	}

}
