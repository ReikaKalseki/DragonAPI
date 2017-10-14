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
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MantlePulsarHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.registry.GameRegistry;

public class TinkerBlockHandler extends ModHandlerBase {

	private static final TinkerBlockHandler instance = new TinkerBlockHandler();

	private final Object pulsar;

	public final Block gravelOreID;
	public final Block stoneOreID;
	public final Block clearGlassID;
	public final Block clearPaneID;
	public final Block searedBlockID;
	private final Item materialID;

	public final BlockKey slimeDirt;
	public final BlockKey slimeGrass;
	public final Block slimeWater;
	public final Block congealedSlime;
	public final Block slimeTallGrass;

	public enum Materials {
		SLIMECRYSTAL(1),
		MOSSBALL(6),
		LAVACRYSTAL(7),
		NECROTICBONE(8);

		private final int metadata;

		private Materials(int m) {
			metadata = m;
		}

		public ItemStack getItem() {
			return new ItemStack(instance.materialID, 1, metadata);
		}
	}

	private TinkerBlockHandler() {
		super();
		Block idgravel = null;
		Block idnether = null;
		Block idglass = null;
		Block idpane = null;
		Block idseared = null;
		Item idmaterial = null;
		Object pulse = null;

		if (this.hasMod()) {
			try {
				Class c = Class.forName("tconstruct.TConstruct");
				Field p = c.getField("pulsar");
				pulse = p.get(null);
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

			try {
				Class tink = ModList.TINKERER.getBlockClass();
				Field gravel = tink.getField("oreGravel");
				idgravel = (Block)gravel.get(null);

				Field ore = tink.getField("oreSlag");
				idnether = (Block)gravel.get(null);
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
				Class tink = Class.forName("tconstruct.smeltery.TinkerSmeltery");
				Field glass = tink.getField("clearGlass");
				idglass = (Block)glass.get(null);

				Field pane = tink.getField("glassPane");
				idpane = (Block)pane.get(null);

				Field sear = tink.getField("searedBlock");
				idseared = (Block)sear.get(null);
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

			try {
				Class tic = Class.forName("tconstruct.tools.TinkerTools");
				Field mat = tic.getField("materials");
				idmaterial = (Item)mat.get(null);
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

		pulsar = pulse;

		gravelOreID = idgravel;
		stoneOreID = idnether;
		clearGlassID = idglass;
		clearPaneID = idpane;
		searedBlockID = idseared;

		materialID = idmaterial;

		slimeDirt = new BlockKey(ReikaItemHelper.lookupBlock(this.getMod(), "CraftedSoil", 5));
		slimeGrass = new BlockKey(ReikaItemHelper.lookupBlock(this.getMod(), "slime.grass", 5));
		congealedSlime = GameRegistry.findBlock(this.getMod().modLabel, "slime.gel");
		slimeWater = GameRegistry.findBlock(this.getMod().modLabel, "liquid.slime");
		slimeTallGrass = GameRegistry.findBlock(this.getMod().modLabel, "slime.grass.tall");
	}

	public static enum Pulses {
		SMELTERY("Tinkers' Smeltery"),
		TOOLS("Tinkers' Tools"),
		WEAPONS("Tinkers' Weaponry"),
		ARMOR("Tinkers' Armory"),
		MECH("Tinkers' Mechworks"),
		WORLD("Tinkers' World");

		private final String id;

		private Pulses(String s) {
			id = s;
		}

		public boolean isLoaded() {
			return MantlePulsarHandler.isPulseLoaded(instance.pulsar, id);
		}
	}

	public static TinkerBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return gravelOreID != null && stoneOreID != null;// && clearGlassID != null && clearPaneID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.TINKERER;
	}

	public boolean isGravelOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return Block.getBlockFromItem(block.getItem()) == gravelOreID;
	}

	public boolean isNetherOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return Block.getBlockFromItem(block.getItem()) == stoneOreID && block.getItemDamage() < 3;
	}

	public boolean isSlimeIslandBlock(Block b, int meta) {
		if (slimeDirt.match(b, meta) || slimeGrass.match(b, meta) || b == slimeWater || b == slimeTallGrass || b == congealedSlime)
			return true;
		return ModWoodList.getModWood(b, meta) == ModWoodList.SLIME || ModWoodList.getModWoodFromLeaf(b, meta) == ModWoodList.SLIME;
	}

}
