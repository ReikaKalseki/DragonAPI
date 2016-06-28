/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import reika.dragonapi.interfaces.registry.BlockEnum;
import reika.dragonapi.libraries.ReikaRegistryHelper;


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

}
