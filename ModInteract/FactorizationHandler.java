/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class FactorizationHandler extends ModHandlerBase {

	private static final FactorizationHandler instance = new FactorizationHandler();

	public final int bedrockID;
	public final int ingotID;

	private FactorizationHandler() {
		super();
		int idbedrock = -1;
		int idingot = -1;
		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Class core = Class.forName("factorization.shared.Core");
				Field registry = core.getField("registry");
				Object reg = registry.get(null);

				Field bed = blocks.getField("fractured_bedrock_block");
				Block b = (Block)bed.get(reg);
				idbedrock = b.blockID;

				Field ingot = blocks.getField("dark_iron");
				Item i = (Item)ingot.get(reg);
				idingot = i.itemID;
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
		bedrockID = idbedrock;
		ingotID = idingot;
	}

	public static FactorizationHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return bedrockID != -1 && ingotID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.FACTORIZATION;
	}

}
