/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Interfaces.Registry.OreEnum;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public abstract class EnumOreBlock extends Block {

	private boolean setHarvest = false;

	protected EnumOreBlock(Material mat) {
		super(mat);
	}

	public final void register() {
		for (int i = 0; i < 16; i++) {
			OreEnum ore = this.getOre(i);
			if (ore != null)
				this.setHarvestLevel(ore.getHarvestTool(), ore.getHarvestLevel(), i);
		}
		setHarvest = true;
	}

	public abstract OreEnum getOre(int meta);

	public final OreEnum getOre(World world, int x, int y, int z) {
		return this.getOre(world.getBlockMetadata(x, y, z));
	}

	@Override
	public final int getHarvestLevel(int meta) {
		return super.getHarvestLevel(meta);//this.getOre(meta).getHarvestLevel();
	}

	@Override
	public final void setHarvestLevel(String toolClass, int level, int meta) {
		OreEnum ore = this.getOre(meta);
		if (setHarvest && ore.enforceHarvestLevel()) {
			DragonAPICore.log("Harvest level of "+ore+" was not set; it chose to disallow it.");
			return;
		}
		super.setHarvestLevel(toolClass, level, meta);
	}

	@Override
	public final boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv)
	{
		OreEnum ore = this.getOre(world, x, y, z);
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		boolean flag = super.removedByPlayer(world, player, x, y, z, harv);
		if (harv && flag) {
			this.onHarvested(world, x, y, z, b, meta, ore, player);
			if (!ore.dropsSelf(world, x, y, z) && !EnchantmentHelper.getSilkTouchModifier(player))
				ReikaWorldHelper.splitAndSpawnXP(world, x+0.5F, y+0.5F, z+0.5F, this.droppedXP(ore, world, x, y, z));
		}
		return flag;
	}

	protected void onHarvested(World world, int x, int y, int z, Block b, int meta, OreEnum ore, EntityPlayer ep) {

	}

	private static int droppedXP(OreEnum ore, World world, int x, int y, int z) {
		return ReikaRandomHelper.doWithChance(ore.getXPDropped(world, x, y, z)) ? 1 : 0;
	}
}
