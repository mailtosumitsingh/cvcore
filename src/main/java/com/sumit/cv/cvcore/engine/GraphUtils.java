package com.sumit.cv.cvcore.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.google.common.base.Predicate;
import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.models.ConnDef;
import com.sumit.cv.cvcore.models.Shape;
import com.sumit.cv.cvcore.opencv.Model;
import com.sumit.cv.cvcore.opencv.OpencvUtils;

public class GraphUtils {
	DefaultVNodeHandler handler;
	
	
	public DefaultVNodeHandler getHandler() {
		return handler;
	}


	public void setHandler(DefaultVNodeHandler handler) {
		this.handler = handler;
	}


	public void runGraph(Model m) {
		Map < String , Object > executionCtx = new HashMap < > ( ) ;
		FPGraph2 o = makeGraph(m);
		handler.setModel(m);
		handler.init();
		createAndRunGraph(m.getExecutionId(),m.getName(), o,executionCtx);
		
	}


	private com.sumit.cv.cvcore.engine.FPGraph2 makeGraph(Model m) {
		FPGraph2 o = new FPGraph2();
		for(Map.Entry<String, Shape> en:m.getShapes().entrySet()) {
			AnonDefObj def = new AnonDefObj();
			Shape shape = en.getValue();
			def.setId(shape.getId());
			String tagData = shape.getData().get("tag");
			if(tagData==null) tagData = "match";
			for (String tag: tagData.split(",")) {
				def.getTags().add(tag);
			}
			def.setX(shape.getX());
			def.setY(shape.getY());
			def.setR(shape.getR());
			def.setB(shape.getB());
			o.getAnonDefs().add(def);
		}
		o.getAnonDefs().addAll(m.getAnonDefs());
		for(ConnDef def: m.getConnections()) {
			o.getForward().put(def.getId(), def);
		}
		return o;
	}
	
	public Map<String, Object> createAndRunGraph(String uidStr, String name, FPGraph2 o,
			Map<String, Object> executionCtx) {

		Map<String, AnonDefObj> anonCompMap = new HashMap<String, AnonDefObj>();
		for (AnonDefObj def : o.getAnonDefs()) {
			anonCompMap.put(def.getId(), def);
		}
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
			// iterate graph here
			String result = "";
			try {
				result = runGraph(name, uidStr, o, executionCtx, anonCompMap);
				ret.put("result", result);
			} catch (Exception e) {
				StringBuilder error = new StringBuilder();
				error.append(e.getMessage());
				error.append("\n");
				for (StackTraceElement stack : e.getStackTrace()) {
					error.append(stack.toString());
					error.append("\n");
				}
				ret.put("Exception", error.toString());
				e.printStackTrace();
			}
		return ret;
	}
	public String runGraph(String name, String uidStr, FPGraph2 o, Map<String, Object> ctx,
			Map<String, AnonDefObj> anonCompMap) throws Exception {
		final DirectedMultigraph<String, DefaultEdge> g = new DirectedMultigraph<String, DefaultEdge>(
				DefaultEdge.class);
		covertFPGRaphToJGrapht(o, g);
		
		System.out.println(g);

		fixMultipleStarts(g, null);
		System.out.println(g);
		List<List<String>> cycles = detectCycles(g);
		System.out.println(cycles);
		Map<String, VNode<AnonDefObj>> mapVnode = new HashMap<>();
		for (Map.Entry<String, AnonDefObj> en : anonCompMap.entrySet()) {
			mapVnode.put(en.getKey(), new VNode<AnonDefObj>(en.getValue()));
		}
		for (List<String> cycle : cycles) {
			VNode<AnonDefObj> vnode = mapVnode.get(cycle.get(0));
			vnode.setSelf(anonCompMap.get(cycle.get(0)));
			for (int i = 1; i < cycle.size(); i++) {
				vnode.getChildren().add(anonCompMap.get(cycle.get(i)));
				g.removeEdge(cycle.get(i - 1), cycle.get(i));
				g.removeVertex(cycle.get(i));
			}
			mapVnode.put(vnode.getSelf().getId(), vnode);
			// remove g cycles and vnodes
		}
		System.out.println(g);
		List<String> ports = topologicallySort(g);
		System.out.println(ports);
		System.out.println("Wait");
		for (String port : ports) {
			VNode<AnonDefObj> vnodeOBj = mapVnode.get(port);
			if(vnodeOBj==null)
				System.out.println("Could not find vnode: "+port);
			
			handler.handleVnode(o, ctx, mapVnode, vnodeOBj);

		}
		
		return "";
	}

	private void covertFPGRaphToJGrapht(com.sumit.cv.cvcore.engine.FPGraph2 o,
			DirectedMultigraph<String, DefaultEdge> g) {
			for(AnonDefObj adef : o.getAnonDefs()) {
				g.addVertex(adef.getId());
			}
			for(ConnDef cd : o.getForward().values()) {
				g.addEdge(cd.getFrom(), cd.getTo(), new DefaultEdge());
			}
	}

	

	

	public List<List<String>> detectCycles(DirectedMultigraph<String, DefaultEdge> g) {
		System.out.println(g);
		SzwarcfiterLauerSimpleCycles<String, DefaultEdge> cycleDetector = new SzwarcfiterLauerSimpleCycles<>(g);
		List<List<String>> cycles = cycleDetector.findSimpleCycles();
		System.out.println(cycles);
		return cycles;
	}
	public static void fixMultipleStarts(final DirectedMultigraph<String, DefaultEdge> g, Predicate<Object> p) {
		List<String> sources = new ArrayList<String>();
		for (String s : g.vertexSet()) {
			if (g.inDegreeOf(s) == 0 && (p == null || p != null && p.apply(s))) {
				sources.add(s);
			}
		}
		{
			String startNodeName = "STARTNODE";
			g.addVertex(startNodeName);
			for (String s : sources) {
				g.addEdge(startNodeName, s);
			}
		}
	}
	public static List<String> topologicallySort(final DirectedMultigraph<String, DefaultEdge> g) {
		TopologicalOrderIterator<String, DefaultEdge> titer = new TopologicalOrderIterator<String, DefaultEdge>(g);
		TopologicalDepthFirstIterator<String, DefaultEdge> trav = new TopologicalDepthFirstIterator<String, DefaultEdge>(g);
		titer.addTraversalListener(trav);
		while (titer.hasNext()) {
			titer.next();
		}
		return trav.getPortObjects();
	}
}
