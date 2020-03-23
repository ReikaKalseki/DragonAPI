/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.List;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.WorldgenProfiler;
import Reika.DragonAPI.Auxiliary.Trackers.WorldgenProfiler.GeneratorProfile;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;


public class WorldGenProfilerCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length < 1) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Wrong number of arguments. Specify 'disable', 'enable', or 'display'.");
			return;
		}
		switch(args[0].toLowerCase(Locale.ENGLISH)) {
			case "disable":
				WorldgenProfiler.finishProfiling();
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Profiling finished.");
				break;
			case "enable":
				World world = ics instanceof EntityPlayerMP ? this.getCommandSenderAsPlayer(ics).worldObj : DimensionManager.getWorld(Integer.parseInt(args[1]));
				if (WorldgenProfiler.enableProfiling(world))
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Profiling started in world #"+world.provider.dimensionId+".");
				else
					this.sendChatToSender(ics, EnumChatFormatting.RED+"Profiling already running or its hooks are not enabled!");
				break;
			case "display":
				int worldid = WorldgenProfiler.getWorld();
				List<GeneratorProfile> li = WorldgenProfiler.getProfilingData();
				int chunks = WorldgenProfiler.getProfiledChunks().size();
				long total = WorldgenProfiler.getTotalProfilingTime();
				String totalt = String.format("%.6f", total/1000000D);
				String desc = "Profiling data for world #"+worldid+" contains "+li.size()+" generators across "+chunks+" chunks, total time "+totalt+" ms:";
				//this.sendChatToSender(ics, desc);
				DragonAPICore.log(desc);
				for (GeneratorProfile g : li) {
					long time = g.getAverageTime();
					String s = ReikaStringParser.padToLength("'"+g.identifier+"'", 96, " ");
					double percent = g.getTotalTime()*100D/total;
					//String sg = String.format("Generator %s - Average Time Per Chunk: %7.3f microseconds (%2.3f%s); Spilled chunks: %4d; Block changes: %7d", s, time/1000D, percent, "%%", g.getSpilledChunks(), g.getBlockChanges());
					String sg = String.format("Generator %s - Average Time Per Chunk: %7.3f microseconds (%2.3f%s); Spilled chunks: %4d", s, time/1000D, percent, "%%", g.getSpilledChunks());
					//this.sendChatToSender(ics, sg);
					DragonAPICore.log(sg);
				}
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Profiling data written to log.");
				break;
			default:
				this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid argument. Specify 'disable', 'enable', or 'display'.");
				break;
		}
	}

	@Override
	public String getCommandString() {
		return "profilegen";
	}

	@Override
	protected boolean isAdminOnly() {
		return DragonOptions.ADMINPROFILERS.getState();
	}

}
