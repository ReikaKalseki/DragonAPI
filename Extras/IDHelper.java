package Reika.DragonAPI.Extras;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class IDHelper {

	private static final HashMap<String, Integer> blocks;
	private static final HashMap<String, Integer> items;
	private static final HashMap<String, Integer> entities;
	private static final HashMap<String, Integer> potions;
	private static final HashMap<String, Integer> biomes;
	private static final HashMap<String, Integer> fluids;
	private static final HashMap<String, Integer> fluidcontainers;

	static {
		blocks = ReikaJavaLibrary.sortMapByValues(calcBlockIDs());
		items = ReikaJavaLibrary.sortMapByValues(calcItemIDs());
		entities = ReikaJavaLibrary.sortMapByValues(calcEntityIDs());
		potions = ReikaJavaLibrary.sortMapByValues(calcPotionIDs());
		biomes = ReikaJavaLibrary.sortMapByValues(calcBiomeIDs());
		fluids = ReikaJavaLibrary.sortMapByValues(calcFluidIDs());
		fluidcontainers = ReikaJavaLibrary.sortMapByValues(calcFluidContainers());
	}

	private static HashMap<String, Integer> calcBlockIDs() {
		HashMap<String, Integer> map = new HashMap();
		Iterator<Block> it = Block.blockRegistry.iterator();
		while (it.hasNext()) {
			Block o = it.next();
			String name = Block.blockRegistry.getNameForObject(o);
			int id = Block.blockRegistry.getIDForObject(o);
			map.put(name, id);
		}
		return map;
	}

	private static HashMap<String, Integer> calcItemIDs() {
		HashMap<String, Integer> map = new HashMap();
		Iterator<Item> it = Item.itemRegistry.iterator();
		while (it.hasNext()) {
			Item o = it.next();
			String name = Item.itemRegistry.getNameForObject(o);
			int id = Item.itemRegistry.getIDForObject(o);
			map.put(name, id);
		}
		return map;
	}

	private static HashMap<String, Integer> calcBiomeIDs() {
		HashMap<String, Integer> map = new HashMap();
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b != null) {
				map.put(b.biomeName, i);
			}
		}
		return map;
	}

	private static HashMap<String, Integer> calcEntityIDs() {
		HashMap<String, Integer> map = new HashMap();
		map.putAll(EntityList.stringToIDMapping);
		return map;
	}

	private static HashMap<String, Integer> calcPotionIDs() {
		HashMap<String, Integer> map = new HashMap();
		for (int i = 0; i < Potion.potionTypes.length; i++) {
			Potion b = Potion.potionTypes[i];
			if (b != null) {
				map.put(b.getName(), i);
			}
		}
		return map;
	}

	private static HashMap<String, Integer> calcFluidIDs() {
		HashMap<String, Integer> map = new HashMap();
		map.putAll(FluidRegistry.getRegisteredFluidIDs());
		return map;
	}

	private static HashMap<String, Integer> calcFluidContainers() {
		HashMap<String, Integer> map = new HashMap();
		FluidContainerData[] dat = FluidContainerRegistry.getRegisteredFluidContainerData();
		for (int i = 0; i < dat.length; i++) {
			FluidContainerData fcd = dat[i];
			FluidStack fs = fcd.fluid;
			Fluid f = FluidRegistry.getFluid(fs.fluidID);
			ItemStack empty = fcd.emptyContainer;
			ItemStack full = fcd.filledContainer;
			String is1 = empty != null ? String.format("%s (%d:%d)", empty.getDisplayName(), Item.getIdFromItem(empty.getItem()), empty.getItemDamage()) : "null";
			String is2 = full != null ? String.format("%s (%d:%d)", full.getDisplayName(), Item.getIdFromItem(full.getItem()), full.getItemDamage()) : "null";
			String sg = String.format("%d of %s in %s > %s", fs.amount, f.getName(), is1, is2);
			map.put(sg, fs.fluidID);
		}
		return map;
	}

	public static Map<String, Integer> getBlockIDs() {
		return Collections.unmodifiableMap(blocks);
	}

	public static Map<String, Integer> getItemIDs() {
		return Collections.unmodifiableMap(items);
	}

	public static Map<String, Integer> getEntityIDs() {
		return Collections.unmodifiableMap(entities);
	}

	public static Map<String, Integer> getPotionIDs() {
		return Collections.unmodifiableMap(potions);
	}

	public static Map<String, Integer> getBiomeIDs() {
		return Collections.unmodifiableMap(biomes);
	}

	public static Map<String, Integer> getFluidIDs() {
		return Collections.unmodifiableMap(fluids);
	}

	public static Map<String, Integer> getFluidContainers() {
		return Collections.unmodifiableMap(fluidcontainers);
	}
}
