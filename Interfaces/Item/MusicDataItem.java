package Reika.DragonAPI.Interfaces.Item;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.MusicScore;


public interface MusicDataItem {

	public MusicScore getMusic(ItemStack is);
	//public int[][][] getNoteblockMusic(ItemStack is);

}
