/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;

import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class ItemBlockCustomLocalization extends ItemBlock {

	private BlockEnum object;

	public ItemBlockCustomLocalization(Block b) {
		super(b);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		if (object == null) {
			ReikaJavaLibrary.pConsole(Level.ERROR, this+", block "+field_150939_a+" has a null block enum!");
			//ReikaJavaLibrary.dumpStack();
			return is.getItem().getUnlocalizedName(is);
		}
		return object.hasMultiValuedName() ? object.getMultiValuedName(is.getItemDamage()) : object.getBasicName();
	}

	public void setEnumObject(BlockEnum b) {
		object = b;
	}

}
