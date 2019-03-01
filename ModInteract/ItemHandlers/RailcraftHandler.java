/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public final class RailcraftHandler extends ModHandlerBase {

	private static final RailcraftHandler instance = new RailcraftHandler();

	public final Block hiddenID;

	private final Block cubeID;
	public final Block oreID;

	public final Item firestoneID;

	private RailcraftHandler() {
		super();
		Block idhidden = null;
		Block idcube = null;
		Block idore = null;
		Item idfirestone = null;
		if (this.hasMod()) {
			try {
				Class c = Class.forName("mods.railcraft.common.blocks.hidden.BlockHidden");
				Field block = c.getDeclaredField("block");
				block.setAccessible(true);
				Block b = (Block)block.get(null);
				idhidden = b; //may be disabled

				c = Class.forName("mods.railcraft.common.blocks.aesthetics.cube.BlockCube");
				block = c.getDeclaredField("instance");
				block.setAccessible(true);
				b = (Block)block.get(null);
				idcube = b;

				c = Class.forName("mods.railcraft.common.blocks.ore.BlockOre");
				block = c.getDeclaredField("instance");
				block.setAccessible(true);
				b = (Block)block.get(null);
				idore = b;

				c = Class.forName("mods.railcraft.common.items.firestone.ItemFirestoneRaw");
				Field item = c.getDeclaredField("item");
				item.setAccessible(true);
				Item i = (Item)item.get(null);
				idfirestone = i;
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
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
		else {
			this.noMod();
		}
		hiddenID = idhidden;
		cubeID = idcube;
		oreID = idore;
		firestoneID = idfirestone;
	}

	public static enum Blocks {
		COKE(),
		CONCRETE(),
		STEEL(),
		INFERNALBRICK(),
		CRUSHEDOBSIDIAN(),
		SANDYBRICK(),
		ABYSSAL(),
		QUARRIED(),
		CREOSOTE(),
		COPPER(),
		TIN(),
		LEAD();

		public int getMetadata() {
			return this.ordinal();
		}

		public boolean match(Block b, int meta) {
			return b == instance.cubeID && meta == this.getMetadata();
		}

		public ItemStack getItem() {
			return new ItemStack(instance.cubeID, 1, this.getMetadata());
		}
	}

	public boolean isPoorOre(Block b, int meta) {
		return b == oreID && meta >= 7;
	}

	public boolean isDarkOre(Block b, int meta) {
		return b == oreID && ReikaMathLibrary.isValueInsideBoundsIncl(2, 4, meta);
	}

	public static RailcraftHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return hiddenID != null && firestoneID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.RAILCRAFT;
	}

}
