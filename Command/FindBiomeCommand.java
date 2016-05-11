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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class FindBiomeCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		int r = 8192;
		int step = 256;
		BiomeGenBase target = null;
		if (args.length == 1) {
			try {
				target = BiomeGenBase.biomeList[Integer.parseInt(args[0])];
			}
			catch (NumberFormatException e) {
				target = ReikaBiomeHelper.getBiomeByName(args[0]);
			}
		}
		MultiMap<String, Coordinate> map = new MultiMap();
		for (int x = -r; x <= r; x += step) {
			for (int z = -r; z <= r; z += step) {
				BiomeGenBase b = ReikaWorldHelper.getNaturalGennedBiomeAt(ep.worldObj, x, z);
				map.addValue(b.biomeName, new Coordinate(x, 0, z));
			}
		}
		if (target == null) {
			for (String n : map.keySet()) {
				this.sendChatToSender(ics, n+": "+map.get(n));
			}
		}
		else {
			this.sendChatToSender(ics, target.biomeName+": "+map.get(target.biomeName));
		}
	}

	@Override
	public String getCommandString() {
		return "findbiome";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
