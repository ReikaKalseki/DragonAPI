/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public class RadialTree {

	private final RadialNodeEntry topEntry;
	private final Collection<RadialNodeEntry> nodes = new ArrayList();
	//private final HashMap<WeakReference<RadialNodeEntry>, RadialNode<RadialNodeEntry>> lookups = new HashMap();

	//public RadialTree(RadialNodeEntry top, Collection<RadialNodeEntry> data) {
	//	this(top, data, null);
	//}

	private RadialTree(RadialNodeEntry top, Collection<RadialNodeEntry> data/*, NodeConverter c*/) {
		topEntry = top;
		for (RadialNodeEntry v : data) {
			this.addObject(v/*, c*/);
		}
	}

	private void addObject(RadialNodeEntry v/*, NodeConverter c*/) {
		//RadialNode n = new RadialNode(v);
		nodes.add(/*c != null ? c.convert(v) : */v);
	}

	@SideOnly(Side.CLIENT)
	public ClickableTree getClickable() {
		return new ClickableTree(this);
	}

	@SideOnly(Side.CLIENT)
	public static class ClickableTree {

		private final RadialTree data;
		private ClickableNode selected;

		//private static final ClickableConverter converter = new ClickableConverter();

		private ClickableTree(RadialTree tree) {
			//data = new RadialTree(tree.topEntry, tree.nodes, converter);
			Collection<ClickableNode> c = new ArrayList();
			for (RadialNodeEntry e : tree.nodes) {
				c.add(new ClickableNode(e));
			}
			data = new RadialTree(new ClickableNode(tree.topEntry), (Collection)c);
		}

		public void render(int x, int y) {
			this.render((ClickableNode)data.topEntry, null);
		}

		private void render(ClickableNode e, ClickableNode parent) {
			this.doRender(e, parent);
			for (RadialNodeEntry c : e.getChildren()) {
				;//temp				//this.render(c, e);
			}
		}

		private void doRender(ClickableNode e, ClickableNode parent) {
			//render
			if (parent != null) {
				//render connection
			}
		}

		public void onClick(int x, int y, int b) {

		}

	}

	private static class ClickableNode implements RadialNodeEntry {

		private final RadialNodeEntry reference;

		private double relativeX;
		private double relativeY;

		private ClickableNode(RadialNodeEntry e) {
			reference = e;
		}

		@Override
		public RadialNodeEntry getParent() {
			return reference.getParent();
		}

		@Override
		public Collection<RadialNodeEntry> getChildren() {
			return reference.getChildren();
		}

	}

	/*
	private static interface NodeConverter<V extends RadialNodeEntry> {

		V convert(RadialNodeEntry v);

		V cast(RadialNodeEntry v);

	}

	private static class ClickableConverter implements NodeConverter<ClickableNode> {

		@Override
		public ClickableNode convert(RadialNodeEntry v) {
			return new ClickableNode(v);
		}

		@Override
		public ClickableNode cast(RadialNodeEntry v) {
			return (ClickableNode)v;
		}

	}*/

	public static interface RadialNodeEntry {

		public RadialNodeEntry getParent();
		public Collection<RadialNodeEntry> getChildren();

	}

}
