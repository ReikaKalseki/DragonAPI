/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.command;

import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import reika.dragonapi.instantiable.data.immutable.BlockKey;
import reika.dragonapi.libraries.io.ReikaChatHelper;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.registry.ReikaOreHelper;
import reika.dragonapi.libraries.registry.ReikaTreeHelper;
import reika.dragonapi.mod.registry.ModOreList;
import reika.dragonapi.mod.registry.ModWoodList;
import Reika.ChromatiCraft.API.TreeGetter;

public class BlockReplaceCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "blockreplace";
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);

		if (args.length == 3) {
			ArrayList<ReplaceCheck> li = new ArrayList();
			String[] parts = args[0].split(",");
			for (int i = 0; i < parts.length; i++) {
				ReplaceCheck rc = this.getCheck(parts[i]);
				if (rc != null)
					li.add(rc);
			}

			int range = ReikaJavaLibrary.safeIntParse(args[2]);
			if (range <= 0) {
				String sg = EnumChatFormatting.RED+"Invalid range '"+range+"'.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
				return;
			}

			BlockKey to = this.getBlockTo(args[1]);

			int px = MathHelper.floor_double(ep.posX);
			int py = MathHelper.floor_double(ep.posY);
			int pz = MathHelper.floor_double(ep.posZ);

			long start = System.currentTimeMillis();
			int count = 0;
			for (int i = -range; i <= range; i++) {
				for (int j = -range; j <= range; j++) {
					for (int k = -range; k <= range; k++) {
						int x = px+i;
						int y = py+j;
						int z = pz+k;
						if (y >= 0 && y <= 256) {
							Block b = ep.worldObj.getBlock(x, y, z);
							int meta = ep.worldObj.getBlockMetadata(x, y, z);
							for (ReplaceCheck rc : li) {
								if (rc.replace(b, meta)) {
									ep.worldObj.setBlock(x, y, z, to.blockID, to.metadata, 3);
									count++;
									break;
								}
							}
						}
					}
				}
			}

			if (count > 0) {
				String s2 = to.blockID.getLocalizedName()+" (Metadata "+to.metadata+")";
				long dur = System.currentTimeMillis()-start;
				String sg = EnumChatFormatting.GREEN+"Replaced "+count+" blocks with "+s2+" in range "+range+". Took "+dur+" ms.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
			}
		}
		else {
			String sg = EnumChatFormatting.RED+"You must specify a source block, a target block, and a range!";
			ReikaChatHelper.sendChatToPlayer(ep, sg);
		}
	}

	private BlockKey getBlockTo(String s) {
		String[] parts = s.split(":");
		Block b = Block.getBlockById(Integer.parseInt(parts[0]));
		int meta = 0;
		if (parts.length == 2) {
			meta = Integer.parseInt(parts[1]);
		}
		return new BlockKey(b, meta);
	}

	private ReplaceCheck getCheck(String s) {
		if (s.equals("all_ores")) {
			return new OreCheck();
		}
		else if (s.equals("all_trees")) {
			return new TreeCheck();
		}
		else if (s.startsWith("class_")) {
			return new ClassCheck(s.substring(6).toLowerCase(Locale.ENGLISH));
		}
		else if (s.equals("any") || s.equals("all") || s.equals("*")) {
			return new AnyCheck();
		}
		else {
			try {
				int id = Integer.parseInt(s);
				return new IDCheck(id);
			}
			catch (NumberFormatException e) {

			}
		}
		return null;
	}

	private static class TreeCheck extends ReplaceCheck {

		@Override
		protected boolean replace(Block b, int meta) {
			return ReikaTreeHelper.getTree(b, meta) != null || ReikaTreeHelper.getTreeFromLeaf(b, meta) != null || ModWoodList.isModWood(b, meta) || ModWoodList.isModLeaf(b, meta) || TreeGetter.isDyeLeaf(new ItemStack(b, 1, meta)) || TreeGetter.isRainbowLeaf(new ItemStack(b, 1, meta));
		}

	}

	private static class OreCheck extends ReplaceCheck {

		@Override
		protected boolean replace(Block b, int meta) {
			return ReikaOreHelper.isVanillaOre(b) || ModOreList.isModOre(b, meta);
		}

	}

	private static class ClassCheck extends ReplaceCheck {

		private final String blockClass;

		private ClassCheck(String c) {
			super();
			blockClass = c;
		}

		@Override
		protected boolean replace(Block b, int meta) {
			return b.getClass().getSimpleName().toLowerCase(Locale.ENGLISH).equals(blockClass);
		}

	}

	private static class IDCheck extends ReplaceCheck {

		private final int blockID;

		private IDCheck(int id) {
			super();
			blockID = id;
		}

		@Override
		protected boolean replace(Block b, int meta) {
			return Block.getIdFromBlock(b) == blockID;
		}

	}

	private static class AnyCheck extends ReplaceCheck {

		private AnyCheck() {
			super();
		}

		@Override
		protected boolean replace(Block b, int meta) {
			return b != Blocks.air;
		}

	}

	private abstract static class ReplaceCheck {

		protected abstract boolean replace(Block b, int meta);

	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
