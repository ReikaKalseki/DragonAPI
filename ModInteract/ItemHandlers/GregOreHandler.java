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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

public final class GregOreHandler extends ModHandlerBase {

	private static final GregOreHandler instance = new GregOreHandler();

	private boolean allOres = true;
	private final HashSet<KeyedItemStack> oreSet = new HashSet();

	private GregOreHandler() {
		if (this.hasMod()) {
			for (CounterpartOres ore : CounterpartOres.oreList) {
				allOres &= ore.init();
				for (ItemStack is : ore.ores) {
					oreSet.add(new KeyedItemStack(is).setSimpleHash(true));
				}
			}
		}
		else {
			this.noMod();
		}
	}

	public static GregOreHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return allOres;
	}

	@Override
	public ModList getMod() {
		return ModList.GREGTECH;
	}

	public boolean isGregOre(Block b) {
		return Block.blockRegistry.getNameForObject(b).startsWith("gt.blockores");
	}

	public boolean isHandledGregOre(ItemStack is) {
		return oreSet.contains(new KeyedItemStack(is).setSimpleHash(true));
	}

	public static enum CounterpartOres {

		CASSITERITE(ModOreList.TIN, 2),
		PYRITE(ReikaOreHelper.IRON, 1/3D),
		LIMONITE(ReikaOreHelper.IRON, 1/4D, "YellowLimonite", "BrownLimonite"),
		HEMATITE(ReikaOreHelper.IRON, 2/5D),
		MAGNETITE(ReikaOreHelper.IRON, 3/7D),
		MINERALSAND(ReikaOreHelper.IRON, 3/14D, "BasalticMineralSand", "GraniticMineralSand"),
		GARNIERITE(ModOreList.NICKEL, 1),
		PENTLANDITE(ModOreList.NICKEL, 9/17D),
		CHALCOPYRITE(ModOreList.COPPER, 1/4D),
		KESTERITE(ModOreList.COPPER, 1/4D),
		STANNITE(ModOreList.COPPER, 1/4D),
		MALACHITE(ModOreList.COPPER, 1/5D),
		TETRAHEDRITE(ModOreList.COPPER, 3/8D),
		SCHEELITE(ModOreList.TUNGSTEN, 1/6D),
		WOLFRAMITE(ModOreList.TUNGSTEN, 1/6D),
		FERBERITE(ModOreList.TUNGSTEN, 1/6D),
		HUEBNERITE(ModOreList.TUNGSTEN, 1/6D),
		STOLZITE(ModOreList.TUNGSTEN, 1/6D),
		TUNGSTATE(ModOreList.TUNGSTEN, 1/7D),
		RUSSELITE(ModOreList.TUNGSTEN, 1/9D),
		PINALITE(ModOreList.TUNGSTEN, 1/11D),
		URANINITE(ModOreList.URANIUM, 1),
		PITCHBLENDE(ModOreList.URANIUM, 3/5D),
		BROMARGYRITE(ModOreList.SILVER, 1/2D),
		COOPERITE(ModOreList.PLATINUM, 1/2D),
		;

		public final OreType counterpart;
		public final double yieldFraction;

		private final HashSet<String> internalNames;
		private final Collection<ItemStack> ores = new ArrayList();

		public static final CounterpartOres[] oreList = values();

		private CounterpartOres(OreType ore, double f, String... n) {
			counterpart = ore;
			yieldFraction = f;

			internalNames = ReikaJavaLibrary.makeSetFromArray(n);
			if (internalNames.isEmpty()) {
				internalNames.add(ReikaStringParser.capFirstChar(this.name()));
			}
		}

		public boolean init() {
			ores.clear();
			for (String s : internalNames) {
				for (ItemStack is : OreDictionary.getOres(s)) {
					if (ModList.GREGTECH.modLabel.equals(ReikaItemHelper.getRegistrantMod(is))) {
						ores.add(is);
					}
				}
			}
			return !ores.isEmpty();
		}

		public Collection<ItemStack> getAllOreBlocks() {
			return Collections.unmodifiableCollection(ores);
		}

		@Override
		public String toString() {
			return this.name()+" (~"+counterpart.toString()+" x "+yieldFraction+") < "+internalNames;
		}
	}

}
