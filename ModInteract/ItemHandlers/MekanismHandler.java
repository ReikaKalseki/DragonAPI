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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.ModRegistry.ModOreList;

public final class MekanismHandler extends ModHandlerBase {

	private static final MekanismHandler instance = new MekanismHandler();

	public final Block oreID;
	//public final Block cableID;

	public static final int osmiumMeta = 0;
	public static final int copperMeta = 1;
	public static final int tinMeta = 2;

	private MekanismHandler() {
		super();
		Block idore = null;
		//Block idcable = null;
		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field ore = blocks.getField("OreBlock");
				Block b = (Block)ore.get(null);
				idore = b;

				//Field wire = blocks.getField("Transmitter");
				//b = (Block)wire.get(null);
				//idcable = b;
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
		oreID = idore;
		//cableID = idcable;
	}

	public static MekanismHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return oreID != null/* && cableID != null*/;
	}

	@Override
	public ModList getMod() {
		return ModList.MEKANISM;
	}

	public ModOreList getModOre(Item id, int meta) {
		return this.getModOre(Block.getBlockFromItem(id), meta);
	}

	public ModOreList getModOre(Block id, int meta) {
		if (id != oreID)
			return null;

		if (meta == osmiumMeta)
			return ModOreList.OSMIUM;
		if (meta == tinMeta)
			return ModOreList.TIN;
		if (meta == copperMeta)
			return ModOreList.COPPER;

		return null;
	}

}
