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

import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;

public final class BloodMagicHandler extends ModHandlerBase {

	private static final BloodMagicHandler instance = new BloodMagicHandler();

	public final Fluid lifeEssence;

	public final Item orbID;
	public final Item demonShardID;
	public final Item resourceID;

	public static final int RED_SHARD_META = 28;
	public static final int BLUE_SHARD_META = 29;

	public final int soulFrayID;

	public final Item boundHelmet;
	public final Item boundChestplate;
	public final Item boundLegs;
	public final Item boundBoots;

	private BloodMagicHandler() {
		super();

		Item idorb = null;
		Item idshard = null;
		Item idres = null;

		Item helm = null;
		Item chest = null;
		Item legs = null;
		Item boots = null;

		Fluid life = null;

		int soulfray = -1;
		if (this.hasMod()) {
			try {
				Class c = this.getMod().getItemClass();
				Field item = c.getDeclaredField("sacrificialDagger");
				item.setAccessible(true);
				idorb = (Item)item.get(null);

				item = c.getDeclaredField("demonBloodShard");
				item.setAccessible(true);
				idshard = (Item)item.get(null);

				item = c.getDeclaredField("baseItems");
				item.setAccessible(true);
				idres = (Item)item.get(null);

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

				f = c.getField("customPotionSoulFrayID");
				soulfray = f.getInt(null);
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
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
		}
		else {
			this.noMod();
		}
		orbID = idorb;
		demonShardID = idshard;
		resourceID = idres;

		boundBoots = boots;
		boundChestplate = chest;
		boundHelmet = helm;
		boundLegs = legs;

		lifeEssence = life;

		soulFrayID = soulfray;
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
