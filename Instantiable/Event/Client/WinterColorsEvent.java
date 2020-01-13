package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;


public abstract class WinterColorsEvent extends Event {

	public final int defaultColor;
	public int chosenColor;

	public WinterColorsEvent(int c) {
		defaultColor = c;
		chosenColor = defaultColor;
	}

	public static class WinterSkyColorsEvent extends WinterColorsEvent {

		public WinterSkyColorsEvent() {
			super(0x688499);
		}

	}

	public static class WinterFogColorsEvent extends WinterColorsEvent {

		public WinterFogColorsEvent() {
			super(0x425766);
		}

	}

	public static int getSkyColor() {
		WinterColorsEvent evt = new WinterSkyColorsEvent();
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.chosenColor;
	}

	public static int getFogColor() {
		WinterColorsEvent evt = new WinterFogColorsEvent();
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.chosenColor;
	}

}
