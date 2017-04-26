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
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;


public class BlockTillEvent extends BlockEvent {

	public Block tilledBlock = Blocks.farmland;
	public int tilledMeta = 0;

	public BlockTillEvent(World world, int x, int y, int z, Block block, int metadata) {
		super(x, y, z, world, block, metadata);
	}

	public static void fire(World world, int x, int y, int z) {
		BlockTillEvent evt = new BlockTillEvent(world, x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
		MinecraftForge.EVENT_BUS.post(evt);
		world.setBlock(x, y, z, evt.tilledBlock, evt.tilledMeta, 3);
	}

}
