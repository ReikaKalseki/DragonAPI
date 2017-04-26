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

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
@Deprecated
public class LightCalculationEvent extends Event {

	public final World world;
	public final int x;
	public final int y;
	public final int z;
	public final EnumSkyBlock lightType;

	public LightCalculationEvent(World world, int x, int y, int z, EnumSkyBlock b) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		lightType = b;
	}

	public static boolean fire(World world, int x, int y, int z, EnumSkyBlock b) {
		return MinecraftForge.EVENT_BUS.post(new LightCalculationEvent(world, x, y, z, b));
	}

}
