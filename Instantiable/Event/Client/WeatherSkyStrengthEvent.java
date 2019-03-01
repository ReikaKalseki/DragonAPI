/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;


public class WeatherSkyStrengthEvent extends Event {

	public final World world;
	public final float originalStrength;
	public final float partialTickTime;

	public float returnValue;

	public WeatherSkyStrengthEvent(World world, float f, float ptick) {
		this.world = world;
		originalStrength = returnValue = f;
		partialTickTime = ptick;
	}

	public static float fire_Rain(World world, float ptick) {
		WeatherSkyStrengthEvent evt = new WeatherSkyStrengthEvent(world, world.getRainStrength(ptick), ptick);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.returnValue;
	}

	public static float fire_Thunder(World world, float ptick) {
		WeatherSkyStrengthEvent evt = new WeatherSkyStrengthEvent(world, world.getWeightedThunderStrength(ptick), ptick);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.returnValue;
	}

}
