package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.creativetab.CreativeTabs;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class CreativeTabSorter {

	public static final CreativeTabSorter instance = new CreativeTabSorter();

	private final HashMap<String, HashMap<String, Integer>> tabIndices = new HashMap();
	private final HashMap<String, CreativeTabs> tabMap = new HashMap();

	private CreativeTabSorter() {

	}

	public void registerCreativeTabBefore(CreativeTabs c, CreativeTabs ref) {
		this.registerCreativeTabRelative(c, ref, -1);
	}

	public void registerCreativeTabAfter(CreativeTabs c, CreativeTabs ref) {
		this.registerCreativeTabRelative(c, ref, 1);
	}

	private void registerCreativeTabRelative(CreativeTabs c, CreativeTabs ref, int rel) {
		String sc = c.getTabLabel();
		HashMap<String, Integer> map = tabIndices.get(sc);
		if (map == null) {
			map = new HashMap();
			tabIndices.put(sc, map);
		}
		map.put(ref.getTabLabel(), rel);
	}

	public void sortTabs() {
		this.collectSortAndReplace(alphaComparator);
	}

	//public void sortRegisteredTabs() {
	//	this.collectSortAndReplace(mapComparator);
	//}

	private void collectSortAndReplace(Comparator c) {
		ArrayList<CreativeTabs> list = ReikaJavaLibrary.makeListFromArray(CreativeTabs.creativeTabArray);
		this.populateTabMap(list);
		//this.resortByMap(list);
		Collections.sort(list, c);
		//this.resortByMap(list); //Comparator does not do the job, as it does not compare all pairs
		//ReikaSortingHelper.allPairSort(list, c); //this does but too slow
		CreativeTabs.creativeTabArray = new CreativeTabs[list.size()];
		for (int i = 0; i < list.size(); i++) {
			CreativeTabs t = list.get(i);
			//ReikaReflectionHelper.setFinalField("tabIndex", t, i);
			t.tabIndex = i;
			CreativeTabs.creativeTabArray[i] = t;
		}
	}

	private void populateTabMap(ArrayList<CreativeTabs> li) {
		tabMap.clear();
		for (CreativeTabs c : li) {
			tabMap.put(c.getTabLabel(), c);
		}
	}

	private void resortByMap(ArrayList<CreativeTabs> li) {
		boolean changed = false;
		do {
			for (int i = 0; i < li.size(); i++) {
				CreativeTabs c = li.get(i);
				String s = c.getTabLabel();
				Map<String, Integer> m = tabIndices.get(s);
				if (m != null) {
					for (String s2 : m.keySet()) {
						for (int k = 0; k < li.size(); k++) {
							CreativeTabs c2 = li.get(k);
							if (c2.getTabLabel().equals(s2)) { //match
								int rel = m.get(s2);
								ReikaJavaLibrary.pConsole(s+" > "+s2+": "+rel);
								if (rel != 0) {
									li.remove(k);
									if (rel < 0 && i > k) { //c goes before c2
										li.add(i+1, c2); //insert after
										changed = true;
									}
									else if (rel > 0 && i < k) { //c goes after c2
										li.add(i, c2); //insert before
										changed = true;
									}
								}
								break;
							}
						}
					}
				}
			}
		} while (changed);
	}

	private static final TabComparator alphaComparator = new TabComparator();
	//private static final TabMapComparator mapComparator = new TabMapComparator();

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
				HashMap<String, Integer> m1 = instance.tabIndices.get(o1.getTabLabel());
				HashMap<String, Integer> m2 = instance.tabIndices.get(o2.getTabLabel());
				//ReikaJavaLibrary.pConsole("alpha: "+m1+" & "+o2.getTabLabel(), o1.getTabLabel().equals("Mod Ores"));
				//ReikaJavaLibrary.pConsole("beta: "+m2+" & "+o1.getTabLabel(), o2.getTabLabel().equals("Mod Ores"));
				if (m1 != null) {
					Integer ig = m1.get(o2.getTabLabel());
					if (ig != null) {
						//ReikaJavaLibrary.pConsole("A: "+o1.getTabLabel()+":"+o2.getTabLabel()+" > "+ig);
						return ig.intValue();
					}
				}
				if (m2 != null) {
					Integer ig = m2.get(o1.getTabLabel());
					if (ig != null) {
						//ReikaJavaLibrary.pConsole("B: "+o2.getTabLabel()+":"+o1.getTabLabel()+" > "+ig);
						return ig.intValue();
					}
				}
				return DragonOptions.SORTCREATIVE.getState() ? String.CASE_INSENSITIVE_ORDER.compare(o1.getTabLabel(), o2.getTabLabel()) : o1.tabIndex-o2.tabIndex;
			}
		}

	}
	/*
	private static class TabMapComparator implements Comparator<CreativeTabs> {

		@Override
		public int compare(CreativeTabs o1, CreativeTabs o2) {
			if (isVanillaTab(o1) && isVanillaTab(o2)) {
				return o1.tabIndex-o2.tabIndex-1000;
			}
			else if (isVanillaTab(o1)) {
				return Integer.MIN_VALUE;
			}
			else if (isVanillaTab(o2)) {
				return Integer.MAX_VALUE;
			}
			else
				return 0;
		}

	}
	 */
	private static boolean isVanillaTab(CreativeTabs t) {
		return t.getTabIndex() <= 11;
	}

}
