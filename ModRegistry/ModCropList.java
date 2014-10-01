/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.BerryBushHandler;
import Reika.DragonAPI.ModInteract.HarvestCraftHandler;
import Reika.DragonAPI.ModInteract.MagicCropHandler;
import Reika.DragonAPI.ModInteract.OreBerryBushHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList.VarType;

public enum ModCropList {
	//seed meta, min meta, fresh meta, ripe meta
	BARLEY(ModList.NATURA, 0xCDB14D, "crops", "seeds", 0, 0, 0, 3, VarType.INSTANCE),
	COTTON(ModList.NATURA, 0xE366F5, "crops", "seeds", 0, 4, 6, 8, VarType.INSTANCE),
	FLAX(ModList.BLUEPOWER, 0xD9C482, "flax_crop", "flax_seeds", 0, 0, 0, 8, VarType.INSTANCE),
	MAGIC(ModList.MAGICCROPS, 0x6F9165, MagicCropHandler.getInstance()),
	MANA(ModList.THAUMCRAFT, 0x55aaff, "blockManaPod", "itemManaBean", 0, 0, 0, 3, VarType.INSTANCE),
	BERRY(ModList.NATURA, 0x55ff33, BerryBushHandler.getInstance()),
	OREBERRY(ModList.TINKERER, 0xcccccc, OreBerryBushHandler.getInstance()),
	PAM(ModList.HARVESTCRAFT, 0x22aa22, HarvestCraftHandler.getInstance()),
	ALGAE(ModList.EMASHER, 0x29D855, "algae", 0, VarType.INSTANCE),
	ENDER(ModList.EXTRAUTILS, 0x00684A, "enderLily", 7, VarType.INSTANCE);

	private final ModList mod;
	public final Block blockID;
	public final Item seedID;
	public final int seedMeta;
	public final int ripeMeta;
	/** Not necessarily zero; see cotton */
	private final int harvestedMeta;
	private int minmeta;
	private final CropHandlerBase handler;
	private String blockClass;
	private String itemClass;
	private boolean dropsSelf;

	public final int cropColor;

	private boolean exists = false;

	public static final ModCropList[] cropList = values();
	private static final BlockMap<ModCropList> cropMappings = new BlockMap();

	private ModCropList(ModList api, int color, String blockVar, int metaripe, VarType type) {
		this(api, color, blockVar, 0, metaripe, type);
	}

	private ModCropList(ModList api, int color, String blockVar, int metamin, int metaripe, VarType type) {
		dropsSelf = true;
		mod = api;
		ripeMeta = metaripe;
		harvestedMeta = metamin;
		handler = null;
		cropColor = color;

		Block id = null;
		Item seed = null;
		if (mod.isLoaded()) {
			Class blocks = api.getBlockClass();
			Class items = api.getItemClass();
			if (blocks == null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty block class");
			}
			else if (items == null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty item class");
			}
			else if (blockVar == null || blockVar.isEmpty()) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty variable name");
			}
			else {
				try {
					Field b;
					Field i;
					switch(type) {
					case ITEMSTACK:
						b = blocks.getField(blockVar);
						ItemStack is = (ItemStack)b.get(null);
						if (is == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Block not instantiated!");
							exists = false;
						}
						else {
							id = Block.getBlockFromItem(is.getItem());
							exists = true;
						}
						break;
					case INSTANCE:
						b = blocks.getField(blockVar);
						Block block = (Block)b.get(null);
						if (block == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Block not instantiated!");
							exists = false;
						}
						else {
							id = block;
							exists = true;
						}
						break;/*
					case INT:
						b = blocks.getField(blockVar);
						id = b.getInt(null);
						exists = true;
						break;*/
					default:
						ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type for field "+blockVar);
						exists = false;
					}
				}
				catch (NoSuchFieldException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
				catch (SecurityException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
				catch (IllegalArgumentException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
			}
		}
		blockID = id;
		seedID = Item.getItemFromBlock(blockID);
		seedMeta = 0;
	}

	private ModCropList(ModList api, int color, CropHandlerBase h) {
		handler = h;
		mod = api;
		blockID = null;
		seedID = null;
		seedMeta = -1;
		harvestedMeta = -1;
		ripeMeta = -1;
		cropColor = color;
		exists = h.initializedProperly();
	}

	private ModCropList(ModList api, int color, String blockVar, String itemVar, int seedItem, int metamin, int metafresh, int metaripe, VarType type) {
		this(api, color, blockVar, itemVar, seedItem, metamin, metafresh, metaripe, type, type);
	}

	private ModCropList(ModList api, int color, String blockVar, String itemVar, int seedItem, int metamin, int metafresh, int metaripe, VarType blockType, VarType itemType) {
		if (!DragonAPIInit.canLoadHandlers())
			throw new MisuseException("Accessed registry enum too early! Wait until postInit!");
		mod = api;
		harvestedMeta = metafresh;
		ripeMeta = metaripe;
		minmeta = metamin;
		cropColor = color;
		seedMeta = seedItem;
		handler = null;
		Block id = null;
		Item seed = null;
		if (mod.isLoaded()) {
			Class blocks = api.getBlockClass();
			Class items = api.getItemClass();
			if (blocks == null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty block class");
			}
			else if (items == null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty item class");
			}
			else if (blockVar == null || blockVar.isEmpty() || itemVar == null || itemVar.isEmpty()) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty variable name");
			}
			else {
				try {
					Field b;
					Field i;
					switch(blockType) {
					case ITEMSTACK:
						b = blocks.getField(blockVar);
						ItemStack is = (ItemStack)b.get(null);
						if (is == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Block not instantiated!");
							exists = false;
						}
						else {
							id = Block.getBlockFromItem(is.getItem());
							exists = true;
						}
						break;
					case INSTANCE:
						b = blocks.getField(blockVar);
						Block block = (Block)b.get(null);
						if (block == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Block not instantiated!");
							exists = false;
						}
						else {
							id = block;
							exists = true;
						}
						break;/*
					case INT:
						b = blocks.getField(blockVar);
						id = b.getInt(null);
						exists = true;
						break;*/
					default:
						ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type for field "+blockVar);
						exists = false;
					}
					switch(itemType) {
					case ITEMSTACK:
						i = items.getField(itemVar);
						ItemStack is2 = (ItemStack)i.get(null);
						if (is2 == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Seed not instantiated!");
							exists = false;
						}
						else {
							seed = is2.getItem();
							exists = true;
						}
						break;
					case INSTANCE:
						i = items.getField(itemVar);
						Item item = (Item)i.get(null);
						if (item == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Seed not instantiated!");
							exists = false;
						}
						else {
							seed = item;
							exists = true;
						}
						break;/*
					case INT:
						i = items.getField(itemVar);
						seed = i.getInt(null);
						exists = true;
						break;*/
					default:
						ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type for field "+itemVar);
						exists = false;
					}
				}
				catch (NoSuchFieldException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
				catch (SecurityException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
				catch (IllegalArgumentException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
				}
			}
		}
		blockID = id;
		seedID = seed;
	}

	@Override
	public String toString() {
		if (this.isHandlered())
			return this.name()+" from "+mod+" with handler "+handler.getClass().getSimpleName();
		else
			return this.name()+" from "+mod+" with metadatas ["+harvestedMeta+","+ripeMeta+"]";
	}

	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ArrayList<ItemStack> li = new ArrayList();
		if (blockID != null)
			li.addAll(blockID.getDrops(world, x, y, z, meta, fortune));
		else {
			if (b == Blocks.air)
				return new ArrayList();
			if (b != null)
				li.addAll(b.getDrops(world, x, y, z, meta, fortune));
		}
		if (this.isHandlered())
			li.addAll(handler.getAdditionalDrops(world, x, y, z, b, meta, fortune));
		return li;
	}

	public boolean isTileEntity() {
		return false;
	}

	public void runTEHarvestCode(World world, int x, int y, int z) {
		if (!this.isTileEntity())
			return;
		handler.editTileDataForHarvest(world, x, y, z);
	}

	public void removeOneSeed(ArrayList<ItemStack> li) {
		Iterator<ItemStack> it = li.iterator();
		while (it.hasNext()) {
			ItemStack is = it.next();
			if (this.isSeedItem(is)) {
				if (is.stackSize > 1)
					is.stackSize--;
				else
					it.remove();
				return;
			}
		}
	}

	public boolean isSeedItem(ItemStack is) {
		if (this.isHandlered()) {
			return handler.isSeedItem(is);
		}
		else if (itemClass != null && !itemClass.isEmpty()) {
			return itemClass.equals(is.getItem().getClass().getSimpleName());
		}
		else {
			return (seedID == is.getItem() && seedMeta == is.getItemDamage());
		}
	}

	public ModList getParentMod() {
		return mod;
	}

	public static ModCropList getModCrop(Block id, int meta) {
		ModCropList mod = cropMappings.get(id, meta);

		if (mod == null) {
			for (int i = 0; i < cropList.length && mod == null; i++) {
				ModCropList crop = cropList[i];
				if (crop.isHandlered()) {
					if (crop.handler.isCrop(id)) {
						mod = crop;
					}
				}
				else if (crop.blockClass != null && !crop.blockClass.isEmpty()) {
					if (crop.blockClass.equals(id.getClass().getSimpleName())) {
						mod = crop;
					}
				}
				else {
					if (crop.blockID == id && ReikaMathLibrary.isValueInsideBoundsIncl(crop.minmeta, crop.ripeMeta, meta)) {
						mod = crop;
					}
				}
			}
			cropMappings.put(id, meta, mod);
		}

		return mod;
	}

	public static boolean isModCrop(Block id, int meta) {
		return getModCrop(id, meta) != null;
	}

	public boolean destroyOnHarvest() {
		return this == ALGAE;
	}

	public boolean isBerryBush() {
		return this == BERRY || this == OREBERRY;
	}

	public boolean isRipe(World world, int x, int y, int z) {
		return this.isHandlered() ? handler.isRipeCrop(world, x, y, z) : world.getBlockMetadata(x, y, z) >= ripeMeta;
	}

	public void makeRipe(World world, int x, int y, int z) {
		if (this.isHandlered()) {
			handler.makeRipe(world, x, y, z);
		}
		else {
			int metato = ripeMeta;
			world.setBlockMetadataWithNotify(x, y, z, metato, 3);
		}
	}

	public int getHarvestedMetadata(World world, int x, int y, int z) {
		return this.isHandlered() ? handler.getHarvestedMeta(world, x, y, z) : harvestedMeta;
	}

	public boolean isHandlered() {
		return handler != null;
	}

	public boolean exists() {
		return exists;
	}

	static {
		for (int i = 0; i < ModCropList.cropList.length; i++) {
			ModCropList c = ModCropList.cropList[i];
			if (c.exists() && !c.isHandlered()) {
				Block b = c.blockID;
				for (int k = c.minmeta; k <= c.ripeMeta; k++)
					cropMappings.put(b, k, c);
			}
		}
	}
}
