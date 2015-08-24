/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import net.minecraft.block.Block;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import forestry.api.genetics.IFlower;


public final class BasicFlower implements IFlower {

	public final BlockKey block;

	private final boolean plantable;

	private double weight;

	public BasicFlower(BlockKey bk, boolean plant) {
		block = bk;
		weight = 1;
		plantable = plant;
	}

	@Override
	public int compareTo(IFlower o) {
		return 0;
	}

	@Override
	public Block getBlock() {
		return block.blockID;
	}

	@Override
	public int getMeta() {
		return block.metadata;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double w) {
		weight = w;
	}

	@Override
	public boolean isPlantable() {
		return plantable;
	}

}
