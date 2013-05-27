package Reika.DragonAPI.Libraries;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaNBTHelper extends DragonAPICore {

	public static void writeInvToNBT(ItemStack[] inv, NBTTagCompound NBT) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inv.length; i++)
        {
            if (inv[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                inv[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        NBT.setTag("Items", nbttaglist);
	}

	public static ItemStack[] getInvFromNBT(NBTTagCompound NBT) {
        NBTTagList nbttaglist = NBT.getTagList("Items");
		ItemStack[] inv = new ItemStack[nbttaglist.tagCount()];

        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
            byte byte0 = nbttagcompound.getByte("Slot");

            if (byte0 >= 0 && byte0 < inv.length)
            {
                inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }
		return inv;
	}

}
