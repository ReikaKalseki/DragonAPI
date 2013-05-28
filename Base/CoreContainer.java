/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * This code is provided under a modified visible-source license that is as follows:
 * 
 * Any and all users are permitted to use the source for educational purposes, or to create other mods that call
 * parts of this code and use DragonAPI as a dependency.
 * 
 * Unless given explicit written permission - electronic writing is acceptable - no user may redistribute this
 * source code nor any derivative works. These pre-approved works must prominently contain this copyright notice.
 * 
 * Additionally, no attempt may be made to achieve monetary gain from this code by anyone except the original author.
 * In the case of pre-approved derivative works, any monetary gains made will be shared between the original author
 * and the other developer(s), proportional to the ratio of derived to original code.
 * 
 * Finally, any and all displays, duplicates or derivatives of this code must be prominently marked as such, and must
 * contain attribution to the original author, including a link to the original source. Any attempts to claim credit
 * for this code will be treated as intentional theft.
 * 
 * Due to the Mojang and Minecraft Mod Terms of Service and Licensing Restrictions, compiled versions of this code
 * must be provided for free. However, with the exception of pre-approved derivative works, only the original author
 * may distribute compiled binary versions of this code.
 * 
 * Failure to comply with these restrictions is a violation of copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Libraries.ReikaMathLibrary;

public class CoreContainer extends Container {

	protected TileEntity tile;
	int posX; int posY; int posZ;
	protected EntityPlayer ep;

    public CoreContainer(EntityPlayer player, TileEntity te)
    {
        tile = te;
        posX = tile.xCoord;
        posY = tile.yCoord;
        posZ = tile.zCoord;
        ep = player;
        //this.detectAndSendChanges();
    }

    protected void addPlayerInventory(EntityPlayer player) {
        for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
                this.addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++)
        {
            this.addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18, 142));
        }
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); i++)
        {
            ICrafting icrafting = (ICrafting)crafters.get(i);
        }
    }

    @Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.isStandard8mReach(par1EntityPlayer);
    }

	public final boolean isStandard8mReach(EntityPlayer ep) {
		double dist = ReikaMathLibrary.py3d(tile.xCoord+0.5-ep.posX, tile.yCoord+0.5-ep.posY, tile.zCoord+0.5-ep.posZ);
		return (dist <= 8);
	}

    @Override
	public final ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)inventorySlots.get(par2);
        if (!(tile instanceof IInventory))
        	return null;
        int invsize = ((IInventory)tile).getSizeInventory();

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 < invsize)
            {
                if (!this.mergeItemStack(var5, invsize, inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var5, 0, invsize, false))
            {
                return null;
            }

            if (var5.stackSize == 0)
            {
                var4.putStack((ItemStack)null);
            }
            else
            {
                var4.onSlotChanged();
            }
        }

        return var3;
    }

}
