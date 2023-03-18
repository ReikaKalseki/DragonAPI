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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlayerPlaceBlockEvent extends WorldPositionEvent {

	public final Block block;
	public final int metadata;
	private final ItemStack held;
	public final EntityPlayer player;
	public final ForgeDirection side;

	public PlayerPlaceBlockEvent(World world, int x, int y, int z, int side, Block b, int meta, ItemStack is, EntityPlayer ep) {
		super(world, x, y, z);
		block = b;
		metadata = meta;
		held = is;
		player = ep;
		this.side = ForgeDirection.VALID_DIRECTIONS[side];
	}

	public ItemStack getItem() {
		return held.copy();
	}

	public static boolean fire(World world, int x, int y, int z, int side, Block b, int meta, ItemStack is, EntityPlayer ep) {
		return MinecraftForge.EVENT_BUS.post(new PlayerPlaceBlockEvent(world, x, y, z, side, b, meta, is, ep));
	}

	public static boolean fireTryPlace(World world, int x, int y, int z, Block b, int meta, int flags, int side, EntityPlayer ep, ItemStack is) {
		if (fire(world, x, y, z, side, b, meta, is, ep))
			return false;
		else
			return world.setBlock(x, y, z, b, meta, flags);
	}

}
