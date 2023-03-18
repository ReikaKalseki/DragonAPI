/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

@Cancelable
public class PlayerOpenGuiEvent extends Event {

	public final EntityPlayer player;
	public final Object mod;
	public final int guiID;
	public final World world;
	public final int posX;
	public final int posY;
	public final int posZ;

	public PlayerOpenGuiEvent(EntityPlayer ep, Object md, int id, World w, int x, int y, int z) {
		player = ep;
		mod = md;
		guiID = id;
		world = w;
		posX = x;
		posY = y;
		posZ = z;
	}

	public static void fire(EntityPlayer ep, Object mod, int modGuiId, World world, int x, int y, int z) {
		if (!MinecraftForge.EVENT_BUS.post(new PlayerOpenGuiEvent(ep, mod, modGuiId, world, x, y, z)))
			FMLNetworkHandler.openGui(ep, mod, modGuiId, world, x, y, z);
	}

}
