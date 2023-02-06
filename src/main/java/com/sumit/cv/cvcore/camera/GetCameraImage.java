/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package com.sumit.cv.cvcore.camera;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class GetCameraImage  {

	public void handle(String index, String width, String height)throws Exception {
		if(index==null) {
			index = "0";
		}
		if(width==null) {
			width = "1280";
		}
		if(height==null) {
			height = "720";
		}
		int idx = Integer.parseInt(index);
		int w = Integer.parseInt(width);
		int h = Integer.parseInt(height);
		VideoCapture capture = null;
		try {
			capture = new VideoCapture(0);
			capture.set(Videoio.CAP_PROP_FRAME_WIDTH, w);
			capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, h);
			Mat matrix = new Mat();
			capture.read(matrix);
			//response.setContentType("image/jpeg");
			//response.setHeader("Access-Control-Allow-Origin", "*");
			//BufferedImage buff = OpencvUtils.Mat2BufferedImage(matrix);
			//OutputStream out = response.getOutputStream();
			//ImageIO.write(buff, "jpg", out);
			capture.release();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(capture!=null)
				capture.release();
		}
	}

}

