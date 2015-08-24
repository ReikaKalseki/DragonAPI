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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;


public abstract class BasicFlowerProvider implements IFlowerProvider {

	public final BlockKey block;

	private final List<IFlower> flowers = new ArrayList();

	public BasicFlowerProvider(Block b, boolean plantable) {
		this(b, -1, plantable);
	}

	public BasicFlowerProvider(Block b, int meta, boolean plantable) {
		this(new BlockKey(b, meta), plantable);
	}

	public BasicFlowerProvider(BlockKey bk, boolean plantable) {
		block = bk;

		if (bk.hasMetadata()) {
			flowers.add(new BasicFlower(block, plantable));
		}
		else {
			for (int i = 0; i < 16; i++) {
				flowers.add(new BasicFlower(new BlockKey(block.blockID, i), plantable));
			}
		}
	}

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
		return block.matchInWorld(world, x, y, z);
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable ip) {
		return false;
	}

	@Override
	public boolean growFlower(World world, IIndividual individual, int x, int y, int z) {
		return false;
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products; //noop
	}

	@Override
	public final List<IFlower> getFlowers() {
		return Collections.unmodifiableList(flowers);
	}

}
