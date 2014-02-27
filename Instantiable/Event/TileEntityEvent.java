/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.Event;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class TileEntityEvent extends Event {

	private final TileEntity tile;

	public TileEntityEvent(TileEntity te) {
		tile = te;
	}

	public final World getWorld() {
		return tile.worldObj;
	}

	public final int getTileX() {
		return tile.xCoord;
	}

	public final int getTileY() {
		return tile.yCoord;
	}

	public final int getTileZ() {
		return tile.zCoord;
	}

	public final boolean isTileInventory() {
		return tile instanceof IInventory;
	}

	public final boolean isTileFluidHandler() {
		return tile instanceof IFluidHandler;
	}

}
