package Reika.DragonAPI.ModRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public enum ModCropList {

	BARLEY(ModList.NATURA, "mods.natura.common.NContent", "crops", 0, 0, 3, Block.class),
	COTTON(ModList.NATURA, "mods.natura.common.NContent", "crops", 4, 6, 8, Block.class),
	FLAX(ModList.REDPOWER, "", "", 0, 0, 0, Block.class),
	CANOLA(ModList.ROTARYCRAFT, "Reika.RotaryCraft.RotaryCraft", "canola", 0, 0, 9, Block.class);

	private ModList mod;
	public final int blockID;
	public final int ripeMeta;
	/** Not necessarily zero; see cotton */
	public final int harvestedMeta;
	private int minmeta;

	public static final ModCropList[] cropList = values();

	private ModCropList(ModList api, String className, String blockVar, int metamin, int metafresh, int metaripe, Class type) {
		mod = api;
		harvestedMeta = metafresh;
		ripeMeta = metaripe;
		minmeta = metamin;
		int id = -1;
		if (mod.isLoaded()) {
			if (className == null || className.isEmpty()) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty parent class");
			}
			else if (blockVar == null || blockVar.isEmpty()) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this+": Empty variable name");
			}
			else {
				try {
					Class cl = Class.forName(className);
					Field b = cl.getField(blockVar);
					if (type == ItemStack.class) {
						ItemStack block = (ItemStack)b.get(null);
						id = block.itemID;
					}
					else if (type == Block.class) {
						Block block = (Block)b.get(null);
						id = block.blockID;
					}
					else if (type == Integer.class) {
						id = b.getInt(null);
					}
					else {
						ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading wood "+this);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Invalid variable type for "+b);
					}
				}
				catch (ClassNotFoundException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Error loading crop "+this);
					e.printStackTrace();
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
		return this.name()+" from "+mod;
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
			if (crop.blockID == id && ReikaMathLibrary.isValueInsideBoundsIncl(crop.minmeta, crop.ripeMeta, meta))
				return crop;
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
}
