/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Mostly for rendering purposes. */
public class InertItem extends EntityItem {

	public InertItem(World world, ItemStack item) {
		super(world);
		this.setEntityItemStack(item);
	}

	@Override
	public void onUpdate() {
		age++;
	}

}
