package Reika.DragonAPI.Interfaces;

import net.minecraft.item.ItemStack;

public interface ActivatedInventoryItem {

	public ItemStack[] getInventory(ItemStack is);

	public void decrementSlot(ItemStack is, int slot);

	public boolean isSlotActive(ItemStack is, int slot);

}
