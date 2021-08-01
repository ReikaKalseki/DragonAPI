/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

import Reika.DragonAPI.Interfaces.TextureFetcher;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public interface RenderFetcher {

	@SideOnly(Side.CLIENT)
	public abstract TextureFetcher getRenderer();

}
