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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModRegistry.ModOreList;

public class MagicCropHandler extends CropHandlerBase {

	private static final String[] crops = {
		"Coal", "Redstone", "Glowstone", "Obsidian", "Dye", "Iron", "Gold", "Lapis", "Ender", "Nether", "XP", "Blaze", "Diamond",
		"Emerald", "Copper", "Tin", "Silver", "Lead", "Quartz"
	};

	private static final MagicCropHandler instance = new MagicCropHandler();

	private final ArrayList<Integer> blockIDs = new ArrayList();
	public final int oreID;
	public final int netherOreID;
	public final int essenceID;

	private MagicCropHandler() {
		super();
		int idore = -1;
		int idnether = -1;
		int idessence = -1;
		if (this.hasMod()) {
			Class c = this.getMod().getBlockClass();
			for (int i = 0; i < crops.length; i++) {
				String field = "mCrop"+crops[i];
				try {
					Field f = c.getField(field);
					Block crop = (Block)f.get(null);
					blockIDs.add(crop.blockID);
				}
				catch (NoSuchFieldException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
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
			try {
				Field f = c.getField("BlockOreEssence");
				Block ore = (Block)f.get(null);
				idore = ore.blockID;

				f = c.getField("BlockOreEssenceNether");
				ore = (Block)f.get(null);
				idnether = ore.blockID;

				f = c.getField("MagicEssence");
				Item essence = (Item)f.get(null);
				idessence = essence.itemID;
			}
			catch (NoSuchFieldException e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
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
		oreID = idore;
		netherOreID = idnether;
		essenceID = idessence;
	}

	@Override
	public boolean isCrop(int id) {
		return blockIDs.contains(id);
	}

	@Override
	public boolean isRipeCrop(int id, int meta) {
		return this.isCrop(id) && meta == this.getRipeMeta();
	}

	public static MagicCropHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return !blockIDs.isEmpty() && oreID != -1 && netherOreID != -1 && essenceID != -1;
	}

	public boolean isEssenceOre(int id) {
		return id == netherOreID || id == oreID;
	}

	@Override
	public ModList getMod() {
		return ModList.MAGICCROPS;
	}

	@Override
	public int getRipeMeta() {
		return 7;
	}

	@Override
	public int getFreshMeta() {
		return 0;
	}

	public void registerEssence() {
		ItemStack ore = new ItemStack(oreID, 1, 0);
		OreDictionary.registerOre("oreEssence", ore);

		ore = new ItemStack(netherOreID, 1, 0);
		OreDictionary.registerOre("oreNetherEssence", ore);

		ModOreList.ESSENCE.reloadOreList();
		ReikaJavaLibrary.pConsole("DRAGONAPI: Registering Magic Crops Essence ore to the Ore Dictionary!");
	}

}
