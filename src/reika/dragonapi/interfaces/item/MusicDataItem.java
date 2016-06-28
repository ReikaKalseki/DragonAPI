/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.item;

import net.minecraft.item.ItemStack;
import reika.dragonapi.instantiable.MusicScore;


public interface MusicDataItem {

	public MusicScore getMusic(ItemStack is);
	//public int[][][] getNoteblockMusic(ItemStack is);

}
