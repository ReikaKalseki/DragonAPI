package Reika.DragonAPI.Extras;

import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public enum IDType {

	BLOCK(),
	ITEM(),
	ENTITY(),
	BIOME(),
	POTION(),
	FLUID(),
	FLUIDCONTAINER();

	public static final IDType[] list = values();

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}
}
