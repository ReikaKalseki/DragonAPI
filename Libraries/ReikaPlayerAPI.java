package Reika.DragonAPI.Libraries;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class ReikaPlayerAPI {
	
	private ReikaPlayerAPI() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}
	
	public static void transferInventoryToChest(EntityPlayer ep, ItemStack[] inv) {
		int num = ReikaInventoryHelper.getTotalUniqueStacks(ep.inventory.mainInventory);
		if (num >= inv.length)
			return;
	}
	
	public static void clearHotbar(EntityPlayer ep) {
		for (int i = 0; i < 9; i++)
			ep.inventory.mainInventory[i] = null;
	}
	
	public static void clearInventory(EntityPlayer ep) {
		for (int i = 0; i < ep.inventory.mainInventory.length; i++)
			ep.inventory.mainInventory[i] = null;
	}
	
	public static void cleanInventory(EntityPlayer ep, boolean hotbar) {
		
	}
}
