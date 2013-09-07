package Reika.DragonAPI;

import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;

public class APIProxyClient extends APIProxy {

	@Override
	public void registerSounds() {
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(ReactorCraft.instance, SoundRegistry.soundList));
	}

	@Override
	public void registerRenderers() {

	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
