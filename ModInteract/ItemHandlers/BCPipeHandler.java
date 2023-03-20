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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.ModHandlerBase;

import buildcraft.transport.TileGenericPipe;

public class BCPipeHandler extends ModHandlerBase {

	public static enum Materials {
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
		OBSIDIAN(),
		DAIZULI();

		public static final Materials[] list = values();
	}

	public static enum Pipes {
		pipeFluidsWood(Materials.WOOD),
		pipeFluidsCobblestone(Materials.COBBLESTONE),
		pipeFluidsStone(Materials.STONE),
		pipeFluidsIron(Materials.IRON),
		pipeFluidsGold(Materials.GOLD),
		pipeFluidsVoid(Materials.VOID),
		pipeFluidsSandstone(Materials.SANDSTONE),
		pipeFluidsDiamond(Materials.DIAMOND),
		pipeFluidsEmerald(Materials.EMERALD),

		pipeItemsWood(Materials.WOOD),
		pipeItemsEmerald(Materials.EMERALD),
		pipeItemsStone(Materials.STONE),
		pipeItemsCobblestone(Materials.COBBLESTONE),
		pipeItemsIron(Materials.IRON),
		pipeItemsQuartz(Materials.QUARTZ),
		pipeItemsGold(Materials.GOLD),
		pipeItemsDiamond(Materials.DIAMOND),
		pipeItemsObsidian(Materials.OBSIDIAN),
		pipeItemsLapis(Materials.LAPIS),
		pipeItemsDaizuli(Materials.DAIZULI),
		pipeItemsVoid(Materials.VOID),
		pipeItemsSandstone(Materials.SANDSTONE),

		pipePowerWood(Materials.WOOD),
		pipePowerCobblestone(Materials.COBBLESTONE),
		pipePowerStone(Materials.STONE),
		pipePowerQuartz(Materials.QUARTZ),
		pipePowerGold(Materials.GOLD),
		pipePowerIron(Materials.IRON),
		pipePowerDiamond(Materials.DIAMOND),
		pipePowerEmerald(Materials.EMERALD),
		;

		public static Pipes[] list = values();

		public final Materials material;

		private Pipes(Materials m) {
			material = m;
		}
	}

	private final EnumMap<Pipes, Item> itemIDs = new EnumMap(Pipes.class);
	private final HashMap<Item, Pipes> reverseIDs = new HashMap();

	private static final BCPipeHandler instance = new BCPipeHandler();

	/** Pipe Block ID */
	public final Block pipeID;

	private BCPipeHandler() {
		super();
		Block idpipe = null;
		if (this.hasMod()) {
			Class transport = this.getMod().getBlockClass();
			for (int i = 0; i < Pipes.list.length; i++) {
				Item id = this.getPipeItemID(transport, Pipes.list[i].name());
				itemIDs.put(Pipes.list[i], id);
				reverseIDs.put(id, Pipes.list[i]);
			}
			try {
				Field pipe = transport.getField("genericPipeBlock");
				idpipe = ((Block)pipe.get(null));
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

		pipeID = idpipe;
	}

	private Item getPipeItemID(Class c, String varname) {
		try {
			Field f = c.getField(varname);
			Item id = ((Item)f.get(null));
			return id;
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
			DragonAPICore.log("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
			e.printStackTrace();
			this.logFailure(e);
		}
		return null;
	}

	public static BCPipeHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return pipeID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BCTRANSPORT;
	}

	public Materials getPipeType(TileEntity te) {
		if (!this.initializedProperly() || !ModList.BCTRANSPORT.isLoaded())
			return null;
		return this.doGetPipeType(te);
	}

	@ModDependent(ModList.BCTRANSPORT)
	public Materials doGetPipeType(TileEntity te) {
		if (te instanceof TileGenericPipe) {
			TileGenericPipe tp = (TileGenericPipe)te;
			Pipes p = reverseIDs.get(tp.pipe.item);
			return p == null ? null : p.material;
		}
		return null;
	}

	public Item getPipe(Pipes p) {
		return itemIDs.get(p);
	}

	private Materials getType(String sg) {
		sg = sg.toLowerCase(Locale.ENGLISH);
		sg = sg.replaceAll("pipe", "");
		sg = sg.replaceAll("items", "");
		sg = sg.replaceAll("fluids", "");
		sg = sg.replaceAll("power", "");
		for (int i = 0; i < Materials.list.length; i++) {
			String type = Materials.list[i].name().toLowerCase(Locale.ENGLISH);
			if (type.equals(sg))
				return Materials.list[i];
		}
		return null;
	}

}
