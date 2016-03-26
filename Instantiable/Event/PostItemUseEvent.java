/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;


public class PostItemUseEvent extends PlayerEvent {

	private final ItemStack item;

	public final World world;
	public final int x;
	public final int y;
	public final int z;
	public final int side;

	public final float hitX;
	public final float hitY;
	public final float hitZ;

	public PostItemUseEvent(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int s, float a, float b, float c) {
		super(player);

		item = is;

		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		side = s;

		hitX = a;
		hitY = b;
		hitZ = c;
	}

	public ItemStack getItem() {
		return item != null ? item.copy() : null;
	}

	public static void fire(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int s, float a, float b, float c) {
		MinecraftForge.EVENT_BUS.post(new PostItemUseEvent(is, player, world, x, y, z, s, a, b, c));
	}

}
