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

import java.io.IOException;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructureExport;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;


public class StructureExportCommand extends DragonCommandBase {

	private final PlayerMap<Coordinate> click = new PlayerMap();
	private final MultiMap<UUID, String> tags = new MultiMap();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		if (args.length > 0) {
			UUID uid = ep.getPersistentID();
			for (int i = 0; i < args.length; i++) {
				tags.addValue(uid, args[i]);
			}
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+String.valueOf(args.length)+" tags added.");
			return;
		}

		if (click.containsKey(ep)) {
			Coordinate c1 = click.get(ep);
			this.sendChatToSender(ics, EnumChatFormatting.YELLOW+"Position 2 set.");
			Coordinate c2 = new Coordinate(ep);
			String name = String.valueOf(System.currentTimeMillis());
			click.remove(ep);

			StructureExport s = new StructureExport(name);
			int x1 = Math.min(c1.xCoord, c2.xCoord);
			int y1 = Math.min(c1.yCoord, c2.yCoord);
			int z1 = Math.min(c1.zCoord, c2.zCoord);
			int x2 = Math.max(c1.xCoord, c2.xCoord);
			int y2 = Math.max(c1.yCoord, c2.yCoord);
			int z2 = Math.max(c1.zCoord, c2.zCoord);
			s.addWatchedNBT(tags.get(ep.getPersistentID()));
			s.addRegion(ep.worldObj, x1, y1, z1, x2, y2, z2);
			try {
				s.save();
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Structure exported.");
			}
			catch (IOException e) {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"Structure not exported: "+e.toString());
				e.printStackTrace();
			}
		}
		else {
			this.sendChatToSender(ics, EnumChatFormatting.YELLOW+"Position 1 set.");
			click.put(ep, new Coordinate(ep));
		}
	}

	@Override
	public String getCommandString() {
		return "exportstructure";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
