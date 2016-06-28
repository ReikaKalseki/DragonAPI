/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import reika.dragonapi.instantiable.data.blockstruct.StructureExport;
import reika.dragonapi.instantiable.data.immutable.Coordinate;
import reika.dragonapi.instantiable.data.maps.MultiMap;
import reika.dragonapi.instantiable.data.maps.PlayerMap;


public class StructureExportCommand extends DragonCommandBase {

	private final PlayerMap<Coordinate> click = new PlayerMap();
	private final PlayerMap<HashMap<String, Boolean>> states = new PlayerMap();
	private final MultiMap<UUID, String> tags = new MultiMap();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);

		if (args.length == 1) {
			if (args[0].equals("cleartags")) {
				tags.remove(ep.getUniqueID());
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"NBT tags cleared.");
			}
			else {
				HashMap<String, Boolean> map = states.get(ep);
				if (map == null) {
					map = new HashMap();
					states.put(ep, map);
				}
				Boolean b = map.get(args[0]);
				boolean e = b != null ? b.booleanValue() : false;
				map.put(args[0], !e);
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Flag '"+args[0]+"' toggled.");
			}
			return;
		}
		else if (args.length > 0) {
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
			HashMap<String, Boolean> map = states.get(ep);
			if (map != null) {
				Boolean b = map.get("encrypt");
				s.encryptData = b != null && b.booleanValue();

				b = map.get("compress");
				s.compressData = b != null && b.booleanValue();
			}
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
