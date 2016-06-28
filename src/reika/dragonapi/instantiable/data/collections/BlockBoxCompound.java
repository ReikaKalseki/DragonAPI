/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.collections;

import java.util.ArrayList;

import reika.dragonapi.instantiable.data.immutable.BlockBox;

public class BlockBoxCompound {

	private final ArrayList<BlockBox> boxes = new ArrayList();
	//Have some way to do exclusion

	public void addBox(BlockBox b) {
		if (!boxes.contains(b))
			boxes.add(b);
	}

	public void addBox(int minx, int miny, int minz, int maxx, int maxy, int maxz) {
		this.addBox(new BlockBox(minx, miny, minz, maxx, maxy, maxz));
	}

	public boolean contains(int x, int y, int z) {
		for (int i = 0; i < boxes.size(); i++) {
			BlockBox box = boxes.get(i);
			if (box.isBlockInside(x, y, z))
				return true;
		}
		return false;
	}

}
