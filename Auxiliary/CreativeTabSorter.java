/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.creativetab.CreativeTabs;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class CreativeTabSorter {

	public static final CreativeTabSorter instance = new CreativeTabSorter();

	private final HashMap<CreativeTabs, LinkedList<CreativeTabs>> tabGroups = new HashMap();
	private final Collection<CreativeTabs> childTabs = new ArrayList();

	private CreativeTabSorter() {

	}

	public void registerCreativeTabAfter(CreativeTabs c, CreativeTabs ref) {
		LinkedList<CreativeTabs> li = tabGroups.get(ref);
		if (li == null) {
			li = new LinkedList();
			tabGroups.put(ref, li);
		}
		li.addLast(c);
		childTabs.add(c);
	}

	public void sortTabs() {
		this.collectSortAndReplace(tabComparator);
	}

	private void collectSortAndReplace(Comparator c) {
		ArrayList<CreativeTabs> list = ReikaJavaLibrary.makeListFromArray(CreativeTabs.creativeTabArray);
		for (CreativeTabs t : childTabs) {
			list.remove(t);
		}
		Collections.sort(list, c);
		for (CreativeTabs p : tabGroups.keySet()) {
			LinkedList<CreativeTabs> ch = tabGroups.get(p);
			int index = list.indexOf(p)+1;
			for (CreativeTabs t : ch) {
				list.add(index, t);
				index++;
			}
		}
		CreativeTabs.creativeTabArray = new CreativeTabs[list.size()];
		for (int i = 0; i < list.size(); i++) {
			CreativeTabs t = list.get(i);
			t.tabIndex = i;
			CreativeTabs.creativeTabArray[i] = t;
		}
	}

	private static final TabComparator tabComparator = new TabComparator();

	private static class TabComparator implements Comparator<CreativeTabs> {

		@Override
		public int compare(CreativeTabs o1, CreativeTabs o2) {
			if (isVanillaTab(o1) && isVanillaTab(o2)) {
				//ReikaJavaLibrary.pConsole(o1.getTabLabel()+"@"+o1.tabIndex+" : "+o2.getTabLabel()+"@"+o2.tabIndex+" > "+(o1.tabIndex-o2.tabIndex));
				return o1.tabIndex-o2.tabIndex;
			}
			else if (isVanillaTab(o1)) {
				return Integer.MIN_VALUE;
			}
			else if (isVanillaTab(o2)) {
				return Integer.MAX_VALUE;
			}
			else {
				return DragonOptions.SORTCREATIVE.getState() ? String.CASE_INSENSITIVE_ORDER.compare(o1.getTabLabel(), o2.getTabLabel()) : o1.tabIndex-o2.tabIndex;
			}
		}

	}

	private static boolean isVanillaTab(CreativeTabs t) {
		return t.getTabIndex() <= 11;
	}

}
