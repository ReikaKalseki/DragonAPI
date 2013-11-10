/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class MekToolHandler extends ModHandlerBase {

	private static final String[] paxelVars = {
		"WoodPaxel", "StonePaxel", "IronPaxel", "DiamondPaxel", "GoldPaxel", "GlowstonePaxel", "BronzePaxel", "OsmiumPaxel",
		"ObsidianPaxel", "LazuliPaxel", "SteelPaxel"
	};

	private static final String[] pickVars = {
		"GlowstonePickaxe", "BronzePickaxe", "OsmiumPickaxe", "ObsidianPickaxe", "LazuliPickaxe", "SteelPickaxe"
	};

	private static final MekToolHandler instance = new MekToolHandler();

	private final ArrayList<Integer> paxelIDs = new ArrayList();
	private final ArrayList<Integer> pickIDs = new ArrayList();

	private MekToolHandler() {
		super();
		if (this.hasMod()) {
			try {
				Class item = this.getMod().getItemClass();
				for (int i = 0; i < paxelVars.length; i++) {
					String varname = paxelVars[i];
					Field paxel = item.getField(varname);
					int idpaxel = ((Item)paxel.get(null)).itemID;
					paxelIDs.add(idpaxel);
				}
				for (int i = 0; i < pickVars.length; i++) {
					String varname = pickVars[i];
					Field pick = item.getField(varname);
					int idpick = ((Item)pick.get(null)).itemID;
					pickIDs.add(idpick);
				}
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
	}

	public static MekToolHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return !paxelIDs.isEmpty() && !pickIDs.isEmpty();
	}

	@Override
	public ModList getMod() {
		return ModList.MEKTOOLS;
	}

	public boolean isPickTypeTool(ItemStack held) {
		if (!this.initializedProperly())
			return false;
		return paxelIDs.contains(held.itemID) || pickIDs.contains(held.itemID);
	}

	public boolean isWood(ItemStack held) {
		return paxelIDs.get(0) == held.itemID;
	}

}
