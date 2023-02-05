package com.sumit.cv.cvcore.engine;

import java.util.Map;

import com.sumit.cv.cvcore.models.AnonDefObj;
import com.sumit.cv.cvcore.opencv.Model;
import com.sumit.cv.cvcore.opencv.OpencvUtils;

public abstract class DefaultVNodeHandler implements IVnodeHandler {

	Model model;
	
	@Override
	public void handleChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode, VNode<AnonDefObj> vnodeChild) {
		handleVnode(o, ctx,  mapVnode, vnodeChild);

	}
	@Override
	public void beforeChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode, VNode<AnonDefObj> vnodeChild) {

	}
	@Override
	public void postChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode, VNode<AnonDefObj> vnodeChild) {

	}
	@Override
	public void handleAsParentWithoutChilds(FPGraph2 o, Map<String, Object> ctx, AnonDefObj self) {

	}

	@Override
	public void handleChild(FPGraph2 o, Map<String, Object> ctx, VNode<AnonDefObj> vnodeSelf) {

	}

	@Override
	public void handleAsParentWithChilds(FPGraph2 o, Map<String, Object> ctx, AnonDefObj self) {

	}
	
	
	@Override
	public void handleVnode(FPGraph2 o, Map<String, Object> ctx,
			 Map<String, VNode<AnonDefObj>> mapVnode,VNode<AnonDefObj> v) {
		if (v == null) {
			System.out.println("Could not find vnode." );
		} else {
			System.out.println("Running vnode: " + v.getSelf().getId());
			if (v.getChildren().size() > 0) {
				handleAsParentWithChilds(o, ctx, v.getSelf());
				for (AnonDefObj child : v.getChildren()) {
					VNode<AnonDefObj> vnodeChild = mapVnode.get(child.getId());
					if (vnodeChild.getChildren().size() > 0) {
						beforeChildComplexNode(o, ctx, mapVnode, vnodeChild);
						handleChildComplexNode(o, ctx, mapVnode, vnodeChild);
						postChildComplexNode(o, ctx, mapVnode, vnodeChild);
					} else {
						handleChild(o,ctx,vnodeChild);
					}
				}

			} else {
				handleAsParentWithoutChilds(o, ctx, v.getSelf());
			}
		}
	}
	public void setModel(Model m) {
		this.model = m;
	}
	public abstract void init() ;
	

}
