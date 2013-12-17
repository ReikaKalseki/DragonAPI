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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.MagicCropHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList.VarType;

public enum ModCropList {

	BARLEY(ModList.NATURA, "crops", 0, 0, 3, VarType.BLOCK),
	COTTON(ModList.NATURA, "crops", 4, 6, 8, VarType.BLOCK),
	FLAX(ModList.REDPOWER, "", 0, 0, 0, VarType.BLOCK),
	CANOLA(ModList.ROTARYCRAFT, "canola", 0, 0, 9, VarType.BLOCK),
	MAGIC(ModList.MAGICCROPS, MagicCropHandler.getInstance());

	private final ModList mod;
	public final int blockID;
	public final int ripeMeta;
	/** Not necessarily zero; see cotton */
	public final int harvestedMeta;
	private int minmeta;
	private final CropHandlerBase handler;

	public static final ModCropList[] cropList = values();

	private ModCropList(ModList api, CropHandlerBase h) {
		handler = h;
		mod = api;
		blockID = -1;
		ripeMeta = h.getRipeMeta();
		harvestedMeta = h.getFreshMeta();
	}

	private ModCropList(ModList api, String blockVar, int metamin, int metafresh, int metaripe, VarType type) {
		if (!DragonAPIInit.canLoadHandlers())
			throw new MisuseException("Accessed registry enum too early! Wait until postInit!");
		mod = api;
		harvestedMeta = metafresh;
		ripeMeta = metaripe;
		minmeta = metamin;
		handler = null;
		int id = -1;
		if (mod.isLoaded()) {
			Class cl = api.getBlockClass();
			if (cl == null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty block class");
			}
			if (blockVar == null || blockVar.isEmpty()) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty variable name");
			}
			else {
				try {
					Field b = cl.getField(blockVar);
					switch(type) {
					case ITEMSTACK:
						ItemStack is = (ItemStack)b.get(null);
						if (is == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Block not instantiated!");
						}
						else
							id = is.itemID;
						break;
					case BLOCK:
						Block block = (Block)b.get(null);
						if (block == null) {
							ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Block not instantiated!");
						}
						else
							id = block.blockID;
						break;
					case INT:
						id = b.getInt(null);
						break;
					default:
						ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type for "+b);
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
	}

	@Override
	public String toString() {
		return this.name()+" from "+mod+" with metadatas ["+harvestedMeta+","+ripeMeta+"]";
	}

	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune) {
		int meta = world.getBlockMetadata(x, y, z);
		return Block.blocksList[blockID].getBlockDropped(world, x, y, z, meta, fortune);
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
		return this != COTTON;
	}

	public boolean isRipe(int meta) {
		return meta >= ripeMeta;
	}

	public boolean isHandlered() {
		return handler != null;
	}
}
