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
import net.minecraft.item.Item;
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

	public final Item folderID;

	private MystCraftHandler() {
		super();
		Block iddecay = null;
		Block idportal = null;
		Block idcrystal = null;

		Item idfolder = null;

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
			iddecay = Block.getBlockFromName(ModList.MYSTCRAFT.modLabel+":"+MystObjects.Blocks.decay);
			idportal = Block.getBlockFromName(ModList.MYSTCRAFT.modLabel+":"+MystObjects.Blocks.portal);
			idcrystal = Block.getBlockFromName(ModList.MYSTCRAFT.modLabel+":"+MystObjects.Blocks.crystal);

			idfolder = (Item)Item.itemRegistry.getObject(ModList.MYSTCRAFT.modLabel+":"+MystObjects.Items.folder);

			ReikaJavaLibrary.initClass(ReikaMystcraftHelper.class);
		}
		else {
			this.noMod();
		}

		decayID = iddecay;
		portalID = idportal;
		crystalID = idcrystal;
		folderID = idfolder;
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
