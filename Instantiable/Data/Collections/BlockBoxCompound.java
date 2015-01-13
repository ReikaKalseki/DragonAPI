/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;

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
