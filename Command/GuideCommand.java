/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class GuideCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "dragonapi";
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ItemStack is = new ItemStack(Items.enchanted_book);
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		NBTTagCompound data = new NBTTagCompound();
		NBTTagList data2 = new NBTTagList();
		data2.appendTag(new NBTTagString("Reika's Mods Guide"));
		data.setTag("Lore", data2);
		is.stackTagCompound.setTag("display", data);
		ep.inventory.addItemStackToInventory(is);
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

}
