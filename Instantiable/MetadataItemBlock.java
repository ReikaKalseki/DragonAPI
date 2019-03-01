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

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;


public final class MetadataItemBlock extends ItemBlockWithMetadata {

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

}
