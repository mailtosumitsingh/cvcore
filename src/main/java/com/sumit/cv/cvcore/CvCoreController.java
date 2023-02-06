package com.sumit.cv.cvcore;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sumit.cv.cvcore.dro.ReadDROOverHTTP;
import com.sumit.cv.cvcore.dro.dro.simpledro.DROModel;
import com.sumit.cv.cvcore.dro.dro.simpledro.DROValue;
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
	@PostMapping("/getCameraImage")
	public byte[] process() {
		return null;
	}
	@PostMapping("/readDro")
	public DROValue readDRo(DROModel model) {
		ReadDROOverHTTP reader = new ReadDROOverHTTP(model.getModel());
		try {
			List<Integer>vals = reader.handle(model.getFileName(), model.getFileNameTemplate());
			DROValue val= new DROValue();
			val.setValues(vals);
			return val;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DROValue();
		
	}
}
