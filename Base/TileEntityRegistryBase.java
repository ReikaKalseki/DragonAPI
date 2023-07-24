/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.Interfaces.Registry.TileEnum;

public abstract class TileEntityRegistryBase<E extends TileEnum> extends TileEntityBase {

	@Override
	public final Block getTileEntityBlockID() {
		return this.getTile().getBlock();
	}

	public abstract E getTile();

	public final int getIndex() {
		return this.getTile().ordinal();
	}

	@Override
	public String getTEName() {
		return this.getTile().getName();
	}

	@Override
	public abstract void updateEntity(World world, int x, int y, int z, int meta);

	@Override
	protected abstract void animateWithTick(World world, int x, int y, int z);

	public final boolean isThisTE(Block id, int meta) {
		return id == this.getTileEntityBlockID() && meta == this.getIndex();
	}
}
