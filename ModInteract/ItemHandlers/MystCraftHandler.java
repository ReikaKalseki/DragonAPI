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
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Exception.ModReflectionException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;

import com.xcompwiz.mystcraft.api.MystObjects;

import cpw.mods.fml.common.registry.GameRegistry;

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
			/*
			try {
				iddecay = this.getBlockInstance("decay");
				idportal = this.getBlockInstance("portal");
				idcrystal = this.getBlockInstance("crystal");
			}
			catch (ModReflectionException e) {
				DragonAPICore.logError("Reflective exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
			 */
			iddecay = Block.getBlockFromName(MystObjects.Blocks.decay);
			idportal = Block.getBlockFromName(MystObjects.Blocks.portal);
			idcrystal = Block.getBlockFromName(MystObjects.Blocks.crystal);
			ReikaJavaLibrary.initClass(ReikaMystcraftHelper.class);
		}
		else {
			this.noMod();
		}

		decayID = iddecay;
		portalID = idportal;
		crystalID = idcrystal;
	}

	private Block getBlockInstance(String name) throws ModReflectionException {
		Class c = this.getMod().getBlockClass();
		String s = this.getField(c, "block_", name);
		if (s == null)
			s = this.getField(c, "", name);
		if (s == null)
			throw new ModReflectionException(ModList.MYSTCRAFT, "Could not find a field for "+name);
		return GameRegistry.findBlock(this.getMod().modLabel, s);
	}

	private String getField(Class c, String pre, String name) {
		try {
			Field f = c.getDeclaredField(pre+name);
			String reg = (String)f.get(null);
			return reg;
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
