package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;


public class MemoryUsageCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		System.gc();

		Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory();
		long allocated = runtime.totalMemory();
		long free = runtime.freeMemory();
		long totalFree = free+(max-allocated);
		long used = max-totalFree;

		sendChatToSender(ics, "Memory Usage Info:");
		sendChatToSender(ics, "Used memory: "+MathHelper.floor_double(((double)used/max)*100)+"%% ("+this.bytesToMB(used)+"MB) of "+this.bytesToMB(max)+"MB");
		sendChatToSender(ics, "Allocated memory: "+MathHelper.floor_double(((double)allocated/max)*100)+"%% ("+this.bytesToMB(allocated)+" MB)");
	}

	private String bytesToMB(long mem) {
		return String.valueOf(mem >> 20);
	}

	@Override
	public String getCommandString() {
		return "checkram";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
