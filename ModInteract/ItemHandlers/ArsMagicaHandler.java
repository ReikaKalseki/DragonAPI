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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModRegistry.ModOreList;

import am2.api.spell.component.interfaces.ISpellComponent;
import am2.api.spell.component.interfaces.ISpellModifier;
import am2.api.spell.component.interfaces.ISpellShape;

public final class ArsMagicaHandler extends ModHandlerBase {

	public final Block oreID;
	public final Item itemID;

	public final int metaVinteum = 0;
	public final int metaChimerite = 1;
	public final int metaTopaz = 2;
	public final int metaMoonstone = 3;
	public final int metaSunstone = 4;

	public final int metaChimeriteItem = 4;
	public final int metaVinteumItem = 0;
	public final int metaTopazItem = 5;
	public final int metaMoonstoneItem = 7;
	public final int metaSunstoneItem = 6;

	private final ItemStack oreChimerite;
	private final ItemStack oreVinteum;
	private final ItemStack oreTopaz;
	private final ItemStack oreMoonstone;
	private final ItemStack oreSunstone;

	private final ItemStack itemChimerite;
	private final ItemStack itemVinteum;
	private final ItemStack itemTopaz;
	private final ItemStack itemMoonstone;
	private final ItemStack itemSunstone;

	private final ArrayList ores = new ArrayList<ItemStack>();
	private final ArrayList items = new ArrayList<ItemStack>();

	private final ArrayList<Class<? extends ISpellComponent>> spellComponents = new ArrayList();
	private final ArrayList<Class<? extends ISpellShape>> spellShapes = new ArrayList();
	private final ArrayList<Class<? extends ISpellModifier>> spellModifiers = new ArrayList();

	private Object spellUtilInstance;
	private Method getSpellComponents;

	private boolean isOreDict = false;

	private static final ArsMagicaHandler instance = new ArsMagicaHandler();

	private ArsMagicaHandler() {
		super();
		Block idore = null;
		Item iditem = null;

		if (this.hasMod()) {
			try {
				Class blocks = ModList.ARSMAGICA.getBlockClass();
				Class items = ModList.ARSMAGICA.getItemClass();
				Field ore = blocks.getField("AMOres");
				Field item = items.getField("itemOre");

				Block oreb = (Block)ore.get(null);
				Item itemi = (Item)item.get(null);

				idore = oreb;
				iditem = itemi;

				for (Class c : ReikaJavaLibrary.getAllClassesFromPackage("am2.spell.components", null, false, true)) {
					if (ISpellComponent.class.isAssignableFrom(c)) {
						spellComponents.add(c);
					}
					if (ISpellShape.class.isAssignableFrom(c)) {
						spellShapes.add(c);
					}
					if (ISpellModifier.class.isAssignableFrom(c)) {
						spellModifiers.add(c);
					}
				}

				Class util = Class.forName("am2.spell.SpellUtils");
				getSpellComponents = util.getDeclaredMethod("getComponentsForStage", ItemStack.class, int.class);
				getSpellComponents.setAccessible(true);
				Field f = util.getDeclaredField("instance");
				f.setAccessible(true);
				spellUtilInstance = f.get(null);
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalArgumentException e) {
				DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalAccessException e) {
				DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IOException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Classes not loadable!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Class not found!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NoSuchMethodException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Method not found!");
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}

		oreID = idore;
		itemID = iditem;

		oreChimerite = new ItemStack(oreID, 1, metaChimerite);
		oreTopaz = new ItemStack(oreID, 1, metaTopaz);
		oreSunstone = new ItemStack(oreID, 1, metaSunstone);
		oreVinteum = new ItemStack(oreID, 1, metaVinteum);
		oreMoonstone = new ItemStack(oreID, 1, metaMoonstone);

		ores.add(oreChimerite);
		ores.add(oreTopaz);
		ores.add(oreSunstone);
		ores.add(oreVinteum);
		ores.add(oreMoonstone);

		itemTopaz = new ItemStack(itemID, 1, metaTopazItem);
		itemSunstone = new ItemStack(itemID, 1, metaSunstoneItem);
		itemVinteum = new ItemStack(itemID, 1, metaVinteumItem);
		itemMoonstone = new ItemStack(itemID, 1, metaMoonstoneItem);
		itemChimerite = new ItemStack(itemID, 1, metaChimeriteItem);

		items.add(itemTopaz);
		items.add(itemSunstone);
		items.add(itemVinteum);
		items.add(itemMoonstone);
		items.add(itemChimerite);
	}

	public static ArsMagicaHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return oreID != null && itemID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.ARSMAGICA;
	}

	public ItemStack getOre(int meta) {
		if (!this.initializedProperly())
			return null;
		switch(meta) {
			case metaVinteum:
				return oreVinteum.copy();
			case metaChimerite:
				return oreChimerite.copy();
			case metaTopaz:
				return oreTopaz.copy();
			case metaMoonstone:
				return oreMoonstone.copy();
			case metaSunstone:
				return oreSunstone.copy();
		}
		return null;
	}

	public ItemStack getItem(int meta) {
		if (!this.initializedProperly())
			return null;
		switch(meta) {
			case metaTopazItem:
				return itemTopaz.copy();
			case metaMoonstoneItem:
				return itemMoonstone.copy();
			case metaSunstoneItem:
				return itemSunstone.copy();
			case metaChimeriteItem:
				return itemChimerite.copy();
			case metaVinteumItem:
				return itemVinteum.copy();
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public ItemStack getOre(ModOreList ore) {
		if (!this.initializedProperly())
			return null;
		switch(ore) {
			case VINTEUM:
				return oreVinteum.copy();
			case CHIMERITE:
				return oreChimerite.copy();
			case BLUETOPAZ:
				return oreTopaz.copy();
			case MOONSTONE:
				return oreMoonstone.copy();
			case SUNSTONE:
				return oreSunstone.copy();
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public ItemStack getItem(ModOreList ore) {
		if (!this.initializedProperly())
			return null;
		switch(ore) {
			case BLUETOPAZ:
				return itemTopaz.copy();
			case CHIMERITE:
				return itemChimerite.copy();
			case MOONSTONE:
				return itemMoonstone.copy();
			case SUNSTONE:
				return itemSunstone.copy();
			case VINTEUM:
				return itemVinteum.copy();
		}
		return null;
	}

	public boolean isArsMagicaOre(ItemStack is) {
		if (!this.initializedProperly())
			return false;
		if (is == null)
			return false;
		//return ReikaItemHelper.listContainsItemStack(ores, is);
		return Block.getBlockFromItem(is.getItem()) == oreID;
	}

	public boolean isItem(ItemStack is) {
		if (!this.initializedProperly())
			return false;
		//return ReikaItemHelper.listContainsItemStack(items, is) && is.getItem() == itemID;
		return is.getItem() == itemID;
	}

	public void forceOreRegistration() {
		if (!isOreDict) {
			DragonAPICore.log("Ars Magica ores are being registered to Ore Dictionary!");
			for (int i = 0; i < ModOreList.oreList.length; i++) {
				ModOreList o = ModOreList.oreList[i];
				if (o.isArsMagica()) {
					OreDictionary.registerOre(o.getOreDictNames()[0], this.getOre(o));
					OreDictionary.registerOre(o.getProductOreDictName(), this.getItem(o));
					o.initialize();
					DragonAPICore.log("Registering "+o.displayName);
				}
			}
		}
		else {
			DragonAPICore.log("Ars Magica ores already registered to ore dictionary! No action taken!");
			ReikaJavaLibrary.dumpStack();
		}
	}

	@ModDependent(ModList.ARSMAGICA)
	public ISpellComponent[] getSpellComponents(ItemStack is) {
		try {
			return (ISpellComponent[])getSpellComponents.invoke(spellUtilInstance, is, 0);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
