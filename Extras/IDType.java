/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public enum IDType {

	BLOCK(4095),
	ITEM(32767),
	ENTITY(Integer.MAX_VALUE),
	BIOME(254),
	POTION(Potion.potionTypes.length-1),
	ENCHANTMENT(Enchantment.enchantmentsList.length-1),
	FLUID(Integer.MAX_VALUE),
	FLUIDCONTAINER(Integer.MAX_VALUE);

	public static final IDType[] list = values();

	public final int maxValue;

	private IDType(int max) {
		maxValue = max;
	}

	public Object getValue(int id) {
		switch(this) {
			case BIOME:
				return BiomeGenBase.biomeList[id];
			case BLOCK:
				return Block.getBlockById(id);
			case ENCHANTMENT:
				return Enchantment.enchantmentsList[id];
			case ENTITY:
				return null;//EntityRegistry.instance().lookupModSpawn(null, id);
			case FLUID:
				return FluidRegistry.getFluid(id);
			case FLUIDCONTAINER:
				return null;
			case ITEM:
				return Item.getItemById(id);
			case POTION:
				return Potion.potionTypes[id];
		}
		return null;
	}

	public boolean hasValue(int id) {
		return this.getValue(id) != null;
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}
}
