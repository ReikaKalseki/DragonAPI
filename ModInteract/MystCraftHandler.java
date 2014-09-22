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

public class MystCraftHandler extends ModHandlerBase {

	private static final MystCraftHandler instance = new MystCraftHandler();

	public final Block decayID;
	public final Block portalID;
	public final Block crystalID;

	private MystCraftHandler() {
		super();
		Block iddecay = null;
		Block idportal = null;
		Block idcrystal = null;

		if (this.hasMod()) {
			try {
				iddecay = this.getBlockInstance("Decay");
				idportal = this.getBlockInstance("LinkPortal");
				idcrystal = this.getBlockInstance("Crystal");
			}
			catch (NullPointerException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
			}

			ReikaJavaLibrary.initClass(ReikaMystcraftHelper.class);
		}
		else {
			this.noMod();
		}

		decayID = iddecay;
		portalID = idportal;
		crystalID = idcrystal;
	}

	private Block getBlockInstance(String name) {
		try {
			Field f = Class.forName("com.xcompwiz.mystcraft.block."+name).getDeclaredField("instance");
			f.setAccessible(true);
			return (Block)f.get(null);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static MystCraftHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return decayID != null && portalID != null && crystalID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.MYSTCRAFT;
	}

}
