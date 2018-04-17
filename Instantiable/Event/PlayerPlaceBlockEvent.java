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
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlayerPlaceBlockEvent extends PositionEvent {

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

}
