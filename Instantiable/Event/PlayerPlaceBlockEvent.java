/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class PlayerPlaceBlockEvent extends Event {

	public final World world;
	public final int x;
	public final int y;
	public final int z;
	public final Block block;
	public final int metadata;
	private final ItemStack held;
	public final EntityPlayer player;

	public PlayerPlaceBlockEvent(World world, int x, int y, int z, Block b, int meta, ItemStack is, EntityPlayer ep) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		block = b;
		metadata = meta;
		held = is;
		player = ep;
	}

	public ItemStack getItem() {
		return held.copy();
	}

}
