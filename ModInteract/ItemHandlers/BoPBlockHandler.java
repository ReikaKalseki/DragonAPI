/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class BoPBlockHandler extends ModHandlerBase {

	private static final BoPBlockHandler instance = new BoPBlockHandler();

	public final Block coral1;
	public final Block coral2;

	public final Block flower1;
	public final Block flower2;

	public static final String[] flower1Types = {
		"clover",
		"swampflower",
		"deadbloom",
		"glowflower",
		"hydrangea",
		"cosmos",
		"daffodil",
		"wildflower",
		"violet",
		"anemone",
		"lilyflower",
		"enderlotus",
		"bromeliad",
		"eyebulbbottom",
		"eyebulbtop",
		"dandelion"
	};

	public static final String[] flower2Types = {
		"hibiscus",
		"lilyofthevalley",
		"burningblossom",
		"lavender",
		"goldenrod",
		"bluebells",
		"minersdelight",
		"icyiris",
		"rose"
	};

	private BoPBlockHandler() {
		super();
		Block idcoral1 = null;
		Block idcoral2 = null;

		Block idflower1 = null;
		Block idflower2 = null;

		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field c1 = blocks.getField("coral1");
				idcoral1 = ((Block)c1.get(null));

				Field c2 = blocks.getField("coral2");
				idcoral2 = ((Block)c2.get(null));

				Field f1 = blocks.getField("flowers");
				idflower1 = ((Block)f1.get(null));

				Field f2 = blocks.getField("flowers2");
				idflower2 = ((Block)f2.get(null));
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

		coral1 = idcoral1;
		coral2 = idcoral2;

		flower1 = idflower1;
		flower2 = idflower2;
	}

	public static BoPBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return coral1 != null && coral2 != null && flower1 != null && flower2 != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BOP;
	}

	public boolean isCoral(Block id) {
		return id == coral1 || id == coral2;
	}

	public boolean isFlower(Block id) {
		return id == flower1 || id == flower2;
	}

}
