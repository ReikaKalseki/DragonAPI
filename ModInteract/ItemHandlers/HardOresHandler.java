package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

import cpw.mods.fml.common.Loader;

public class HardOresHandler {

	public static final HardOresHandler instance = new HardOresHandler();

	private static final String MODID = "harderores";

	public static final int BLOCK_YIELD = 16;

	private final HashMap<KeyedItemStack, ItemStack> oreMap = new HashMap();

	private HardOresHandler() {

	}

	public boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	private OreType findBaseOreType(ItemStack is) {
		if (!this.isLoaded())
			return null;
		String base = this.findHardOreKey(is);
		OreType ret = null;
		if (base != null) {
			ret = ReikaOreHelper.getEntryFromOreName(base);
			if (ret == null)
				ret = ModOreList.getByOreName(base);
		}
		return ret;
	}

	private String findHardOreKey(ItemStack is) {
		String mod = ReikaItemHelper.getRegistrantMod(is);
		if (mod == null || !mod.equals(MODID))
			return null;
		HashSet<String> ores = ReikaItemHelper.getOreNames(is);
		for (String s : ores) {
			if (s.endsWith("Hard"))
				return s;
		}
		return null;
	}

	public ArrayList<ItemStack> getAllHardOres() {
		ArrayList<ItemStack> ret = new ArrayList();
		for (Entry<KeyedItemStack, ItemStack> ks : oreMap.entrySet()) {
			if (ks.getValue() != null)
				ret.add(ks.getKey().getItemStack());
		}
		return ret;
	}

	public ItemStack getRootOre(ItemStack is) {
		KeyedItemStack ks = this.key(is);
		if (oreMap.containsKey(ks)) {
			return oreMap.get(ks);
		}
		else {
			OreType ore = this.findBaseOreType(is);
			oreMap.put(ks, ore != null ? ore.getFirstOreBlock() : null);
			return null;
		}
	}

	private KeyedItemStack key(ItemStack is) {
		return new KeyedItemStack(is).setIgnoreNBT(true).setIgnoreMetadata(false).setSized(false).setSimpleHash(true).lock();
	}

	public Collection<ItemStack> getOresWithHardVersions() {
		return ReikaItemHelper.cloneItemCollection(oreMap.values());
	}

}
