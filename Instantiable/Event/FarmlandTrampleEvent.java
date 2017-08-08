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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class FarmlandTrampleEvent extends BlockEvent {

	public final Entity entity;
	public final float fallDistance;
	public final boolean defaultTrample;

	public FarmlandTrampleEvent(World world, int x, int y, int z, Entity e, float f) {
		super(x, y, z, world, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		entity = e;
		fallDistance = f;
		defaultTrample = wouldTrampleDefault(world, x, y, z, e, f);
	}

	private static boolean wouldTrampleDefault(World world, int x, int y, int z, Entity e, float f) {
		return !world.isRemote && world.rand.nextFloat() < f-0.5F && (e instanceof EntityPlayer || world.getGameRules().getGameRuleBooleanValue("mobGriefing"));
	}

	public static void fire(World world, int x, int y, int z, Entity e, float f) {
		FarmlandTrampleEvent evt = new FarmlandTrampleEvent(world, x, y, z, e, f);
		MinecraftForge.EVENT_BUS.post(evt);
		boolean flag = false;
		switch(evt.getResult()) {
			case ALLOW:
				flag = true;
				break;
			default:
			case DEFAULT:
				flag = evt.defaultTrample;
				break;
			case DENY:
				break;
		}
		if (flag) {
			world.setBlock(x, y, z, Blocks.dirt);
		}
	}

}
