/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.BlockCheck;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;


public abstract class BlockCheckFlowerProvider implements IFlowerProvider, IFlowerAcceptableRule {

	public final BlockCheck block;
	private final String id;

	public BlockCheckFlowerProvider(Block b, String id) {
		this(b, -1, id);
	}

	public BlockCheckFlowerProvider(Block b, int meta, String id) {
		this(new BlockKey(b, meta), id);
	}

	public BlockCheckFlowerProvider(BlockCheck bk, String id) {
		block = bk;
		this.id = id;

		FlowerManager.flowerRegistry.registerAcceptableFlower(block.asBlockKey().blockID, block.asBlockKey().metadata, this.id);
		FlowerManager.flowerRegistry.registerAcceptableFlowerRule(this, this.id);
	}

	@Override
	public final String getFlowerType() {
		return id;
	}

	public final boolean isAcceptedFlower(World world, int x, int y, int z) {
		return block.matchInWorld(world, x, y, z);//FlowerManager.flowerRegistry.isAcceptedFlower(id, world, x, y, z);
	}

	/*
	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
		return block.matchInWorld(world, x, y, z);
	}
	 */
	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable ip) {
		return false;
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products; //noop
	}

	@Override
	@Deprecated
	public final boolean growFlower(World world, IIndividual individual, int x, int y, int z) {
		return false;
	}

	@Override
	public final Set<IFlower> getFlowers() {
		return new HashSet();//Collections.unmodifiableSet(flowers);
	}

	public boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, int x, int y, int z) {
		return false;
	}

	public final boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		return false;
	}

	@Override
	public final boolean isAcceptableFlower(String flowerType, World world, int x, int y, int z) {
		return id.equals(flowerType) && block.matchInWorld(world, x, y, z);
	}



}
