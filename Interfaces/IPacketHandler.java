package Reika.DragonAPI.Interfaces;

import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IPacketHandler {

	public void handleData(PacketObj packet, World world, EntityPlayer ep);

}
