/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArmorSlot extends Slot {

	private final EntityPlayer player;
	public final int armorType;

	public ArmorSlot(EntityPlayer ep, int index, int x, int y, int type) {
		super(ep.inventory, index, x, y);
		player = ep;
		armorType = type;
	}

	@Override
	public final int getSlotStackLimit()
	{
		return 1;
	}

	@Override
	public final boolean isItemValid(ItemStack is)
	{
		return is != null && is.getItem().isValidArmor(is, armorType, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getBackgroundIconIndex()
	{
		return ItemArmor.func_94602_b(armorType);
	}

}
