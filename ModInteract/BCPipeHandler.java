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
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public class BCPipeHandler extends ModHandlerBase {

	public static enum Types {
		WOOD(),
		STONE(),
		COBBLESTONE(),
		IRON(),
		GOLD(),
		VOID(),
		SANDSTONE(),
		EMERALD(),
		QUARTZ(),
		LAPIS(),
		DIAMOND(),
		OBSIDIAN();

		public static final Types[] pipeList = values();
	}

	private static final String[] fluidPipes = {
		"pipeFluidsWood",
		"pipeFluidsCobblestone",
		"pipeFluidsStone",
		"pipeFluidsIron",
		"pipeFluidsGold",
		"pipeFluidsVoid",
		"pipeFluidsSandstone",
		"pipeFluidsEmerald",
	};

	private static final String[] itemPipes = {
		"pipeItemsWood",
		"pipeItemsEmerald",
		"pipeItemsStone",
		"pipeItemsCobblestone",
		"pipeItemsIron",
		"pipeItemsQuartz",
		"pipeItemsGold",
		"pipeItemsDiamond",
		"pipeItemsObsidian",
		"pipeItemsLapis",
		"pipeItemsDaizuli",
		"pipeItemsVoid",
		"pipeItemsSandstone",
	};

	private static final String[] powerPipes = {
		"pipePowerWood",
		"pipePowerCobblestone",
		"pipePowerStone",
		"pipePowerQuartz",
		"pipePowerGold",
		"pipePowerDiamond",
	};

	private final HashMap<String, Integer> itemIDs = new HashMap();

	private static final BCPipeHandler instance = new BCPipeHandler();

	/** Pipe Block ID */
	public final int pipeID;

	private BCPipeHandler() {
		super();
		int idpipe = -1;
		if (this.hasMod()) {
			try {
				Class Transport = this.getMod().getBlockClass();
				Field pipe = Transport.getField("genericPipeBlock");
				idpipe = ((Block)pipe.get(null)).blockID;
				for (int i = 0; i < fluidPipes.length; i++) {
					Field f = Transport.getField(fluidPipes[i]);
					int id = ((Item)f.get(null)).itemID;
					itemIDs.put(fluidPipes[i], id);
				}
				for (int i = 0; i < itemPipes.length; i++) {
					Field f = Transport.getField(itemPipes[i]);
					int id = ((Item)f.get(null)).itemID;
					itemIDs.put(itemPipes[i], id);
				}
				for (int i = 0; i < powerPipes.length; i++) {
					Field f = Transport.getField(powerPipes[i]);
					int id = ((Item)f.get(null)).itemID;
					itemIDs.put(powerPipes[i], id);
				}
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

		pipeID = idpipe;
	}

	public static BCPipeHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return pipeID != -1;
	}

	@Override
	public ModList getMod() {
		return ModList.BUILDCRAFTTRANSPORT;
	}

	public Types getPipeType(TileEntity te) {
		if (!this.initializedProperly())
			return null;
		try {
			Class test = Class.forName("buildcraft.transport.TileGenericPipe");
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Pipe reader failed! Class not found!");
			e.printStackTrace();
			return null;
		}
		try {
			if (te instanceof TileGenericPipe) {
				TileGenericPipe tp = (TileGenericPipe)te;
				Pipe p = tp.pipe;
				int id = p.itemID;
				PipeTransport pt = p.transport;
				switch(pt.getPipeType()) {
				case FLUID:
					return this.getType(id, fluidPipes);
				case ITEM:
					return this.getType(id, itemPipes);
				case POWER:
					return this.getType(id, powerPipes);
				default:
					return null;
				}
			}
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Pipe reader failed!");
			e.printStackTrace();
			return null;
		}
		return null;
	}

	private Types getType(int id, String[] names) {
		for (int i = 0; i < names.length; i++) {
			String sg = names[i];
			int item = itemIDs.get(sg);
			if (id == item) {
				return this.getType(sg);
			}
		}
		return null;
	}

	private Types getType(String sg) {
		sg = sg.toLowerCase();
		sg = sg.replaceAll("pipe", "");
		sg = sg.replaceAll("items", "");
		sg = sg.replaceAll("fluids", "");
		sg = sg.replaceAll("power", "");
		for (int i = 0; i < Types.pipeList.length; i++) {
			String type = Types.pipeList[i].name().toLowerCase();
			if (type.equals(sg))
				return Types.pipeList[i];
		}
		return null;
	}

}
