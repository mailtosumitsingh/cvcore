package com.sumit.cv.cvcore.opencv.examples;


import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
public class VideoToFrames {
	public static void main(String[] args) throws Exception {
		 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String file = "c:/temp/a.mov";
		String output = "c:/temp/a/";

		movToFrames(file, output);


	    
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
				Imgcodecs.imwrite(output + "/" + framePos + ".jpg", frame);
				framePos++;
				vc.read(frame);
			}
			vc.release();

			System.out.println(frameCount + " Frames extracted");

		}
	}
}
