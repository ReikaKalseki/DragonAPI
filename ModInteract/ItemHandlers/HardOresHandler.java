package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

import cpw.mods.fml.common.Loader;

public class HardOresHandler {

	public static final HardOresHandler instance = new HardOresHandler();

	private static final String MODID = "harderores";

	public static final int BLOCK_YIELD = 16;

	private HardOresHandler() {

	}

	public String getHardOre(ItemStack is) {
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

	public OreType getBaseOreType(ItemStack is) {
		String base = this.getHardOre(is);
		OreType ret = null;
		if (base != null) {
			ret = ReikaOreHelper.getEntryFromOreName(base);
			if (ret == null)
				ret = ModOreList.getByOreName(base);
		}
		return ret;
	}

	public boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	public ArrayList<ItemStack> getAllHardOres() {

	}

}
