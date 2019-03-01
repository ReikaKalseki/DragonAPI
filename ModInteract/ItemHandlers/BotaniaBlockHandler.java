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
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class BotaniaBlockHandler extends ModHandlerBase {

	private static final BotaniaBlockHandler instance = new BotaniaBlockHandler();

	public final Block flowerID;
	public final Block livingRockID;
	public final Block livingWoodID;

	private BotaniaBlockHandler() {
		super();
		Block idflower = null;
		Block idlivingrock = null;
		Block idlivingwood = null;

		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field f = blocks.getField("flower");
				idflower = ((Block)f.get(null));

				f = blocks.getField("livingrock");
				idlivingrock = ((Block)f.get(null));

				f = blocks.getField("livingwood");
				idlivingwood = ((Block)f.get(null));
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

		livingRockID = idlivingrock;
		livingWoodID = idlivingwood;
		flowerID = idflower;
	}

	public static BotaniaBlockHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return livingRockID != null && livingWoodID != null && flowerID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BOTANIA;
	}

	public boolean isMysticalFlower(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return ReikaItemHelper.matchStackWithBlock(block, flowerID);
	}

}
