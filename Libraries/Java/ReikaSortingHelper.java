/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.CommutativePair;

public class ReikaSortingHelper {

	public static void bubbleSort(List li) {
		bubbleSort(li, new DefaultComparator());
	}

	public static void bubbleSort(List li, Comparator c) {
		if (li.size() > 1) {
			for (int i = 0; i < li.size(); i++) {
				for (int x = 1; x < li.size()-i; x++) {
					Object o = li.get(x);
					Object p = li.get(x-1);
					if (c.compare(p, o) > 0) {
						Object temp = p;
						li.set(x-1, o);
						li.set(x, temp);
					}
				}
			}
		}
	}

	public static void mergeSort(List li) {
		mergeSort(li, new DefaultComparator());
	}

	public static void mergeSort(List li, Comparator c) {
		if (li.size() > 1) {
			int mid = li.size()/2;

			List left = li.subList(0, mid);
			List right = li.subList(mid, li.size());

			mergeSort(left);
			mergeSort(right);

			merge(li, left, right, c);
		}
	}

	private static void merge(List li, List left, List right, Comparator c) {
		int sum = left.size()+right.size();
		int i = 0;
		int il = 0;
		int ir = 0;
		while (i < sum) {
			Object ol = left.get(il);
			Object or = right.get(ir);
			if (il < left.size() && ir < right.size()) {
				if (c.compare(ol, or) < 0) {
					li.set(i, ol);
					i++;
					il++;
				}
				else {
					li.set(i, or);
					i++;
					ir++;
				}
			}
			else {
				if (il >= left.size()) {
					while (ir < right.size()) {
						li.set(i, or);
						i++;
						ir++;
					}
				}
				if (ir >= right.size()) {
					while (il < left.size()) {
						li.set(i, ol);
						il++;
						i++;
					}
				}
			}
		}
	}

	public static void allPairSort(ArrayList li) {
		allPairSort(li, new DefaultComparator());
	}

	private static boolean debug = false;

	/** Sorts a list by every possible pairing. Very slow but very effective for specialized datasets. */
	public static void allPairSort(ArrayList li, Comparator c) {
		HashMap<ImmutablePair, Integer> map = new HashMap();
		Collection<CommutativePair> pairs = new ArrayList();
		for (Object o1 : li) {
			for (Object o2 : li) {
				CommutativePair cp = new CommutativePair(o1, o2);
				if (!pairs.contains(cp)) {
					map.put(new ImmutablePair(o1, o2), c.compare(o1, o2));
					pairs.add(cp);
				}
			}
		}

		DragonAPICore.log("About to perform a very slow sorting! If your game locks up here, come to Reika with this message!");
		DragonAPICore.log("Size: "+li.size()+"; Contents: "+li+"; Map: "+map);
		boolean flag = sortListByPairMap(li, map);
		int cycles = 1;
		while (flag) {
			if (debug)
				DragonAPICore.log(cycles+" cycles.");
			flag = sortListByPairMap(li, map);
			cycles++;
		}
	}

	private static boolean sortListByPairMap(ArrayList li, HashMap<ImmutablePair, Integer> map) {
		for (int i = 0; i < li.size(); i++) {
			Object o1 = li.get(i);
			for (int k = 0; k < li.size(); k++) {
				Object o2 = li.get(k);
				Integer rel = map.get(new ImmutablePair(o1, o2));
				if (rel == null)
					continue;
				if (debug) {
					//DragonAPICore.log("Comparing "+o1+" @ "+i+" and "+o2+" @ "+k+"; Map contains "+rel);
					//DragonAPICore.log("");
				}
				if ((rel < 0 && i > k) || (rel > 0 && i < k)) { //swap
					if (debug) {
						String s = (rel < 0 && i > k) ? "after when it should be before" : "before when it should be after";
						DragonAPICore.log("Swapping "+o1+" and "+o2+": o1 was "+s);
					}
					li.set(i, o2);
					li.set(k, o1);
					return true;
				}
			}
		}
		return false;
	}

	/*
	public static void mapKeySort(ArrayList li) {
		mapKeySort(li, new DefaultComparator());
	}

	public static void mapKeySort(ArrayList li, Comparator c) {
		IdentityHashMap<Object, Integer> map = new IdentityHashMap();
		for (int i = 0; i < li.size(); i++) {
			Object o = li.get(i);
			map.put(o, i);
		}
	}
	 */
	private static class DefaultComparator implements Comparator<Comparable> {

		@Override
		public int compare(Comparable o1, Comparable o2) {
			return o1.compareTo(o2);
		}

	}

}
