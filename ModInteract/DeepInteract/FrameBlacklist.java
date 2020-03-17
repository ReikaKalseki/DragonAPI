/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import com.amadornes.framez.api.FramezApi;
import com.amadornes.framez.api.Priority;
import com.amadornes.framez.api.movement.BlockMovementType;
import com.amadornes.framez.api.movement.IMovement;
import com.amadornes.framez.api.movement.IMovementHandler;
import com.amadornes.framez.api.movement.IMovingBlock;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.SmartStrip;
import Reika.DragonAPI.Instantiable.Event.TileEntityMoveEvent;

import cpw.mods.fml.common.Loader;

public class FrameBlacklist {

	public static final FrameBlacklist instance = new FrameBlacklist();

	private FrameBlacklist() {
		if (Loader.isModLoaded("framez"))
			FramezApi.instance().movement().registerMovementHandler(new FramezHandler());
	}

	private boolean isBlacklisted(World world, int x, int y, int z, Block b, int meta, TileEntity te) {
		return MinecraftForge.EVENT_BUS.post(new TileEntityMoveEvent(world, x, y, z, b, meta, te));
	}

	@Strippable("com.amadornes.framez.api.movement.IMovementHandler")
	public class FramezHandler implements IMovementHandler {

		private FramezHandler() {

		}

		@Override
		@SmartStrip
		@Priority(Priority.PriorityEnum.HIGH)
		public boolean startMoving(IMovingBlock block) {
			return FrameBlacklist.this.isBlacklisted(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getBlock(), block.getMetadata(), block.getTileEntity());
		}

		@Override
		@SmartStrip
		@Priority(Priority.PriorityEnum.HIGH)
		public boolean finishMoving(IMovingBlock block) {
			return FrameBlacklist.this.isBlacklisted(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getBlock(), block.getMetadata(), block.getTileEntity());
		}

		@Override
		public BlockMovementType getMovementType(World world, int x, int y, int z, ForgeDirection side, IMovement movement) {
			return FrameBlacklist.this.isBlacklisted(world, x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z)) ? BlockMovementType.UNMOVABLE : null;
		}

		@Override
		public boolean canHandle(World world, int x, int y, int z) {
			return true;
		}

	}

}
