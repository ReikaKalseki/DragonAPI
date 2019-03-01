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

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;

public class ItemSpawner extends Item {

	public ItemSpawner() {
		super();
		this.setHasSubtypes(true);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List par3List, boolean par4) {
		if (is.stackTagCompound == null)
			return;
		ReikaSpawnerHelper.generateSpawnerTooltip(is, par3List);
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int k = 50; k <= 66; k++) { //50-66 hostile, 90-99 animal, 120 villager
			ItemStack spw = new ItemStack(par1, 1, 0);
			if (spw.stackTagCompound == null)
				spw.setTagCompound(new NBTTagCompound());
			spw.stackTagCompound.setString("Spawner", EntityList.getStringFromID(k));
			par3List.add(spw);
		}
		for (int k = 90; k <= 99; k++) {
			ItemStack spw = new ItemStack(par1, 1, 0);
			if (spw.stackTagCompound == null)
				spw.setTagCompound(new NBTTagCompound());
			spw.stackTagCompound.setString("Spawner", EntityList.getStringFromID(k));
			par3List.add(spw);
		}
		ItemStack spw = new ItemStack(par1, 1, 0);
		if (spw.stackTagCompound == null)
			spw.setTagCompound(new NBTTagCompound());
		spw.stackTagCompound.setString("Spawner", EntityList.getStringFromID(120));
		par3List.add(spw);
		return;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		String name = ReikaSpawnerHelper.getSpawnerFromItemNBT(is);
		if (name == null) {
			ReikaChatHelper.write("Spawner has no type set!");
			return false;
		}
		if (!this.isValidDimensionForSpawner(is, world)) {
			ReikaChatHelper.write(ReikaSpawnerHelper.getSpawnerFromItemNBT(is)+" cannot be placed in dimension "+world.provider.getDimensionName()+"!");
			return false;
		}
		if (!ReikaWorldHelper.softBlocks(world, x, y, z)) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z))
				return false;
		}
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, Blocks.mob_spawner);
			TileEntityMobSpawner spw = (TileEntityMobSpawner)world.getTileEntity(x, y, z);
			if (spw != null) {
				world.playSoundEffect(x+0.5, y+0.5, z+0.5, "step.stone", 1F, 1.5F);
				MobSpawnerBaseLogic lgc = spw.func_145881_a();
				ReikaSpawnerHelper.setSpawnerFromItemNBT(is, spw, true);
				lgc.spawnDelay = Math.max(lgc.minSpawnDelay, itemRand.nextInt(Math.min(900, 1+lgc.maxSpawnDelay))); //20s delay
			}
		}
		//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("%d", world.getBlockMetadata(x, y, z)));
		return true;
	}

	@Override
	public int getMetadata (int damageValue) {
		return damageValue;
	}

	private boolean isValidDimensionForSpawner(ItemStack is, World world) {
		int dim = world.provider.dimensionId;
		String name = ReikaSpawnerHelper.getSpawnerFromItemNBT(is);
		if (ReikaTwilightHelper.isTwilightForestBoss(name))
			return dim == ReikaTwilightHelper.getDimensionID();
		if (name.equals("EnderDragon"))
			return dim == 1;
		switch(dim) {
			case 0:
				break;
			case 1: //end
				break;
			case -1: //nether
				break;
			case 7: //twilight forest
				break;
		}
		return true;
	}

	@Override
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return "Monster Spawner";
	}
}
