package com.sumit.cv.cvcore.engine;

import java.util.Map;

import com.sumit.cv.cvcore.models.AnonDefObj;

public interface IVnodeHandler {
	 void handleChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode, VNode<AnonDefObj> vnodeChild);//handleVnode(o, ctx, port, vnodeChild, mapVnode);
	 void handleAsParentWithoutChilds(FPGraph2 o, Map<String, Object> ctx, AnonDefObj self);
	 void handleChild(FPGraph2 o, Map<String, Object> ctx, VNode<AnonDefObj> vnodeSelf);
	 void handleAsParentWithChilds(FPGraph2 o, Map<String, Object> ctx, AnonDefObj self);
		public void handleVnode(FPGraph2 o, Map<String, Object> ctx,
				 Map<String, VNode<AnonDefObj>> mapVnode,VNode<AnonDefObj> v);
		void beforeChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode, VNode<AnonDefObj> vnodeChild) ;
		void postChildComplexNode(FPGraph2 o, Map<String, Object> ctx, Map<String, VNode<AnonDefObj>> mapVnode, VNode<AnonDefObj> vnodeChild) ;

}
