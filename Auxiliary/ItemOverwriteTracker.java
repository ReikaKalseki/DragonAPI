/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.Item;
import Reika.DragonAPI.Exception.IDConflictException;

public class ItemOverwriteTracker {

	public static final ItemOverwriteTracker instance = new ItemOverwriteTracker();

	private List<Integer> trackedIDs = new ArrayList();
	private HashMap<Integer, Item> map = new HashMap();

	private ItemOverwriteTracker() {

	}

	/** Give it the +256 id. */
	public void addItem(Item c, int id) {
		if (trackedIDs.contains(id)) {
			this.onConflict(id, c);
		}
		else {
			trackedIDs.add(id);
			map.put(id, c);
		}
	}

	private void onConflict(int id, Item overwriter) {
		Item original = map.get(id);
		throw new IDConflictException("Item ID "+id+" (registered to "+original.getUnlocalizedName()+") is being overwritten by "+overwriter.getUnlocalizedName());
	}

	public void check() {
		for (int i = 0; i < trackedIDs.size(); i++) {
			int id = trackedIDs.get(i);
			Item original = map.get(id);
			Item current = Item.itemsList[id];
			if (original != current)
				this.onConflict(id, current);
		}
	}

}
