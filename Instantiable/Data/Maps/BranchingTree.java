/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.HashMap;


public class BranchingTree<B, L> {

	private final HashMap<B, TreeNode<B, L>> data = new HashMap();

	public void addPath(L leaf, B... branches) {
		TreeNode par = null;
		for (int i = 0; i < branches.length; i++) {
			B branch = branches[i];
			TreeNode<B, L> node = this.getOrCreateNode(par, branch);
			par = node;
		}
		par.setLeaf(leaf);
	}

	public TreeNavigator getNavigator() {
		return new TreeNavigator(this);
	}

	private TreeNode<B, L> getOrCreateNode(TreeNode par, B branch) {
		HashMap<B, TreeNode<B, L>> map = par == null ? data : par.children;
		TreeNode<B, L> n = map.get(branch);
		if (n == null) {
			n = new TreeNode(par, branch);
			map.put(branch, n);
		}
		return n;
	}

	private TreeNode<B, L> getNode(TreeNode par, B branch) {
		HashMap<B, TreeNode<B, L>> map = par == null ? data : par.children;
		return map.get(branch);
	}

	private static class TreeNode<B, L> {

		private final TreeNode<B, L> parent;
		private final B parentBranch;
		private final HashMap<B, TreeNode<B, L>> children = new HashMap();
		private L leaf;

		private TreeNode(TreeNode<B, L> par, B parB) {
			parent = par;
			this.parentBranch = parB;
		}

		private void setLeaf(L leaf) {
			if (this.leaf != null) {
				throw new IllegalArgumentException("Leaves cannot be modified!");
			}
			this.leaf = leaf;
		}

		@Override
		public String toString() {
			return "["+leaf+"] & "+children.toString();
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (B b : data.keySet()) {
			sb.append(b);
			sb.append("=[");
			TreeNode<B, L> n = data.get(b);
			sb.append(this.getString(1, n));
			sb.append("];\n");
		}
		sb.append("}");
		return sb.toString();
	}

	private String getString(int d, TreeNode<B, L> n) {
		StringBuilder sb = new StringBuilder();
		for (B b : n.children.keySet()) {
			for (int i = 0; i < d; i++) {
				sb.append("\t");
			}
			sb.append(b);
			sb.append("=[");
			TreeNode<B, L> n2 = n.children.get(b);
			sb.append(this.getString(d+1, n2));
			sb.append("];");
		}
		sb.append("\n");
		if (n.leaf != null)
			sb.append("("+n.leaf+")");
		return sb.toString();
	}

	public class TreeNavigator {

		protected final BranchingTree<B, L> tree;
		private TreeNode<B, L> currentNode;

		private TreeNavigator(BranchingTree<B, L> t) {
			tree = t;
		}

		public boolean stepUp() {
			TreeNode<B, L> last = this.currentNode;
			if (this.currentNode != null) {
				this.currentNode = this.currentNode.parent;
			}
			return last != this.currentNode;
		}

		public boolean stepDown(B branch) {
			currentNode = BranchingTree.this.getNode(currentNode, branch);
			return this.currentNode != null;
		}

		public L getLeaf() {
			return this.currentNode != null ? this.currentNode.leaf : null;
		}

	}
	/*
	private class PrintNavigator extends TreeNavigator {

		private PrintNavigator(BranchingTree<B, L> t) {
			super(t);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (B b : tree.data.keySet()) {
				TreeNode<B, L> n = tree.data.get(b);
				sb.append(b);
				sb.append("=[");
				TreeNode<B, L> n2 = n.children.get(b);
				sb.append(this.getString(n2));
				sb.append("]; ");
			}
			return sb.toString();
		}
	}
	 */
}
