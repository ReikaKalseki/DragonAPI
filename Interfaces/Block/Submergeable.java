/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Block;

import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


/** For non-cube blocks that are intended to be underwater yet not have the "walls" of water around a void. Avoids needing to make it Material.water. */
public interface Submergeable {

	public boolean isSubmergeable(IBlockAccess iba, int x, int y, int z);

	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass();

	public boolean canRenderInPass(int pass);

	public boolean renderLiquid(int meta);

}
