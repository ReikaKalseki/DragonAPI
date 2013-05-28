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
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
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

        for (var4 = 0; var4 < numRows; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(lowerInv, var5 + var4 * 9, 8 + var5 * 18, 18 + var4 * 18));
            }
        }

        for (var4 = 0; var4 < 3; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(player.inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 103 + var4 * 18 + var3));
            }
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            this.addSlotToContainer(new Slot(player.inventory, var4, 8 + var4 * 18, 161 + var3));
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
