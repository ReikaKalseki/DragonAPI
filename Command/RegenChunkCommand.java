package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;


public class RegenChunkCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		WorldServer world = (WorldServer)ep.worldObj;
		int x = MathHelper.floor_double(ep.posX);
		int z = MathHelper.floor_double(ep.posZ);
		Chunk c = world.getChunkFromBlockCoords(x, z);
		ReikaChunkHelper.regenChunk(world, x, z);
	}

	@Override
	public String getCommandString() {
		return "regenchunk";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
