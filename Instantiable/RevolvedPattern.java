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

import net.minecraft.world.World;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;


public class RevolvedPattern {

	public final int coreSize;
	public final int height;

	private final int size;

	private final FilledBlockArray data;

	public RevolvedPattern(World world, int c, int h, int r) {
		coreSize = c;
		height = h;
		size = r;
		data = new FilledBlockArray(world);
	}

	public RevolvedPattern addBlock(BlockKey bk, int layer, int main, int side) {
		data.setBlock(main, layer, side, bk);
		return this;
	}

	public RevolvedPattern addLayer(BlockKey[][] arr, int layer) {
		if (arr.length != size || arr[0].length != size) {
			throw new MisuseException("Layer does not fit!");
		}
		for (int i = 0; i < arr.length; i++) {
			for (int k = 0; k < arr[i].length; k++) {
				this.addBlock(arr[i][k], layer, i, k);
			}
		}
		return this;
	}

	public RevolvedPattern calculate() {
		FilledBlockArray fx = (FilledBlockArray)data.flipX();
		FilledBlockArray fz = (FilledBlockArray)data.flipZ();
		FilledBlockArray fxz = (FilledBlockArray)data.flipX().flipZ();
		if (coreSize > 1) {
			int n = coreSize-1;
			fx.offset(-n, 0, 0);
			fz.offset(0, 0, -n);
			fxz.offset(-n, 0, -n);
		}
		data.addAll(fx);
		data.addAll(fz);
		data.addAll(fxz);
		return this;
	}

	public void populate(FilledBlockArray f) {
		f.addAll(data);
	}
}
