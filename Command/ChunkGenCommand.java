/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import Reika.DragonAPI.Auxiliary.Trackers.ChunkPregenerator;


public class ChunkGenCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 4) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid arguments. Use [dim] [perTick] [ctrX] [ctrZ] [radius].");
			return;
		}
		try {
			int dim = Integer.parseInt(args[0]);
			WorldServer world = DimensionManager.getWorld(dim);
			double pertick = Double.parseDouble(args[1]);
			int ctrX = Integer.parseInt(args[2]);
			int ctrZ = Integer.parseInt(args[3]);
			int radius = Integer.parseInt(args[4]);
			ChunkPregenerator.instance.addChunks(world, pertick, ctrX, ctrZ, radius);
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Queued a pregen of chunks R="+radius+" around "+ctrX+","+ctrZ+" in DIM"+dim+". Genning "+pertick+" c/t.");
		}
		catch (NumberFormatException e) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Error parsing argument: "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String getCommandString() {
		return "pregen";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
