/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.itemhandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;

public class ChiselBlockHandler extends ModHandlerBase {

	private boolean init = false;

	public static enum BlockEntry {
		MARBLE("marble"),
		SANDSTONE("sandstone"),
		LIMESTONE("limestone"),
		GRANITE("granite"),
		DIORITE("diorite"),
		ANDESITE("andesite");

		private final String tag;
		private Block block;

		private static final BlockEntry[] list = values();

		private BlockEntry(String s) {
			tag = s;
		}

		public Block getBlock() {
			return block;
		}
	}

	private static final ChiselBlockHandler instance = new ChiselBlockHandler();

	private ChiselBlockHandler() {
		super();
		int metabreaker = -1;
		int dim = 7;

		if (this.hasMod()) {
			Class block = this.getMod().getBlockClass();
			Class items = this.getMod().getItemClass();

			for (int i = 0; i < BlockEntry.list.length; i++) {
				BlockEntry b = BlockEntry.list[i];
				try {
					Field f = block.getField(b.tag);
					b.block = (Block)f.get(null);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (SecurityException e) {
					DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalArgumentException e) {
					DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (IllegalAccessException e) {
					DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
				}
				catch (NullPointerException e) {
					DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
					e.printStackTrace();
					this.logFailure(e);
				}
			}

			init = true;
		}
		else {
			this.noMod();
		}
	}

	public static ChiselBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.CHISEL;
	}

	public static boolean isWorldgenBlock(Block b, int meta) {
		if (meta != 0)
			return false;
		return b == BlockEntry.MARBLE.getBlock() || b == BlockEntry.SANDSTONE.getBlock() || b == BlockEntry.LIMESTONE.getBlock() || b == BlockEntry.GRANITE.getBlock() || b == BlockEntry.DIORITE.getBlock() || b == BlockEntry.ANDESITE.getBlock();
	}

}
