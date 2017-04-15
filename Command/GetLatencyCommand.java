package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class GetLatencyCommand extends DragonClientCommand {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		long time = System.currentTimeMillis();
		int[] l = ReikaJavaLibrary.splitLong(time);
		long val = ReikaJavaLibrary.buildLong(l[0], l[1]);
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.GETLATENCY.ordinal(), new PacketTarget.ServerTarget(), l[0], l[1]);
	}

	@Override
	public String getCommandString() {
		return "latency";
	}

}
