/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class FireSpreadEvent extends Event {

	public final World world;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public FireSpreadEvent(World world, int x, int y, int z) {
		this.world = world;
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public static boolean fire(World world, int x, int y, int z) {
		boolean rule = world.getGameRules().getGameRuleBooleanValue("doFireTick");
		if (!rule)
			return false;
		boolean flag = MinecraftForge.EVENT_BUS.post(new FireSpreadEvent(world, x, y, z));
		return !flag;
	}

}
