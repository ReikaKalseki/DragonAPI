/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public final class PlayerReference {

	public final UUID uid;
	private final ItemStack heldItem;

	public PlayerReference(EntityPlayer ep) {
		uid = ep.getUniqueID();
		heldItem = ep.getCurrentEquippedItem();
	}

	public ItemStack getHeldItem() {
		return heldItem != null ? heldItem.copy() : null;
	}

	public EntityPlayer getPlayer(World world) {
		return world.func_152378_a(uid);
	}

}
