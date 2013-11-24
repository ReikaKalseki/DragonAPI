/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class GuideCommand extends CommandBase {

	private final String tag = "dragonapi";

	@Override
	public String getCommandName() {
		return tag;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/"+tag;
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ItemStack is = new ItemStack(Item.enchantedBook);
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		NBTTagCompound data = new NBTTagCompound();
		NBTTagList data2 = new NBTTagList();
		data2.appendTag(new NBTTagString("key", "Reika's Mods Guide"));
		data.setTag("Lore", data2);
		is.stackTagCompound.setTag("display", data);
		ep.inventory.addItemStackToInventory(is);
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}



}
