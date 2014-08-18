/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import Reika.DragonAPI.Interfaces.BlockEnum;
import Reika.DragonAPI.Interfaces.ItemEnum;
import Reika.DragonAPI.Interfaces.RegistrationList;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EnumCreativeTab extends CreativeTabs {

	private final String name;

	public EnumCreativeTab(String name) {
		super(name);
		this.name = name;
	}

	@Override
	public final String getTabLabel()
	{
		return name;
	}

	@Override
	public final String getTranslatedTabLabel()
	{
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void displayAllReleventItems(List li) {
		RegistrationList[] list = this.getRegistry();
		for (int i = 0; i < list.length; i++) {
			RegistrationList r = list[i];
			Item item = r instanceof ItemEnum ? ((ItemEnum)r).getItemInstance() : Item.getItemFromBlock(((BlockEnum)r).getBlockInstance());
			CreativeTabs[] c = item.getCreativeTabs();
			for (int k = 0; k < c.length; k++) {
				if (c[k] == this)
					item.getSubItems(item, this, li);
			}
		}
	}

	protected abstract RegistrationList[] getRegistry();

	@Override
	@SideOnly(Side.CLIENT)
	public final Item getTabIconItem() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public abstract ItemStack getIconItemStack();
}
