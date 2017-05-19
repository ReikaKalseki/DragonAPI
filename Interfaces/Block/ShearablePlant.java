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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public interface ShearablePlant {

	public void shearAll(World world, int x, int y, int z, EntityPlayer ep);

	public void shearSide(World world, int x, int y, int z, ForgeDirection side, EntityPlayer ep);

}
