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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class GrassSustainCropEvent extends Event {

	public final World world;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public final ForgeDirection side;
	public final IPlantable crop;

	public GrassSustainCropEvent(World world, int x, int y, int z, ForgeDirection dir, IPlantable p) {
		this.world = world;
		xCoord = x;
		yCoord = y;
		zCoord = z;
		side = dir;
		crop = p;
	}

	public static boolean fireSustain(IBlockAccess world, int x, int y, int z, ForgeDirection dir, IPlantable p, boolean original) {
		if (!(world instanceof World))
			return original;
		Event e = new GrassSustainCropEvent((World)world, x, y, z, dir, p);
		MinecraftForge.EVENT_BUS.post(e);
		switch(e.getResult()) {
			case ALLOW:
				return Blocks.farmland.canSustainPlant(world, x, y, z, dir, p) || original; //or original so things like sugarcane can still be planted
			case DENY:
				return false;
			case DEFAULT:
			default:
				return original;
		}
	}

	public static boolean fireFertility(World world, int x, int y, int z, boolean original) {
		Event e = new GrassSustainCropEvent(world, x, y, z, ForgeDirection.UP, (IPlantable)Blocks.wheat);
		MinecraftForge.EVENT_BUS.post(e);
		switch(e.getResult()) {
			case ALLOW:
				return true;//Blocks.farmland.isFertile(world, x, y, z); do not delegate because will check meta as if dehydrated
			case DENY:
				return false;
			case DEFAULT:
			default:
				return original;
		}
	}

	public static Block fireAgricraft_Block(Block caller, World world, int x, int y, int z) {
		Event e = new GrassSustainCropEvent(world, x, y, z, ForgeDirection.UP, (IPlantable)Blocks.wheat);
		MinecraftForge.EVENT_BUS.post(e);
		switch(e.getResult()) {
			case ALLOW:
				return Blocks.farmland;
			case DENY: //deny & default do same thing, since this is for a "Soil Container" (ie returning delegate if allow, else return self)
			case DEFAULT:
			default:
				return caller;
		}
	}

	public static int fireAgricraft_Meta(World world, int x, int y, int z) {
		Event e = new GrassSustainCropEvent(world, x, y, z, ForgeDirection.UP, (IPlantable)Blocks.wheat);
		MinecraftForge.EVENT_BUS.post(e);
		switch(e.getResult()) {
			case ALLOW:
				return 7;//fully hydrated
			case DENY: //deny & default do same thing, since this is for a "Soil Container" (ie returning delegate if allow, else return self)
			case DEFAULT:
			default:
				return world.getBlockMetadata(x, y, z);
		}
	}

}
