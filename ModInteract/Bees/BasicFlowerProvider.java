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
import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;


public abstract class BasicFlowerProvider implements IFlowerProvider {

	public final BlockKey block;
	private final String id;

	//private final List<IFlower> flowers = new ArrayList();

	public BasicFlowerProvider(Block b, String id) {
		this(b, -1, id);
	}

	public BasicFlowerProvider(Block b, int meta, String id) {
		this(new BlockKey(b, meta), id);
	}

	public BasicFlowerProvider(BlockKey bk, String id) {
		block = bk;
		this.id = id;

		if (bk.hasMetadata()) {
			FlowerManager.flowerRegistry.registerAcceptableFlower(block.blockID, bk.metadata, id);
		}
		else {
			FlowerManager.flowerRegistry.registerAcceptableFlower(block.blockID, id);
		}

		//FlowerManager.flowerRegistry.registerGrowthRule(this, id);
	}

	@Override
	public final String getFlowerType() {
		return id;
	}

	public final boolean isAcceptedFlower(World world, int x, int y, int z) {
		return FlowerManager.flowerRegistry.isAcceptedFlower(id, world, x, y, z);
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



}
