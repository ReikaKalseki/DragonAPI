package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SettingsEvent extends Event {

	public final GameSettings settings;

	public SettingsEvent(GameSettings gs) {
		settings = gs;
	}

	public static class Load extends SettingsEvent {

		public Load(GameSettings gs) {
			super(gs);
		}

	}

	public static class Save extends SettingsEvent {

		public Save(GameSettings gs) {
			super(gs);
		}

	}

	public static void fireLoad(GameSettings gs) {
		MinecraftForge.EVENT_BUS.post(new SettingsEvent.Load(gs));
	}

	public static void fireSave(GameSettings gs) {
		MinecraftForge.EVENT_BUS.post(new SettingsEvent.Save(gs));
	}

}
