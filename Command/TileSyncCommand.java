/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class TileSyncCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		if (args.length != 2) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"Invalid arguments. Specify a range and a sync depth.");
			return;
		}
		int r = ReikaJavaLibrary.safeIntParse(args[0]);
		if (r <= 0) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"Invalid range '"+args[0]+"'.");
			return;
		}
		int nbt = ReikaJavaLibrary.safeIntParse(args[1]);
		World world = ep.worldObj;
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (ReikaWorldHelper.tileExistsAt(world, dx, dy, dz)) {
						TileEntity te = world.getTileEntity(dx, dy, dz);
						if (te instanceof TileEntityBase)
							((TileEntityBase)te).syncAllData(nbt > 0);
					}
				}
			}
		}
	}

	@Override
	public String getCommandString() {
		return "tilesync";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
