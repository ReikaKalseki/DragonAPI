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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.Exception.MisuseException;

public class ItemReq {

	public final Item itemID;
	public final int metadata;
	public final float chanceToUse;
	private int numberNeeded;

	private NBTTagCompound nbt;

	public ItemReq(Item id, int meta, float chance) {
		itemID = id;
		metadata = meta;
		if (chance > 1)
			chance = 1;
		chanceToUse = chance;
		numberNeeded = -1;
	}

	private ItemReq(Item id, float chance) {
		this(id, 0, chance);
	}

	public ItemReq(Block b, float chance) {
		this(b, 0, chance);
	}

	public ItemReq(Block b, int meta, float chance) {
		this(Item.getItemFromBlock(b), meta, chance);
	}

	public ItemReq(Item id, int meta, int number) {
		if (number < 1) {
			throw new MisuseException("You must specify a valid number of items required!");
		}
		itemID = id;
		metadata = meta;
		chanceToUse = -1;
		numberNeeded = number;
	}

	public boolean alwaysConsume() {
		return numberNeeded != -1 || chanceToUse == 1;
	}

	public void setNBTTag(NBTTagCompound tag) {
		nbt = tag;
	}

	public int getNumberNeeded() {
		return numberNeeded;
	}

	public void use() {
		if (numberNeeded > 0)
			numberNeeded--;
	}

	public boolean callAndConsume() {
		if (numberNeeded > 0)
			numberNeeded--;
		int chance = (int)(1F/chanceToUse);
		Random r = new Random();
		if (r.nextInt(chance) > 0)
			return false;
		else
			return true;
	}

	public ItemStack asItemStack() {
		if (numberNeeded != -1)
			return new ItemStack(itemID, numberNeeded, metadata);
		else if (this.alwaysConsume())
			return new ItemStack(itemID, 1, metadata);
		else
			return new ItemStack(itemID, 1, metadata);//return new ItemStack.getItem, (int)(100*chanceToUse), metadata);
	}

}
