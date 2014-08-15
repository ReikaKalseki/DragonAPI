/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.awt.Color;

import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public interface WireBlock {

	public int getPowerState(IBlockAccess world, int x, int y, int z);

	/** Does it connect to this side at all? */
	public boolean isConnectedTo(IBlockAccess world, int x, int y, int z, int side);

	/** Is it another wire block or a redstone logic block? */
	public boolean isDirectlyConnectedTo(IBlockAccess world, int x, int y, int z, int side);

	public boolean isTerminus(IBlockAccess world, int x, int y, int z, int side);

	public Color getColor();

	public IIcon getConnectedSideOverlay();

	public IIcon getBaseTexture();

	public boolean drawWireUp(IBlockAccess world, int x, int y, int z, int side);

}