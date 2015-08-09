/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

public class WailaTechnicalOverride implements IWailaDataProvider {

	public static final WailaTechnicalOverride instance = new WailaTechnicalOverride();

	private final Collection<Block> blocks = new ArrayList();

	private WailaTechnicalOverride() {

	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		Block b = acc.getBlock();
		int meta = acc.getMetadata();
		if (b == Blocks.wooden_door || b == Blocks.iron_door) {
			meta = meta&7;
		}
		else if (b == Blocks.skull) {
			return null;
		}
		return new ItemStack(b.getItemDropped(meta, new Random(), 0));
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		for (int i = 0; i < tip.size(); i++) {
			String s = tip.get(i);
			String rep = s.replace("DragonAPI", "Minecraft");
			tip.set(i, rep);
		}
		return tip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	public void addBlock(Block b) {
		blocks.add(b);
	}

	@ModDependent(ModList.WAILA)
	public static void registerOverride(IWailaRegistrar reg) {
		for (Block b : instance.blocks) {
			Class c = b.getClass();
			reg.registerHeadProvider(instance, c);
			reg.registerBodyProvider(instance, c);
			reg.registerTailProvider(instance, c);
			reg.registerStackProvider(instance, c);
		}
	}

}
