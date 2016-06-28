/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.recipehandlers;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import reika.dragonapi.DragonOptions;
import reika.dragonapi.ModList;
import reika.dragonapi.instantiable.data.maps.MultiMap;
import reika.dragonapi.interfaces.registry.OreType;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.registry.ReikaOreHelper;

public final class ModOreCompat {

	public static final ModOreCompat instance = new ModOreCompat();

	private final MultiMap<OreType, String> modOres = new MultiMap();
	private final Collection<OreVariant> variants = new ArrayList();

	private ModOreCompat() {
		if (DragonOptions.GREGORES.getState()) {
			modOres.addValue(ReikaOreHelper.IRON, "oreBandedIron");
			modOres.addValue(ReikaOreHelper.IRON, "oreBrownLimonite");
			modOres.addValue(ReikaOreHelper.IRON, "oreYellowLimonite");
			modOres.addValue(ReikaOreHelper.QUARTZ, "oreNetherQuartz");

			this.addVariant("Netherrack", "ore*", ModList.GREGTECH);
			this.addVariant("Endstone", "ore*", ModList.GREGTECH);
			this.addVariant("Blackgranite", "ore*", ModList.GREGTECH);
			this.addVariant("Redgranite", "ore*", ModList.GREGTECH);
		}
	}

	public Collection<ItemStack> load(OreType type) {
		Collection<ItemStack> c = new ArrayList();
		for (OreVariant v : variants) {
			v.addOreBlocks(type, c);
		}
		for (String s : modOres.get(type)) {
			c.addAll(OreDictionary.getOres(s));
			for (OreVariant v : variants) {
				v.addOreBlocks(s, c);
			}
		}
		return c;
	}

	private void addVariant(String name, String key, ModList... mods) {
		if (ReikaJavaLibrary.isAnyModLoaded(mods))
			variants.add(new OreVariant(name, key));
	}

	private static class OreVariant {

		private final String name;
		private final String regex;

		private OreVariant(String s, String r) {
			name = s;
			regex = r;
		}

		private void addOreBlocks(OreType type, Collection<ItemStack> ores) {
			for (String label : type.getOreDictNames()) {
				this.addOreBlocks(label, ores);
			}
		}

		private void addOreBlocks(String tag, Collection<ItemStack> ores) {
			ores.addAll(OreDictionary.getOres(this.parseTag(tag)));
		}

		private String parseTag(String tag) {
			int idx = regex.indexOf('*');
			String pre = regex.substring(0, idx);
			String suff = tag.substring(pre.length());
			String result = pre+name+suff;
			return result;
		}

	}

}
