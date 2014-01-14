/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.BerryBushHandler;
import Reika.DragonAPI.ModInteract.MagicCropHandler;
import Reika.DragonAPI.ModInteract.OreBerryBushHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList.VarType;

public enum ModCropList {

	BARLEY(ModList.NATURA, 0xCDB14D, "crops", "seeds", 0, 0, 0, 3, VarType.INSTANCE),
	COTTON(ModList.NATURA, 0xE366F5, "crops", "seeds", 0, 4, 6, 8, VarType.INSTANCE),
	FLAX(ModList.REDPOWER, 0xD9C482, "", "", 0, 0, 0, 0, VarType.INSTANCE),
	CANOLA(ModList.ROTARYCRAFT, 0x5B5B5B, "canola", "ItemCanolaSeed", 0, 0, 0, 9, VarType.INSTANCE, VarType.CLASS),
	MAGIC(ModList.MAGICCROPS, 0x6F9165, MagicCropHandler.getInstance()),
	MANA(ModList.THAUMCRAFT, 0x55aaff, "blockManaPod", "itemManaBean", 0, 0, 0, 3, VarType.INSTANCE),
	BERRY(ModList.NATURA, 0x55ff33, BerryBushHandler.getInstance()),
	OREBERRY(ModList.TINKERER, 0xcccccc, OreBerryBushHandler.getInstance());

	private final ModList mod;
	public final int blockID;
	public final int seedID;
	public final int seedMeta;
	public final int ripeMeta;
	/** Not necessarily zero; see cotton */
	public final int harvestedMeta;
	private int minmeta;
	private final CropHandlerBase handler;
	private String blockClass;
	private String itemClass;

	public final int cropColor;

	private boolean exists = false;

	public static final ModCropList[] cropList = values();

	private ModCropList(ModList api, int color, CropHandlerBase h) {
		handler = h;
		mod = api;
		blockID = -1;
		seedID = -1;
		seedMeta = -1;
		ripeMeta = h.getRipeMeta();
		harvestedMeta = h.getFreshMeta();
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
		int id = -1;
		int seed = -1;
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
							id = is.itemID;
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
							id = block.blockID;
							exists = true;
						}
						break;
					case INT:
						b = blocks.getField(blockVar);
						id = b.getInt(null);
						exists = true;
						break;
					case CLASS:
						blockClass = blockVar;
						break;
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
							seed = is2.itemID;
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
							seed = item.itemID;
							exists = true;
						}
						break;
					case INT:
						i = items.getField(itemVar);
						seed = i.getInt(null);
						exists = true;
						break;
					case CLASS:
						itemClass = itemVar;
						break;
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
		return this.name()+" from "+mod+" with metadatas ["+harvestedMeta+","+ripeMeta+"]";
	}

	public boolean simulateFullBreak() {
		return this == MAGIC;
	}

	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ArrayList<ItemStack> li = new ArrayList();
		if (blockID != -1)
			li.addAll(Block.blocksList[blockID].getBlockDropped(world, x, y, z, meta, fortune));
		else {
			if (id == -1)
				return new ArrayList();
			Block b = Block.blocksList[id];
			if (b != null)
				li.addAll(b.getBlockDropped(world, x, y, z, meta, fortune));
		}
		if (this.isHandlered())
			li.addAll(handler.getAdditionalDrops(world, x, y, z, id, meta, fortune));
		return li;
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
			return (seedID == is.itemID && seedMeta == is.getItemDamage());
		}
	}

	public ModList getParentMod() {
		return mod;
	}

	public static ModCropList getModCrop(int id, int meta) {
		for (int i = 0; i < cropList.length; i++) {
			ModCropList crop = cropList[i];
			if (crop.isHandlered()) {
				if (crop.handler.isCrop(id))
					return crop;
			}
			else if (crop.blockClass != null && !crop.blockClass.isEmpty()) {
				if (crop.blockClass.equals(Block.blocksList[id].getClass().getSimpleName()))
					return crop;
			}
			else {
				if (crop.blockID == id && ReikaMathLibrary.isValueInsideBoundsIncl(crop.minmeta, crop.ripeMeta, meta))
					return crop;
			}
		}
		return null;
	}

	public static boolean isModCrop(int id, int meta) {
		return getModCrop(id, meta) != null;
	}

	public boolean destroyOnHarvest() {
		return this != COTTON && !this.isBerryBush();
	}

	public boolean isBerryBush() {
		return this == BERRY || this == OREBERRY;
	}

	public boolean isRipe(int meta) {
		return meta >= ripeMeta;
	}

	public boolean isHandlered() {
		return handler != null;
	}

	public boolean exists() {
		return exists;
	}
}
