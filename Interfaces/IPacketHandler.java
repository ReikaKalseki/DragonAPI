/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IPacketHandler {

	public void handleData(PacketObj packet, World world, EntityPlayer ep);

}
