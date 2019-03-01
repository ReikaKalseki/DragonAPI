/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.RelayInventory;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.LootController.Location;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;


public class GenLootCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);

		if (args.length > 0) {
			Object inv = this.getInventory(ep);
			if (inv != null) {
				if (args.length == 3 && args[1].equalsIgnoreCase("preset")) {
					if (inv instanceof IInventory) {
						try {
							Presets p = Presets.valueOf(args[2].toUpperCase(Locale.ENGLISH));
							p.generate(DragonAPICore.rand, (IInventory)inv);
						}
						catch (IllegalArgumentException e) {
							this.sendChatToSender(ics, EnumChatFormatting.RED+"Preset '"+args[2]+"' does not exist. Options: "+Arrays.toString(Presets.values()));
						}
					}
					else {
						this.sendChatToSender(ics, EnumChatFormatting.RED+"Presets only work with inventory targets.");
					}
				}
				else {
					WeightedRandomChestContent[] types = ChestGenHooks.getItems(args[0], ep.getRNG());
					if (types.length == 0) {
						this.sendChatToSender(ics, EnumChatFormatting.RED+"Loot table "+args[0]+" is empty.");
					}
					else {
						int count = ChestGenHooks.getCount(args[0], ep.getRNG());
						int tries = args.length > 1 && ReikaJavaLibrary.isValidInteger(args[1]) ? Integer.parseInt(args[1]) : 1;
						for (int i = 0; i < tries; i++) {
							this.addItems(ics, ep, inv, args[0], types, count);
						}
					}
				}
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"No selected inventory.");
			}
		}
		else {
			try {
				Field f = ChestGenHooks.class.getDeclaredField("chestInfo");
				f.setAccessible(true);
				HashMap<String, ChestGenHooks> map = (HashMap<String, ChestGenHooks>)f.get(null);
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You must specify a loot table.");
				this.sendChatToSender(ics, "Valid types: "+map.keySet());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Object getInventory(EntityPlayer ep) {
		MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 5, false);
		if (mov != null) {
			TileEntity te = ep.worldObj.getTileEntity(mov.blockX, mov.blockY, mov.blockZ);
			if (InterfaceCache.GRIDHOST.instanceOf(te)) {
				return te;
			}
			else if (te instanceof IInventory) {
				return te;
			}
		}
		mov = ReikaPlayerAPI.getLookedAtEntity(ep, 5, false);
		if (mov != null) {
			return mov.entityHit;
		}
		return null;
	}

	private void addItems(ICommandSender ics, EntityPlayer ep, Object inv, String type, WeightedRandomChestContent[] types, int count) {
		if (InterfaceCache.GRIDHOST.instanceOf(inv)) {
			this.handleAE(ep, ep.getRNG(), types, (IGridHost)inv, count);
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Generated loot type "+type+" in ME System @ "+new Coordinate((TileEntity)inv));
		}
		else if (inv instanceof EntityMinecartChest) {
			WeightedRandomChestContent.generateChestContents(ep.getRNG(), types, (EntityMinecartChest)inv, count);
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Generated loot type "+type+" in minecart @ "+new Coordinate((Entity)inv));
		}
		else if (inv instanceof IInventory) {
			WeightedRandomChestContent.generateChestContents(ep.getRNG(), types, (IInventory)inv, count);
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Generated loot type "+type+" in chest @ "+new Coordinate((TileEntity)inv));
		}
	}

	@ModDependent(ModList.APPENG)
	private void handleAE(EntityPlayer ep, Random rand, WeightedRandomChestContent[] types, IGridHost te, int count) {
		RelayInventory relay = new RelayInventory(300);
		WeightedRandomChestContent.generateChestContents(rand, types, relay, count);
		IGridNode node = null;
		for (int i = 0; i < 6 && node == null; i++) {
			node = te.getGridNode(ForgeDirection.VALID_DIRECTIONS[i]);
		}
		if (node == null)
			return;
		MESystemReader me = new MESystemReader(node, ep);
		for (ItemStack is : relay.getItems()) {
			me.addItem(is, false);
		}
	}

	@Override
	public String getCommandString() {
		return "genloot";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	public static enum Presets {
		DUNGEON(Location.DUNGEON.tag),
		MINESHAFT(Location.MINESHAFT.tag),
		VILLAGE(Location.VILLAGE.tag),
		JUNGLE(Location.JUNGLE_PUZZLE.tag),
		PYRAMID(Location.PYRAMID.tag),
		NETHER(null);

		private final ChestGenHooks location;
		//private final int numberPasses;

		private Presets(String s) {
			location = s != null ? ChestGenHooks.getInfo(s) : null;
			//numberPasses = n;
		}

		public void generate(Random rand, IInventory inv) {
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				inv.setInventorySlotContents(i, null);
			}
			WeightedRandomChestContent[] items;
			int count;
			if (this == NETHER) {
				items = StructureNetherBridgePieces.Piece.field_111019_a;
				count = 2+rand.nextInt(4);
			}
			else {
				items = location.getItems(rand);
				count = location.getCount(rand);
			}
			WeightedRandomChestContent.generateChestContents(rand, items, inv, count);
		}
	}

}
