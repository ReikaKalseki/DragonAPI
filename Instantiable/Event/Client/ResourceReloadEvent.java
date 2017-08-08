/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourceReloadEvent extends Event {

	public final int loadState;

	public ResourceReloadEvent(int s) {
		loadState = s;
	}

	public static void registerPre() {
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new Watcher(0));
	}

	public static void registerPost() {
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new Watcher(1));
	}

	private static class Watcher implements IResourceManagerReloadListener {

		private final int state;

		private Watcher(int s) {
			state = s;
		}

		@Override
		public void onResourceManagerReload(IResourceManager rm) {
			MinecraftForge.EVENT_BUS.post(new ResourceReloadEvent(state));
		}

	}

}
