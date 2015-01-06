/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;

public class ResourceReloadEvent extends Event {

	public static void register() {
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new Watcher());
	}

	private static class Watcher implements IResourceManagerReloadListener {

		@Override
		public void onResourceManagerReload(IResourceManager rm) {
			MinecraftForge.EVENT_BUS.post(new ResourceReloadEvent());
		}

	}

	private ResourceReloadEvent() {

	}

}
