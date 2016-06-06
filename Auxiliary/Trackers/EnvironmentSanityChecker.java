/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Exception.EnvironmentSanityException;
import Reika.DragonAPI.Exception.EnvironmentSanityException.ErrorType;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EnvironmentSanityChecker {

	public static final EnvironmentSanityChecker instance = new EnvironmentSanityChecker();

	private EnvironmentSanityChecker() {

	}

	public void check() {
		this.checkBlocks();
		this.checkItems();
		this.checkBiomes();
		this.checkEnchants();
		this.checkPotions();
		this.checkDungeonLoot();
		this.checkOreDict();
	}

	private void checkBlocks() {
		for (String s : ((Set<String>)Block.blockRegistry.getKeys())) {
			Block b = (Block)Block.blockRegistry.getObject(s);
			if (s == null) {
				throw new EnvironmentSanityException(ErrorType.NULLREG, b);
			}
			else if (b == null) {
				throw new EnvironmentSanityException(ErrorType.NULLENTRY, s);
			}
			else {
				this.verifyBlock(b);
			}
		}
	}

	private void checkItems() {
		for (String s : ((Set<String>)Item.itemRegistry.getKeys())) {
			Item b = (Item)Item.itemRegistry.getObject(s);
			if (s == null) {
				throw new EnvironmentSanityException(ErrorType.NULLREG, b);
			}
			else if (b == null) {
				throw new EnvironmentSanityException(ErrorType.NULLENTRY, s);
			}
			else {
				this.verifyItem(b);
			}
		}
	}

	private void checkBiomes() {
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b != null) {
				if (b.biomeID != i) {
					if (i != BiomeGenBase.megaTaigaHills.biomeID + 128 + 1) {
						//What the HELL, Mojang!?
					}
					else {
						throw new EnvironmentSanityException(ErrorType.IDMISMATCH, b, i, b.biomeID);
					}
				}
				else {
					this.verifyBiome(b);
				}
			}
		}
	}

	private void checkEnchants() {
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			Enchantment e = Enchantment.enchantmentsList[i];
			if (e != null) {
				if (e.effectId != i) {
					throw new EnvironmentSanityException(ErrorType.IDMISMATCH, e, i, e.effectId);
				}
				else {
					this.verifyEnchant(e);
				}
			}
		}
	}

	private void checkPotions() {
		for (int i = 0; i < Potion.potionTypes.length; i++) {
			Potion p = Potion.potionTypes[i];
			if (p != null) {
				if (p.id != i) {
					throw new EnvironmentSanityException(ErrorType.IDMISMATCH, p, i, p.id);
				}
				else {
					this.verifyPotion(p);
				}
			}
		}
	}

	private void checkDungeonLoot() {
		try {
			Field f = ChestGenHooks.class.getDeclaredField("chestInfo");
			f.setAccessible(true);
			Field f2 = ChestGenHooks.class.getDeclaredField("contents");
			f2.setAccessible(true);
			Map<String, ChestGenHooks> map = (Map<String, ChestGenHooks>)f.get(null);
			for (String s : map.keySet()) {
				ChestGenHooks c = map.get(s);
				ArrayList<WeightedRandomChestContent> li = (ArrayList<WeightedRandomChestContent>)f2.get(c);
				Iterator<WeightedRandomChestContent> it = li.iterator();
				while (it.hasNext()) {
					WeightedRandomChestContent w = it.next();
					ItemStack is = w.theItemId;
					try {
						if (is == null) {
							throw new EnvironmentSanityException(ErrorType.LOOT, is, s, "Null Stack");
						}
						else if (is.getItem() == null) {
							throw new EnvironmentSanityException(ErrorType.LOOT, is, s, "Null-Item ItemStack");
						}
						else if (is.getItem() == null) {
							throw new EnvironmentSanityException(ErrorType.LOOT, is, s, "Null-Item ItemStack");
						}
						else if (!ReikaItemHelper.verifyItemStack(is, true)) {
							throw new EnvironmentSanityException(ErrorType.LOOT, is, s, "Errors on handling");
						}
					}
					catch (EnvironmentSanityException e) {
						if (DragonOptions.FIXSANITY.getState()) {
							DragonAPICore.logError("Found invalid item "+getSafeItemString(is)+" registered to dungeon loot tag '"+s+"'");
							it.remove();
						}
						else {
							throw e;
						}
					}
				}
			}
		}
		catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private void checkOreDict() {
		String[] tags = OreDictionary.getOreNames();
		for (int i = 0; i < tags.length; i++) {
			String tag = tags[i];
			ArrayList<ItemStack> li = new ArrayList(OreDictionary.getOres(tag));
			for (ItemStack is : li) {
				try {
					this.verifyOreDictItem(tag, is);
				}
				catch (EnvironmentSanityException e) {
					if (DragonOptions.FIXSANITY.getState()) {
						try {
							DragonAPICore.logError("Found invalid item "+getSafeItemString(is)+" registered to OreDict "+tag+": "+e);
							e.printStackTrace();
							ReikaItemHelper.removeOreDictEntry(tag, is);
						}
						catch (Exception ex) { //could not remove
							ex.initCause(e);
							throw new RuntimeException(ex);
						}
					}
					else {
						throw e;
					}
				}
			}
		}
	}

	private void verifyOreDictItem(String tag, ItemStack is) {
		try {
			is.getUnlocalizedName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Name");
		}
		try {
			is.getDisplayName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Display Name");
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.doClientOreDictVerification(tag, is);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doClientOreDictVerification(String tag, ItemStack is2) {
		is2.getItem().registerIcons(ReikaTextureHelper.dummyTextureMap);
		Block b = Block.getBlockFromItem(is2.getItem());
		if (b != null) {
			b.registerBlockIcons(ReikaTextureHelper.dummyTextureMap);
		}
		List<ItemStack> li = new ArrayList();
		is2.getItem().getSubItems(is2.getItem(), is2.getItem().getCreativeTab(), li);
		for (ItemStack is : li) {
			try {
				is.getIconIndex();
			}
			catch (Exception e) {
				throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Icon");
			}
			boolean draw = Tessellator.instance.isDrawing;
			try {
				ReikaGuiAPI.instance.drawItemStack(ReikaGuiAPI.itemRenderer, is, 0, 0);
			}
			catch (Exception e) {
				/*
			try {
				tessellatorReset.invoke(Tessellator.instance);
			}
			catch (Exception ex) {
				RuntimeException exc = new IllegalStateException("Could not reset tessellator when trying to recover parsing invalid OreDict entry");
				ex.initCause(e);
				exc.initCause(ex);
				throw exc;
			}*/
				Tessellator.instance.isDrawing = draw;
				throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Render");
			}
		}
	}

	public void verifyBlock(Block b) throws EnvironmentSanityException {
		if (b.getMaterial() == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.getMaterial(), "Material");
		}
		if (b.stepSound == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.stepSound, "Step Sound");
		}
		try {
			b.getLocalizedName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, b, e, "Localization");
		}
	}

	public void verifyItem(Item i) throws EnvironmentSanityException {
		try {
			i.getUnlocalizedName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, i, e, "Name");
		}
		try {
			i.getMaxDamage();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, i, e, "Max Damage");
		}
		try {
			i.getItemStackLimit(new ItemStack(i));
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, i, e, "Stacksize Limit");
		}
	}

	public void verifyBiome(BiomeGenBase b) throws EnvironmentSanityException {
		if (b.biomeName == null) {
			if (DragonOptions.FIXSANITY.getState())
				b.biomeName = "NULL";
			else
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.biomeName, "Name");
		}
		if (b.topBlock == null) {
			if (DragonOptions.FIXSANITY.getState())
				b.topBlock = Blocks.grass;
			else
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.topBlock, "Top Block");
		}
		if (b.fillerBlock == null) {
			if (DragonOptions.FIXSANITY.getState())
				b.fillerBlock = Blocks.dirt;
			else
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.fillerBlock, "Filler Block");
		}
		if (b.theBiomeDecorator == null) {
			if (DragonOptions.FIXSANITY.getState())
				b.theBiomeDecorator = new BiomeDecorator();
			else
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.theBiomeDecorator, "Decorator");
		}

		for (int i = 0; i < EnumCreatureType.values().length; i++) {
			EnumCreatureType e = EnumCreatureType.values()[i];
			if (b.getSpawnableList(e) == null) {
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.getSpawnableList(e), "Spawnable List");
			}
		}
	}

	public void verifyEnchant(Enchantment e) throws EnvironmentSanityException {
		if (e.type == null) {
			if (DragonOptions.FIXSANITY.getState())
				e.type = EnumEnchantmentType.all;
			else
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, e, e.type, "Type");
		}
		try {
			e.getName();
		}
		catch (Exception ex) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, e, ex, "Name");
		}
		try {
			e.getTranslatedName(1);
		}
		catch (Exception ex) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, e, ex, "Localization");
		}
	}

	public void verifyPotion(Potion p) throws EnvironmentSanityException {
		if (p.getName() == null) {
			if (DragonOptions.FIXSANITY.getState())
				p.setPotionName("NULL");
			else
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, p, p.getName(), "Name");
		}
	}

	public static String getSafeItemString(ItemStack is) {
		if (is == null)
			return "null";
		else if (is.getItem() == null) {
			return "null-item";
		}
		try {
			return is.toString()+" {"+ReikaItemHelper.getRegistrantMod(is)+"}";
		}
		catch (Exception e) {
			return "[THREW "+e.toString()+"]";
		}
	}

}
