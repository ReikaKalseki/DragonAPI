/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.SmartStripper;

import com.amadornes.framez.api.movement.BlockMovementType;
import com.amadornes.framez.api.movement.HandlingPriority;
import com.amadornes.framez.api.movement.HandlingPriority.Priority;
import com.amadornes.framez.api.movement.IMovementHandler;
import com.amadornes.framez.api.movement.IMovingBlock;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public class FrameBlacklist {

	private static boolean isBlacklisted(World world, int x, int y, int z, Block b, int meta, TileEntity te) {
		return MinecraftForge.EVENT_BUS.post(new FrameUsageEvent(world, x, y, z, b, meta, te));
	}

	@Strippable("com.amadornes.framez.api.movement.IMovementHandler")
	public static class FramezHandler implements IMovementHandler {

		@Override
		@SmartStripper
		@HandlingPriority(Priority.HIGH)
		public boolean handleStartMoving(IMovingBlock block) {
			return isBlacklisted(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getBlock(), block.getMetadata(), block.getTileEntity());
		}

		@Override
		@SmartStripper
		@HandlingPriority(Priority.HIGH)
		public boolean handleFinishMoving(IMovingBlock block) {
			return isBlacklisted(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getBlock(), block.getMetadata(), block.getTileEntity());
		}

		@Override
		@SmartStripper
		@HandlingPriority(Priority.HIGH)
		public BlockMovementType getMovementType(World world, Integer x, Integer y, Integer z) {
			return isBlacklisted(world, x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z)) ? BlockMovementType.UNMOVABLE : null;
		}

	}

	@Cancelable
	public static class FrameUsageEvent extends Event {

		public final World world;
		public final int xCoord;
		public final int yCoord;
		public final int zCoord;

		public final Block block;
		public final int metadata;
		public final TileEntity tile;

		private FrameUsageEvent(World w, int x, int y, int z, Block b, int meta, TileEntity te) {
			world = w;
			xCoord = x;
			yCoord = y;
			zCoord = z;
			block = b;
			metadata = meta;
			tile = te;
		}

	}

}
