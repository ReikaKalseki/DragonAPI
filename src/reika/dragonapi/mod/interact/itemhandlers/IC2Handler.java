/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.itemhandlers;

import java.lang.reflect.Field;

import net.minecraft.item.ItemStack;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.base.ModHandlerBase;
import reika.dragonapi.libraries.registry.ReikaItemHelper;

public class IC2Handler extends ModHandlerBase {

	private static final IC2Handler instance = new IC2Handler();
	private boolean init = false;

	private IC2Handler() {
		super();
		if (this.hasMod()) {
			Class ic2 = this.getMod().getItemClass();

			boolean exc = false;

			for (int i = 0; i < IC2Stacks.list.length; i++) {
				IC2Stacks s = IC2Stacks.list[i];
				try {
					Field f = ic2.getField(s.tag);
					s.stack = (ItemStack)f.get(null);
				}
				catch (NoSuchFieldException e) {
					DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
					exc = true;
				}
				catch (SecurityException e) {
					DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
					e.printStackTrace();
					this.logFailure(e);
					exc = true;
				}
				catch (IllegalArgumentException e) {
					DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
					exc = true;
				}
				catch (IllegalAccessException e) {
					DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
					e.printStackTrace();
					this.logFailure(e);
					exc = true;
				}
				catch (NullPointerException e) {
					DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
					e.printStackTrace();
					this.logFailure(e);
					exc = true;
				}
			}
			init = !exc;
		}
		else {
			this.noMod();
		}
	}

	public enum IC2Stacks {

		PURECRUSHEDU("purifiedCrushedUraniumOre"),
		ENERGIUM("energiumDust"),
		ADVANCEDALLOY("advancedAlloy"),
		LAPOTRON("lapotronCrystal"),
		CARBONFIBER("carbonPlate"),
		IRIDPLATE("iridiumPlate"),
		RUBBER("rubber"),
		RESIN("resin"),
		SCRAP("scrap"),
		SCRAPBOX("scrapBox"),
		IRIDIUM("iridiumOre"),
		BRONZEPICK("bronzePickaxe"),
		BRONZEAXE("bronzeAxe"),
		BRONZESWORD("bronzeSword"),
		BRONZESHOVEL("bronzeShovel"),
		BRONZEHOE("bronzeHoe"),
		BRONZEHELMET("bronzeHelmet"),
		BRONZECHESTPLATE("bronzeChestplate"),
		BRONZELEGGINGS("bronzeLeggings"),
		BRONZEBOOTS("bronzeBoots"),
		BIOCHAFF("biochaff"),
		//RSH(),
		//LZH(),
		;

		private final String tag;
		private ItemStack stack;

		private static final IC2Stacks[] list = values();

		private IC2Stacks(String s) {
			tag = s;
		}

		public ItemStack getItem() {
			return stack != null ? stack.copy() : null;
		}

		public boolean match(ItemStack is) {
			return ReikaItemHelper.matchStacks(is, stack);
		}

	}

	public static IC2Handler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.IC2;
	}

}
