package Reika.DragonAPI.Interfaces;

import net.minecraft.item.ItemStack;

public interface GradientBlend {

	public int getColorOne(ItemStack is);
	public int getColorTwo(ItemStack is);
	public int getColorThree(ItemStack is);
	public int getColorFour(ItemStack is);

}
