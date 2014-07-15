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
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class RailcraftHandler extends ModHandlerBase {

	private static final RailcraftHandler instance = new RailcraftHandler();

	public final int hiddenID;

	private RailcraftHandler() {
		super();
		int idhidden = -1;
		if (this.hasMod()) {
			try {
				Class c = Class.forName("mods.railcraft.common.blocks.hidden.BlockHidden");
				Field block = c.getDeclaredField("block");
				block.setAccessible(true);
				Block b = (Block)block.get(null);
				idhidden = b != null ? b.blockID : -1; //may be disabled
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
		hiddenID = idhidden;
	}

	public static RailcraftHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return hiddenID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.RAILCRAFT;
	}

}
