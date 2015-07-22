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

public interface GradientBlend {

	public int getColorOne(ItemStack is);
	public int getColorTwo(ItemStack is);
	public int getColorThree(ItemStack is);
	public int getColorFour(ItemStack is);

}
