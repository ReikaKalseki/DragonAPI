/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;

public final class BloodMagicHandler extends ModHandlerBase {

	private static final BloodMagicHandler instance = new BloodMagicHandler();

	public final Fluid lifeEssence;
	public final Item orbID;

	public final Item boundHelmet;
	public final Item boundChestplate;
	public final Item boundLegs;
	public final Item boundBoots;

	private BloodMagicHandler() {
		super();
		Item idorb = null;

		Item helm = null;
		Item chest = null;
		Item legs = null;
		Item boots = null;

		Fluid life = null;
		if (this.hasMod()) {
			try {
				Class c = this.getMod().getItemClass();
				Field item = c.getDeclaredField("sacrificialDagger");
				item.setAccessible(true);
				idorb = (Item)item.get(null);

				item = c.getDeclaredField("boundHelmet");
				item.setAccessible(true);
				helm = (Item)item.get(null);

				item = c.getDeclaredField("boundPlate");
				item.setAccessible(true);
				chest = (Item)item.get(null);

				item = c.getDeclaredField("boundLeggings");
				item.setAccessible(true);
				legs = (Item)item.get(null);

				item = c.getDeclaredField("boundBoots");
				item.setAccessible(true);
				boots = (Item)item.get(null);

				c = Class.forName("WayofTime.alchemicalWizardry.AlchemicalWizardry");
				Field f = c.getDeclaredField("lifeEssenceFluid");
				f.setAccessible(true);
				life = (Fluid)f.get(null);
			}
			catch (ClassNotFoundException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
			}
			catch (SecurityException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}
		}
		else {
			this.noMod();
		}
		orbID = idorb;

		boundBoots = boots;
		boundChestplate = chest;
		boundHelmet = helm;
		boundLegs = legs;

		lifeEssence = life;
	}

	public static BloodMagicHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return lifeEssence != null && orbID != null && boundBoots != null && boundChestplate != null && boundHelmet != null && boundLegs != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BLOODMAGIC;
	}

	public boolean isBloodOrb(Item item) {
		return item instanceof IBloodOrb;
	}

	public boolean isPlayerWearingFullBoundArmor(EntityPlayer ep) {
		ItemStack[] inv = ep.inventory.armorInventory;
		if (inv[3] == null || inv[3].getItem() != boundHelmet)
			return false;
		if (inv[2] == null || inv[2].getItem() != boundChestplate)
			return false;
		if (inv[1] == null || inv[1].getItem() != boundLegs)
			return false;
		if (inv[0] == null || inv[0].getItem() != boundBoots)
			return false;
		return true;
	}

}
