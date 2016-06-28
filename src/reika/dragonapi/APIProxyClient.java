/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.auxiliary.DebugOverlay;
import reika.dragonapi.auxiliary.trackers.PlayerSpecificRenderer;
import reika.dragonapi.instantiable.EntityTumblingBlock;
import reika.dragonapi.instantiable.event.client.ResourceReloadEvent;
import reika.dragonapi.instantiable.rendering.RenderTumblingBlock;
import reika.dragonapi.io.DelegateFontRenderer;
import reika.dragonapi.io.DirectResourceManager;
import reika.dragonapi.io.ThrottleableEffectRenderer;
import reika.dragonapi.io.VanillaOnlyResourceManager;
import reika.dragonapi.libraries.io.ReikaSoundHelper;
import codechicken.lib.gui.GuiDraw;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

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
		ResourceReloadEvent.registerPre();

		//MinecraftForge.EVENT_BUS.register(PlayerModelRenderer.instance);
		//MinecraftForge.EVENT_BUS.register(CustomSoundHandler.instance);

		//key_nbt = new KeyBinding("TileEntity NBT Overlay", Keyboard.KEY_TAB, "DragonAPI");
		//ClientRegistry.registerKeyBinding(key_nbt);

		RenderingRegistry.registerEntityRenderingHandler(EntityTumblingBlock.class, new RenderTumblingBlock());
	}

	@Override
	public void registerSidedHandlersMain() {
		rm.registerReloadListener(DirectResourceManager.getInstance());
		rm.registerReloadListener(VanillaOnlyResourceManager.getInstance());
		ReikaSoundHelper.injectPaulscodeAccesses();
		Minecraft.getMinecraft().fontRenderer = new DelegateFontRenderer(Minecraft.getMinecraft().fontRenderer);
		if (ModList.NEI.isLoaded())
			GuiDraw.fontRenderer = Minecraft.getMinecraft().fontRenderer;
		Minecraft.getMinecraft().effectRenderer = new ThrottleableEffectRenderer(Minecraft.getMinecraft().effectRenderer);
	}

	@Override
	public void registerSidedHandlersGameLoaded() {
		PlayerSpecificRenderer.instance.registerIntercept();
		ResourceReloadEvent.registerPost();
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

	@Override
	public void postLoad() {
		PlayerSpecificRenderer.instance.loadGlowFiles();
	}

}
