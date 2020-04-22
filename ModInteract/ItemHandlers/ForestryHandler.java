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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Interfaces.Registry.CropHandler;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;

import cpw.mods.fml.common.Loader;
import forestry.api.arboriculture.ITree;

public class ForestryHandler extends ModHandlerBase implements CropHandler {

	private boolean init = false;

	private Field crateList;

	public final Block extraTreeLog;

	private static final ForestryHandler instance = new ForestryHandler();

	public enum ItemEntry {
		APATITE("forestry.core.items.ItemRegistryCore", "apatite"),
		FERTILIZER("forestry.core.items.ItemRegistryCore", "fertilizerCompound"),
		SAPLING("forestry.arboriculture.items.ItemRegistryArboriculture", "sapling"),
		COMB("forestry.apiculture.items.ItemRegistryApiculture", "beeComb"),
		HONEY("forestry.apiculture.items.ItemRegistryApiculture", "honeyDrop"),
		HONEYDEW("forestry.apiculture.items.ItemRegistryApiculture", "honeydew"),
		JELLY("forestry.apiculture.items.ItemRegistryApiculture", "royalJelly"),
		PROPOLIS("forestry.apiculture.items.ItemRegistryApiculture", "propolis"),
		WAX("forestry.core.items.ItemRegistryCore", "beeswax"),
		REFWAX("forestry.core.items.ItemRegistryCore", "refractoryWax"),
		PHOSPHOR("forestry.core.items.ItemRegistryCore", "phosphor"),
		MULCH("forestry.core.items.ItemRegistryCore", "mulch"),
		PEAT("forestry.core.items.ItemRegistryCore", "peat"),
		ASH("forestry.core.items.ItemRegistryCore", "ash"),
		POLLEN("forestry.apiculture.items.ItemRegistryApiculture", "pollenCluster"),
		TREEPOLLEN("forestry.arboriculture.items.ItemRegistryArboriculture", "pollenFertile"),
		QUEEN("forestry.apiculture.items.ItemRegistryApiculture", "beeQueenGE"),
		PRINCESS("forestry.apiculture.items.ItemRegistryApiculture", "beePrincessGE"),
		DRONE("forestry.apiculture.items.ItemRegistryApiculture", "beeDroneGE"),
		LARVA("forestry.apiculture.items.ItemRegistryApiculture", "beeLarvaeGE"),
		BUTTERFLY("forestry.lepidopterology.items.ItemRegistryLepidopterology", "butterflyGE"),
		CATERPILLAR("forestry.lepidopterology.items.ItemRegistryLepidopterology", "caterpillarGE"),
		CRAFTING("forestry.core.items.ItemRegistryCore", "craftingMaterial"),
		BASICFRAME("forestry.apiculture.items.ItemRegistryApiculture", "frameUntreated"),
		IMPREGFRAME("forestry.apiculture.items.ItemRegistryApiculture", "frameImpregnated"),
		PROVENFRAME("forestry.apiculture.items.ItemRegistryApiculture", "frameProven");

		private final String reg;
		private final String tag;
		private Item item;

		private static final ItemEntry[] list = values();

		private ItemEntry(String c, String id) {
			reg = c;
			tag = id;
		}

		public Item getItem() {
			return item;
		}

		private Object getRegistryObject() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
			String n = reg.split("\\.")[1];
			n = ReikaStringParser.capFirstChar(n);
			n = "forestry.plugins.Plugin"+n;
			Class c = Class.forName(n);
			Field f = c.getDeclaredField("items");
			f.setAccessible(true);
			return f.get(null);
		}
	}

	public enum BlockEntry {
		SAPLING("forestry.arboriculture.blocks.BlockRegistryArboriculture", "saplingGE"),
		LEAF("forestry.arboriculture.blocks.BlockRegistryArboriculture", "leaves"),
		LOG("forestry.arboriculture.blocks.BlockRegistryArboriculture", "logs"),
		FIRELOG("forestry.arboriculture.blocks.BlockRegistryArboriculture", "logsFireproof"),
		HIVE("forestry.apiculture.blocks.BlockRegistryApiculture", "beehives"),
		SOIL("forestry.core.blocks.BlockRegistryCore", "soil");

		private final String reg;
		private final String tag;
		private Block item;

		private static final BlockEntry[] list = values();

		private BlockEntry(String c, String id) {
			reg = c;
			tag = id;
		}

		public Block getBlock() {
			return item;
		}

		private Object getRegistryObject() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
			String n = reg.split("\\.")[1];
			n = ReikaStringParser.capFirstChar(n);
			n = "forestry.plugins.Plugin"+n;
			Class c = Class.forName(n);
			Field f = c.getDeclaredField("blocks");
			f.setAccessible(true);
			return f.get(null);
		}
	}

	public static enum CraftingMaterials {
		PULSEDUST(),
		PULSEMESH(),
		SILKWISP(),
		WOVENSILK(),
		DISSIPATION(),
		ICESHARD(),
		SCENTEDPANEL();

		public ItemStack getItem() {
			return new ItemStack(ItemEntry.CRAFTING.getItem(), 1, this.ordinal());
		}
	}

	private ForestryHandler() {
		super();
		Block extra = null;
		if (this.hasMod()) {
			for (int i = 0; i < ItemEntry.list.length; i++) {
				ItemEntry ie = ItemEntry.list[i];
				try {
					Class c = Class.forName(ie.reg);
					Field f = c.getDeclaredField(ie.tag); //is no longer enum object
					Object reg = ie.getRegistryObject();
					ie.item = (Item)f.get(reg);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
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
				catch (ClassNotFoundException e) {
					DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
			}

			for (int i = 0; i < BlockEntry.list.length; i++) {
				BlockEntry ie = BlockEntry.list[i];
				try {
					Class c = Class.forName(ie.reg);
					Field f = c.getDeclaredField(ie.tag); //is no longer enum object
					Object reg = ie.getRegistryObject();
					ie.item = (Block)f.get(reg);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
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
				catch (ClassNotFoundException e) {
					DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
				}
			}

			try {
				Class c = Class.forName("forestry.plugins.PluginStorage");
				crateList = c.getDeclaredField("crates");
				crateList.setAccessible(true);
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

			init = true;

			if (Loader.isModLoaded("ExtraTrees")) {
				try {
					Class c = Class.forName("binnie.extratrees.ExtraTrees");
					Field f = c.getDeclaredField("blockLog");
					f.setAccessible(true);
					extra = (Block)f.get(null);
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
		}
		else {
			this.noMod();
		}
		extraTreeLog = extra;
	}

	public static ForestryHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.FORESTRY;
	}

	public enum Combs {

		HONEY(0),
		SIMMERING(2),
		STRINGY(3),
		FROZEN(4),
		DRIPPING(5),
		SILKY(6),
		PARCHED(7),
		MOSSY(15),
		MELLOW(16);

		public final int damageValue;

		private Combs(int dmg) {
			damageValue = dmg;
		}

		public ItemStack getItem() {
			return new ItemStack(ItemEntry.COMB.getItem(), 1, damageValue);
		}
	}

	public static enum SoilType {
		HUMUS(),
		BOG_EARTH(),
		PEAT();

		public static SoilType getTypeFromMeta(int meta) {
			int type = meta & 0x3;
			int maturity = meta >> 2;

			if (type == 1) {
				if (maturity < 3) {
					return BOG_EARTH;
				}
				return PEAT;
			}

			return HUMUS;
		}
	}

	public Collection<Item> getAllCrates() {
		try {
			return Collections.unmodifiableCollection((Collection<Item>)crateList.get(null));
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return new ArrayList();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			return new ArrayList();
		}
	}

	public boolean isLog(Block b) {
		return b == BlockEntry.LOG.getBlock() || b == BlockEntry.FIRELOG.getBlock() || (b != null && b == extraTreeLog);
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return 0;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id == BlockEntry.LEAF.getBlock();
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te != null && ReikaBeeHelper.hasFruit(te);
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null)
			ReikaBeeHelper.setTreeRipeness(te, -1);
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te != null ? ReikaBeeHelper.getTreeRipeness(te) : 0;
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return false;
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList();
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null) {
			ITree tree = ReikaBeeHelper.getTree(te);
			if (tree != null) {
				ItemStack[] fruit = tree.produceStacks(world, x, y, z, ReikaBeeHelper.getTreeRipeness(te));
				for (ItemStack is : fruit)
					ret.add(is);
			}
		}
		return ret;
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

	@Override
	public void editTileDataForHarvest(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null)
			ReikaBeeHelper.setTreeRipeness(te, 0);
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return true;
	}

	@Override
	public boolean isTileEntity() {
		return true;
	}

}
