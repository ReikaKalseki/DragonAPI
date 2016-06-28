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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;

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
		CICADA("cicada"),
		NAGASTONE("nagastone"),
		AURORA("auroraBlock");

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

				try {
					Field breaker = devices.getField("META_ANTIBUILDER");
					metabreaker = breaker.getInt(null);
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

				try {
					Field dimension = mod.getField("dimensionID");
					dim = dimension.getInt(null);
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

				for (int i = 0; i < BlockEntry.list.length; i++) {
					BlockEntry b = BlockEntry.list[i];
					try {
						Field f = twilight.getField(b.tag);
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

				for (int i = 0; i < ItemEntry.list.length; i++) {
					ItemEntry b = ItemEntry.list[i];
					try {
						Field f = items.getField(b.tag);
						b.item = (Item)f.get(null);
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
			catch (ClassNotFoundException e) {
				DragonAPICore.logError("Twilight Forest class not found! Cannot read its contents!");
				e.printStackTrace();
				this.logFailure(e);
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
