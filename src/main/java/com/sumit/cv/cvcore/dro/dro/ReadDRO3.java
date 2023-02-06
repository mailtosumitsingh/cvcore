package com.sumit.cv.cvcore.dro.dro;


import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.global.opencv_imgproc;
import com.sumit.cv.cvcore.opencv.OpencvUtils;
import com.sumit.cv.cvcore.opencv.Rectangle;

public class ReadDRO3 {
	private static boolean DEBUG = true;

	//what we need?
	//where on screen are the digits?
	//offsets of digits in screen, each digit could have its own offset
	public static void main(String[] args) throws Exception {
		System.loadLibrary("opencv_java341");
		System.loadLibrary("opencv_ffmpeg341");

		SevenSegModel model = new SevenSegModel();
		model.initDigitsOffsets();
		
		model.initDigits();
		Mat img = org.bytedeco.opencv.global.opencv_imgcodecs.imread("D:\\meters\\dro\\output\\1268.jpg", opencv_imgcodecs.IMREAD_COLOR);
		img = OpencvUtils.rotateImage(img, new Point2f(0,0),model.getDisplayAngle());
		
		if(DEBUG) {
			OpencvUtils.drawRect(img, new Point(model.getX(),model.getY()), new Point(model.getX()+model.getW(), model.getY()+model.getH()), new Scalar(255, 0, 0, 1));
		}
		
		for(int digitScroll =0;digitScroll<model.getDigits().size();digitScroll++) {
			Digit d = model.getDigits().get(digitScroll);
			if(DEBUG) {
			OpencvUtils.drawRect(img, new Point(d.getX(),d.getY()), new Point(d.getX()+d.getW(),d.getY()+d.getH()), new Scalar(255, 0, 128, 1));
			}
			if(DEBUG) {
				opencv_imgproc.putText(img, d.getLabel(),new Point(d.getX()+d.getW()/2,d.getY()+d.getH()/2),1,1,new Scalar(255, 0,128,1 ));
			}
			Mat digitMat = OpencvUtils.getImageRoiRect(img, d.getX(),d.getY(),d.getW(),d.getH());
			digitize(digitMat,d);
			for(int k=0;k<model.getDigits().size();k++) {
				Rectangle rr =  d.getSegments().get(k);
				if(DEBUG) {
					OpencvUtils.drawRect(img, new Point((int)d.getX()+(int)rr.getX(),(int)d.getY()+(int)rr.getY()), new Point((int)d.getX()+(int)rr.getX()+(int)rr.getWidth(),(int)d.getY()+(int)rr.getY()+(int)rr.getHeight()), new Scalar(128, 128, 128, 1) );
				}
				Mat img2 = OpencvUtils.getImageRoiRect(img, (int)(d.getX()+rr.getX()),(int)(d.getY()+rr.getY()),(int)rr.getWidth(),(int)rr.getHeight());
				double count = img2.rows()*img2.cols();
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
				double matchPerc = (countMatch/count)*100;
				if(matchPerc>model.getModelSegmentClusterTolerance()) {
				status  ="on";
				}else {
					status  ="off";
				}
				System.out.println(digitScroll +"("+k+")"+" is  "+status +" "+model.getModelSegmentClusterTolerance()+" / "+matchPerc);

			}
		}

		OpencvUtils.saveImage(img, "D:\\meters\\dro\\output\\1268_changed.jpg");
	
	}


	private static Digit  digitize(Mat digitMat,Digit d) {
			// prepare digitization data per char
			// seven segment led :-) hurray so easy to digitize!!!
			Rectangle r1 = new Rectangle();
			r1.x = 0+d.getSgh() +d.getSegmentDrift();
			r1.y = 0;
			r1.width = d.getSgw();
			r1.height = d.getSgh();
			
			Rectangle r7 = new Rectangle(r1);
			r7.y=r7.y+d.getSgw()+d.getSgh()+d.getSegSpace();
			d.getSegments().set(7, r7);
			
			Rectangle r4 = new Rectangle(r1);
			r4.y=r4.y+d.getSgw()*2+d.getSgh()*2;
			r4.x = r4.x-d.getSegmentDrift();
			d.getSegments().set(4, r4);
			
			
			// to iterate we need to sty relative oriented other wise we will
			// have to build custom model based on absolute pos of every segment
			Rectangle r2 = new Rectangle(r1);
			r2.x = r1.x+r1.width+d.getSegSpace();
			r2.y = r1.y + (int)r1.getHeight();
			r2.width = d.getSgh();
			r2.height = d.getSgw();
			d.getSegments().set(1, r1);
			d.getSegments().set(2, r2);
			Rectangle r6 = new Rectangle(r2);
			r6.x=r6.x-d.getSgw()-d.getSgh()-d.getSegSpace()-d.getSegDriftV();
			d.getSegments().set(6, r6);
			
			Rectangle r3 = new Rectangle(r2);
			r3.x = r2.x-d.getSegmentDrift()/2-d.getSegDriftV();
			r3.y =  (int)r2.getY()+(int)r2.getHeight()+d.getSgh()+d.getSegSpace();
			r3.width = d.getSgh();
			r3.height = d.getSgw();
			d.getSegments().set(3, r3);

			Rectangle r5 = new Rectangle(r3);
			r5.x=r5.x-d.getSgw()-d.getSgh()-d.getSegSpace()-d.getSegDriftV();
			d.getSegments().set(5, r5);

			
			
			return d;
	}
}
