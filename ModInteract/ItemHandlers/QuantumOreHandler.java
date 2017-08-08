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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

public class QuantumOreHandler extends ModHandlerBase {

	private static final QuantumOreHandler instance = new QuantumOreHandler();

	public final Block quantumID;
	public final Block quantumIDGlow;

	public final Item dustID;

	private boolean isOreDict = false;

	private QuantumOreHandler() {
		super();
		Block idore = null;
		Block idore2 = null;
		Item iddust = null;

		if (this.hasMod()) {
			try {
				Class quant = ModList.QCRAFT.getBlockClass();

				Field quantum = quant.getField("quantumOreBlockID");
				idore = (Block)quantum.get(null);
				quantum = quant.getField("quantumOreGlowingBlockID");
				idore2 = (Block)quantum.get(null);

				Field dust = quant.getField("quantumDustItemID");
				iddust = (Item)dust.get(null);
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

		quantumID = idore;
		quantumIDGlow = idore2;

		dustID = iddust;
	}

	public static QuantumOreHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return quantumID != null && quantumIDGlow != null && dustID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.QCRAFT;
	}

	public boolean isQuantumOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return ReikaItemHelper.matchStackWithBlock(block, quantumID) || ReikaItemHelper.matchStackWithBlock(block, quantumIDGlow);
	}

	public void forceOreRegistration() {
		if (!isOreDict) {
			DragonAPICore.log("QCraft ores are being registered to Ore Dictionary!");
			ModOreList ore = ModOreList.QUANTUM;
			String tag = ore.getOreDictNames()[0];
			OreDictionary.registerOre(tag, new ItemStack(quantumID, 1, 0));
			OreDictionary.registerOre(tag, new ItemStack(quantumIDGlow, 1, 0));
			ore.initialize();
			isOreDict = true;
		}
		else {
			DragonAPICore.log("QCraft ores already registered to ore dictionary! No action taken!");
			ReikaJavaLibrary.dumpStack();
		}
	}

}
