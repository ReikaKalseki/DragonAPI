/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.awt.Color;

import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public interface WireBlock {

	public int getPowerState(IBlockAccess world, int x, int y, int z);

	public boolean isConnectedTo(IBlockAccess world, int x, int y, int z, int side);

	public Color getColor();

	public Icon getConnectedSideOverlay();

	public Icon getBaseTexture();

	public boolean drawWireUp(IBlockAccess world, int x, int y, int z, int side);

}
