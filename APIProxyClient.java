/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import codechicken.lib.gui.GuiDraw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Auxiliary.DebugOverlay;
import Reika.DragonAPI.Auxiliary.PlayerModelRenderer;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.Event.ResourceReloadEvent;
import Reika.DragonAPI.Instantiable.IO.DelegateFontRenderer;
import cpw.mods.fml.client.FMLClientHandler;

public class APIProxyClient extends APIProxy {

	public static KeyBinding key_nbt;

	private static final SimpleReloadableResourceManager rm = (SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();

	@Override
	public void registerSounds() {
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(ReactorCraft.instance, SoundRegistry.soundList));
	}

	@Override
	public void registerSidedHandlers() {

		//Minecraft mc = Minecraft.getMinecraft();
		//mc.mcResourceManager = new CustomResourceManager((SimpleReloadableResourceManager)mc.mcResourceManager);

		MinecraftForge.EVENT_BUS.register(DebugOverlay.instance);
		ResourceReloadEvent.register();

		//MinecraftForge.EVENT_BUS.register(PlayerModelRenderer.instance);
		PlayerModelRenderer.instance.register();
		//MinecraftForge.EVENT_BUS.register(CustomSoundHandler.instance);

		//key_nbt = new KeyBinding("TileEntity NBT Overlay", Keyboard.KEY_TAB, "DragonAPI");
		//ClientRegistry.registerKeyBinding(key_nbt);
	}

	@Override
	public void registerSidedHandlersMain() {
		rm.registerReloadListener(DirectResourceManager.getInstance());
		Minecraft.getMinecraft().fontRenderer = new DelegateFontRenderer(Minecraft.getMinecraft().fontRenderer);
		if (ModList.NEI.isLoaded())
			GuiDraw.fontRenderer = Minecraft.getMinecraft().fontRenderer;
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
