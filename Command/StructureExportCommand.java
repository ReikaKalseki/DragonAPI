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

	private final PlayerMap<String> name = new PlayerMap();
	private final PlayerMap<Coordinate> position1 = new PlayerMap();
	private final PlayerMap<Coordinate> position2 = new PlayerMap();
	private final MultiMap<UUID, String> tags = new MultiMap();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		switch(args[0]) {
			case "name":
				name.put(ep, args[1]);
				this.sendChatToSender(ics, EnumChatFormatting.YELLOW+"Name set.");
				break;
			case "pos1":
				position1.put(ep, new Coordinate(ep));
				this.sendChatToSender(ics, EnumChatFormatting.YELLOW+"Position 1 set.");
				break;
			case "pos2":
				position2.put(ep, new Coordinate(ep));
				this.sendChatToSender(ics, EnumChatFormatting.YELLOW+"Position 2 set.");
				break;
			case "save": {
				Coordinate c1 = position1.get(ep);
				Coordinate c2 = position2.get(ep);
				String n = name.get(ep);
				if (c1 == null || c2 == null || n == null) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"You must select two positions and a name first.");
					return;
				}
				StructureExport s = new StructureExport(n);
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
				break;
			}
			case "load": {
				String n = name.get(ep);
				if (n == null) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"You must select a name first.");
					return;
				}
				StructureExport s = new StructureExport(n);
				try {
					s.load();
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Structure loaded.");
				}
				catch (IOException e) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"Structure not loaded: "+e.toString());
					e.printStackTrace();
				}
				break;
			}
			case "tag": {
				UUID uid = ep.getPersistentID();
				for (int i = 1; i < args.length; i++) {
					tags.addValue(uid, args[i]);
				}
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+String.valueOf(args.length-1)+" tags added.");
				break;
			}
			case "place": {
				Coordinate c1 = position1.get(ep);
				Coordinate c2 = position2.get(ep);
				String n = name.get(ep);
				if (c1 == null || c2 == null || n == null) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"You must select two positions and a name first.");
					return;
				}
				StructureExport s = new StructureExport(n);
				try {
					s.load();
					Coordinate offset = new Coordinate(ep).getDifference(c1);
					s.offset(offset);
					s.place(ep.worldObj);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Structure placed.");
				}
				catch (IOException e) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"Structure not placed: "+e.toString());
					e.printStackTrace();
				}
				break;
			}
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
