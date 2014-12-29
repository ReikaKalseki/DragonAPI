/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Auxiliary.DebugOverlay;
import Reika.DragonAPI.Auxiliary.PlayerModelRenderer;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.Event.ResourceReloadEvent;
import cpw.mods.fml.client.FMLClientHandler;

public class APIProxyClient extends APIProxy {

	public static KeyBinding key_nbt;

	private static final SimpleReloadableResourceManager rm = (SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();

	@Override
	public void registerSounds() {
		
	}

	@Override
	public void registerSidedHandlers() {

	MinecraftForge.EVENT_BUS.register(DebugOverlay.instance);
		ResourceReloadEvent.register();

		PlayerModelRenderer.instance.register();
	}

	@Override
	public void registerSidedHandlersMain() {
		rm.registerReloadListener(DirectResourceManager.getInstance());
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
