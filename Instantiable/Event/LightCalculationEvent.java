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

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
@Deprecated
public class LightCalculationEvent extends WorldPositionEvent {

	public final EnumSkyBlock lightType;

	public LightCalculationEvent(World world, int x, int y, int z, EnumSkyBlock b) {
		super(world, x, y, z);
		lightType = b;
	}

	public static boolean fire(World world, int x, int y, int z, EnumSkyBlock b) {
		return MinecraftForge.EVENT_BUS.post(new LightCalculationEvent(world, x, y, z, b));
	}

}
