package Reika.DragonAPI.Base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.tileentity.TileEntity;

public class ContainerBasicStorage extends CoreContainer {

	IInventory lowerInv;

	public ContainerBasicStorage(EntityPlayer player, TileEntity te) {
		super(player, te);
        lowerInv = (IInventory)te;
        tile = te;
        int numRows = lowerInv.getSizeInventory() / 9;
        lowerInv.openChest();
        int var3 = (numRows - 4) * 18;
        int var4;
        int var5;
        int py = 0;
        if (numRows < 3) {
        	py = (3-numRows)*18;
        }
        for (var4 = 0; var4 < numRows; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(lowerInv, var5 + var4 * 9, 8 + var5 * 18, 18 + var4 * 18+py));
            }
        }

        for (var4 = 0; var4 < 3; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(player.inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 103 + var4 * 18 + var3+py));
            }
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            this.addSlotToContainer(new Slot(player.inventory, var4, 8 + var4 * 18, 161 + var3+py));
        }
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
	public final void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
    {
        super.onCraftGuiClosed(par1EntityPlayer);
        lowerInv.closeChest();
    }

    public final IInventory getLowerInventory()
    {
        return lowerInv;
    }

}
