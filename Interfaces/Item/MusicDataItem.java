/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Item;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.MusicScore;


public interface MusicDataItem {

	public MusicScore getMusic(ItemStack is);
	//public int[][][] getNoteblockMusic(ItemStack is);

}
