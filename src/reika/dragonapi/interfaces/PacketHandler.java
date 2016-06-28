/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import reika.dragonapi.libraries.io.ReikaPacketHelper.PacketObj;

public interface PacketHandler {

	public void handleData(PacketObj packet, World world, EntityPlayer ep);

}
