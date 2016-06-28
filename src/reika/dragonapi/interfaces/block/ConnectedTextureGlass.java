/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.block;

import java.util.ArrayList;

import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;


public interface ConnectedTextureGlass {

	/** Returns the unconnected sides. Each integer represents one of 8 adjacent corners to a face, with the same
	 * numbering convention as is found on a calculator or computer number pad. */
	public ArrayList<Integer> getEdgesForFace(IBlockAccess world, int x, int y, int z, ForgeDirection face);
	public IIcon getIconForEdge(IBlockAccess world, int x, int y, int z, int edge);

	public IIcon getIconForEdge(int itemMeta, int edge);
	public boolean renderCentralTextureForItem(int meta);

}
