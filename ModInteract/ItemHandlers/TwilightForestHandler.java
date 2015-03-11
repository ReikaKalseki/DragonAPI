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
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class TwilightForestHandler extends ModHandlerBase {

	public final int breakerMeta;

	public final int dimensionID;

	private boolean init = false;

	public static enum BlockEntry {
		ROOT("root"),
		TOWERMACHINE("towerDevice"),
		TOWERWOOD("towerWood"),
		MAZESTONE("mazestone"),
		TREECORE("magicLogSpecial"),
		SHIELD("shield"),
		PORTAL("portal"),
		HEDGE("hedge"),
		DEADROCK("deadrock"),
		FIREJET("fireJet"),
		FIREFLY("firefly"),
		MOONWORM("moonworm"),
		FIREFLYJAR("fireflyJar"),
		CICADA("cicada");

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

	public static enum ItemEntry {
		STEELLEAF("steeleafIngot"),
		IRONWOOD("ironwoodIngot"),
		MOONWORM("moonwormQueen"),
		NAGASCALE("nagaScale"),
		TORCHBERRY("torchberries"),
		TOWERKEY("towerKey"),
		FEATHER("feather"),
		CARMINITE("carminite"),
		TRANSFORMDUST("transformPowder");


		private final String tag;
		private Item item;

		private static final ItemEntry[] list = values();

		private ItemEntry(String s) {
			tag = s;
		}

		public Item getItem() {
			return item;
		}

		public ItemStack getStack() {
			return new ItemStack(item);
		}
	}

	private static final TwilightForestHandler instance = new TwilightForestHandler();

	private TwilightForestHandler() {
		super();
		int metabreaker = -1;
		int dim = 7;

		if (this.hasMod()) {
			try {
				Class twilight = this.getMod().getBlockClass();
				Class items = this.getMod().getItemClass();
				Class devices = Class.forName("twilightforest.block.BlockTFTowerDevice");
				Class mod = Class.forName("twilightforest.TwilightForestMod");
				Field breaker = devices.getField("META_ANTIBUILDER");
				Field dimension = mod.getField("dimensionID");
				metabreaker = breaker.getInt(null);
				dim = dimension.getInt(null);

				for (int i = 0; i < BlockEntry.list.length; i++) {
					BlockEntry b = BlockEntry.list[i];
					Field f = twilight.getField(b.tag);
					b.block = (Block)f.get(null);
				}

				for (int i = 0; i < ItemEntry.list.length; i++) {
					ItemEntry b = ItemEntry.list[i];
					Field f = items.getField(b.tag);
					b.item = (Item)f.get(null);
				}

				init = true;
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Twilight Forest class not found! Cannot read its contents!");
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

		breakerMeta = metabreaker;
		dimensionID = dim;
	}

	public static TwilightForestHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.TWILIGHT;
	}

	public boolean isToughBlock(Block b) {
		return b == BlockEntry.MAZESTONE.getBlock() || b == BlockEntry.SHIELD.getBlock() || b == BlockEntry.DEADROCK.getBlock();
	}

}
