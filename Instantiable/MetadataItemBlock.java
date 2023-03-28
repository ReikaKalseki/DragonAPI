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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MetadataItemBlock extends ItemBlockWithMetadata {

	public MetadataItemBlock(Block b) {
		super(b, b);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		BlockEnum e = (BlockEnum)ReikaRegistryHelper.getRegistryForObject(field_150939_a);
		if (e == null)
			return super.getItemStackDisplayName(is);
		return e.hasMultiValuedName() ? e.getMultiValuedName(is.getItemDamage()) : e.getBasicName();
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		return super.getUnlocalizedName()+"."+is.getItemDamage();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item it, CreativeTabs tab, List li) {
		BlockEnum e = (BlockEnum)ReikaRegistryHelper.getRegistryForObject(field_150939_a);
		if (e == null) {
			super.getSubItems(it, tab, li);
		}
		else {
			for (int i = 0; i < e.getNumberMetadatas(); i++) {
				li.add(new ItemStack(it, 1, i));
			}
		}
	}

	public static class MetadataItemBlockFixedName extends MetadataItemBlock {

		public MetadataItemBlockFixedName(Block b) {
			super(b);
		}

		@Override
		public String getUnlocalizedName(ItemStack is) {
			return field_150939_a.getUnlocalizedName();
		}
	}

}
