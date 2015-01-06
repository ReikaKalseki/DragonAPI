/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class BerryBushHandler extends CropHandlerBase {

	private static final BerryBushHandler instance = new BerryBushHandler();

	public final Block bushID;
	public final Item berryID;

	public final Block netherBushID;
	public final Item netherBerryID;

	private BerryBushHandler() {
		super();
		Block idbush = null;
		Item idberry = null;
		Block idnetherbush = null;
		Item idnetherberry = null;
		if (this.hasMod()) {
			Class blocks = this.getMod().getBlockClass();
			Class items = this.getMod().getItemClass();
			try {
				Field f = blocks.getField("berryBush");
				Block bush = (Block)f.get(null);
				idbush = bush;

				f = blocks.getField("netherBerryBush");
				Block netherbush = (Block)f.get(null);
				idnetherbush = netherbush;

				f = items.getField("berryItem");
				Item berry = (Item)f.get(null);
				idberry = berry;

				f = items.getField("netherBerryItem");
				Item netherberry = (Item)f.get(null);
				idnetherberry = netherberry;
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
		berryID = idberry;
		bushID = idbush;

		netherBerryID = idnetherberry;
		netherBushID = idnetherbush;
	}

	@Override
	public boolean isCrop(Block id) {
		return id == bushID || id == netherBushID;
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return false;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return this.isCrop(b) && meta >= 12;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int base = meta%4;
		int metato = 12+base;
		world.setBlockMetadataWithNotify(x, y, z, metato, 3);
	}

	public static BerryBushHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return bushID != null && berryID != null && netherBushID != null && netherBerryID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.NATURA;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) - 4;
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		if (id == bushID) {
			li.add(new ItemStack(berryID, 1, meta-12));
		}
		else if (id == netherBushID) {
			li.add(new ItemStack(netherBerryID, 1, meta-12));
		}
		return li;
	}

}
