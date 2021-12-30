package Reika.DragonAPI.ModInteract.ItemHandlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import Reika.DragonAPI.Exception.MisuseException;

public class HexcraftHandler {

	private static HexHandler handler;

	public static void setHandler(HexHandler h) {
		if (handler != null)
			throw new MisuseException("Handler already set!");
		handler = h;
	}

	public static HexHandler getActiveHandler() {
		return handler;
	}

	public static interface HexHandler {

		boolean isMonolith(Block id);

		boolean isWorldGenMonolith(Block id);

		public BasicHexColor[] getColors();

	}

	public static interface BasicHexColor {

		public Item getCrystal();

		public Block getMonolith(boolean nether);

		public boolean isPrimary(boolean nether);
	}

}
