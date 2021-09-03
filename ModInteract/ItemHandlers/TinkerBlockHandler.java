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
	private final Item materialID;

	public final Block searedBlockID; //table, basin, faucet
	public final Block smelteryTankID;
	public final Block smelteryBlockID; //includes all smeltery structure blocks
	public final Block netherSmelteryID;
	public final Block netherTankID; //includes all smeltery structure blocks

	public final BlockKey slimeDirt;
	public final BlockKey slimeGrass;
	public final Block slimeWater;
	public final Block congealedSlime;
	public final Block slimeTallGrass;

	public enum Materials {
		SLIMECRYSTAL(1),
		SEAREDBRICK(2),
		MOSSBALL(6),
		LAVACRYSTAL(7),
		NECROTICBONE(8),
		SLIMECRYSTAL2(17),
		SILKYCLOTH(25),
		SILKYJEWEL(26),
		GLUE(36),
		NETHERBRICK(37);

		private final int metadata;

		private Materials(int m) {
			metadata = m;
		}

		public ItemStack getItem() {
			return new ItemStack(instance.materialID, 1, metadata);
		}
	}

	public enum SmelteryBlocks {
		CONTROLLER(),
		DRAIN(),
		BRICK(),
		NONE(), //does not exist
		STONE(),
		COBBLE(),
		PAVER(), //cubes
		CRACKED(),
		ROAD(), //2x2 mini tiles
		FANCY(),
		CHISELED(), // aka circle stone brick
		CREEPER(); //also called chiseled

		public static final SmelteryBlocks[] list = values();

		private SmelteryBlocks() {

		}

		public ItemStack getItem(boolean nether) {
			return new ItemStack(nether ? instance.netherSmelteryID : instance.smelteryBlockID, 1, this.ordinal());
		}

		public boolean isBasicBuildingBlock() {
			switch(this) {
				case CONTROLLER:
				case DRAIN:
					return false;
				default:
					return true;
			}
		}
	}

	public enum SmelteryTanks {
		TANK(0), //The basic ISBRH one
		GLASS(1), //The full glass block
		WINDOW(2); //The oval one

		public final int metadata;

		public static final SmelteryTanks[] list = values();

		private SmelteryTanks(int meta) {
			metadata = meta;
		}

		public ItemStack getItem(boolean nether) {
			return new ItemStack(nether ? instance.netherTankID : instance.smelteryTankID, 1, metadata);
		}
	}

	private TinkerBlockHandler() {
		super();
		Block idgravel = null;
		Block idnether = null;
		Block idglass = null;
		Block idpane = null;
		Item idmaterial = null;
		Object pulse = null;

		Block idseared = null;
		Block idtank = null;
		Block idsmelt = null;
		Block idtanknether = null;
		Block idsmeltnether = null;

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

				Field tank = tink.getField("lavaTank");
				idtank = (Block)tank.get(null);

				Field smelt = tink.getField("smeltery");
				idsmelt = (Block)smelt.get(null);

				Field tanknether = tink.getField("lavaTankNether");
				idtanknether = (Block)tanknether.get(null);

				Field smeltnether = tink.getField("smelteryNether");
				idsmeltnether = (Block)smeltnether.get(null);
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


			slimeDirt = BlockKey.fromItem(ReikaItemHelper.lookupBlock(this.getMod(), "CraftedSoil", 5));
			slimeGrass = BlockKey.fromItem(ReikaItemHelper.lookupBlock(this.getMod(), "slime.grass", 5));
			congealedSlime = GameRegistry.findBlock(this.getMod().modLabel, "slime.gel");
			slimeWater = GameRegistry.findBlock(this.getMod().modLabel, "liquid.slime");
			slimeTallGrass = GameRegistry.findBlock(this.getMod().modLabel, "slime.grass.tall");
		}
		else {
			this.noMod();

			slimeDirt = null;
			slimeGrass = null;
			congealedSlime = null;
			slimeWater = null;
			slimeTallGrass = null;
		}

		pulsar = pulse;

		gravelOreID = idgravel;
		stoneOreID = idnether;
		clearGlassID = idglass;
		clearPaneID = idpane;
		searedBlockID = idseared;
		smelteryBlockID = idsmelt;
		smelteryTankID = idtank;
		netherSmelteryID = idsmeltnether;
		netherTankID = idtanknether;

		materialID = idmaterial;
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

	public boolean isSmelteryBlock(Block b) {
		return b == smelteryBlockID || b == smelteryTankID || b == netherSmelteryID || b == netherTankID;
	}

}
