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
import java.util.HashMap;
import java.util.Random;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.RelayInventory;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
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
			MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 5, false);
			if (mov != null) {
				TileEntity te = ep.worldObj.getTileEntity(mov.blockX, mov.blockY, mov.blockZ);
				WeightedRandomChestContent[] types = ChestGenHooks.getItems(args[0], ep.getRNG());
				if (types.length == 0) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"Loot table "+args[0]+" is empty.");
				}
				else {
					int count = ChestGenHooks.getCount(args[0], ep.getRNG());
					int tries = args.length > 1 && ReikaJavaLibrary.isValidInteger(args[1]) ? Integer.parseInt(args[1]) : 1;
					for (int i = 0; i < tries; i++) {
						if (InterfaceCache.GRIDHOST.instanceOf(te)) {
							this.handleAE(ep, ep.getRNG(), types, (IGridHost)te, count);
							this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Generated loot type "+args[0]+" in ME System @ "+new Coordinate(te));
						}
						else if (te instanceof IInventory) {
							WeightedRandomChestContent.generateChestContents(ep.getRNG(), types, (IInventory)te, count);
							this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Generated loot type "+args[0]+" in chest @ "+new Coordinate(te));
						}
						else {
							this.sendChatToSender(ics, EnumChatFormatting.RED+"TileEntity at "+mov.blockX+", "+mov.blockY+", "+mov.blockZ+" is not an inventory.");
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

}
