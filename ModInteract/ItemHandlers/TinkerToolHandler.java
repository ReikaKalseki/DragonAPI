/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class TinkerToolHandler extends ModHandlerBase {

	private static final TinkerToolHandler instance = new TinkerToolHandler();

	public enum Tools {
		PICK("pickaxe"),
		SPADE("shovel"),
		AXE("hatchet"),
		BROADSWORD("broadsword"),
		LONGSWORD("longsword"),
		RAPIER("rapier"),
		DAGGER("dagger"),
		CUTLASS("cutlass"),
		PAN("frypan"),
		SIGN("battlesign"),
		CHISEL("chisel"),
		MATTOCK("mattock"),
		SCYTHE("scythe"),
		LUMBERAXE("lumberaxe"),
		CLEAVER("cleaver"),
		HAMMER("hammer"),
		BATTLEAX("battleaxe");

		private Item item;
		private final String field;

		private static final Tools[] list = values();

		private Tools(String s) {
			field = s;
		}

		public Item getItem() {
			return item;
		}

	}

	public enum ToolBlocks {
		TOOLSTATION("toolStationWood"),
		TOOLSTATION2("toolStationStone"),
		TOOLFORGE("toolForge"),
		WORKBENCH("craftingStationWood"),
		CRAFTSLAB("craftingSlabWood"),
		FURNACE("furnaceSlab");

		private Block item;
		private final String field;

		private static final ToolBlocks[] list = values();

		private ToolBlocks(String s) {
			field = s;
		}

		public Block getItem() {
			return item;
		}

	}

	private boolean init = false;

	private TinkerToolHandler() {
		super();

		if (this.hasMod()) {
			try {
				Class tic = Class.forName("tconstruct.tools.TinkerTools");
				for (int i = 0; i < Tools.list.length; i++) {
					Tools t = Tools.list[i];
					Field f = tic.getField(t.field);
					t.item = (Item)f.get(null);
				}

				for (int i = 0; i < ToolBlocks.list.length; i++) {
					ToolBlocks t = ToolBlocks.list[i];
					Field f = tic.getField(t.field);
					t.item = (Block)f.get(null);
				}

				init = true;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
	}

	public static TinkerToolHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.TINKERER;
	}

	public boolean isPick(ItemStack is) {
		return is != null && is.getItem() == Tools.PICK.item;
	}

	public boolean isHammer(ItemStack is) {
		return is != null && is.getItem() == Tools.HAMMER.item;
	}

	public int getHarvestLevel(ItemStack is) {
		if (is.stackTagCompound == null)
			return 0;
		NBTTagCompound tag = is.stackTagCompound.getCompoundTag("InfiTool");
		return tag.getInteger("HarvestLevel");
	}

	public boolean isStoneOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 1;
	}

	public boolean isIronOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 2;
	}

	public boolean isDiamondOrBetter(ItemStack is) {
		return this.getHarvestLevel(is) >= 3;
	}

	public boolean isToolStation(Block b) {
		return b == ToolBlocks.TOOLSTATION.item || b == ToolBlocks.TOOLSTATION2.item;
	}

	public boolean isWorkbench(Block b) {
		return b == ToolBlocks.WORKBENCH.item || b == ToolBlocks.CRAFTSLAB.item;
	}

}
