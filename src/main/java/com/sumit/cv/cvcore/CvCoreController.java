package com.sumit.cv.cvcore;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sumit.cv.cvcore.engine.GraphUtils;
import com.sumit.cv.cvcore.engine.OpencvVnodeHandler;
import com.sumit.cv.cvcore.opencv.Model;


@RestController
public class CvCoreController {
	
	@PostMapping("/process")
	public Map<String, Map<String, Double>> process(@RequestBody Model model) {
		GraphUtils gu = new GraphUtils();
		OpencvVnodeHandler vnodeHandler = new OpencvVnodeHandler();
		gu.setHandler( vnodeHandler );
		gu.runGraph(model);
		return vnodeHandler.getScores();
	}
}
