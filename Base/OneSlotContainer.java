/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * Unless given explicit written permission - electronic writing is acceptable - no user may
 * copy, edit, or redistribute this source code nor any derivative works.
 * Failure to comply with these restrictions is a violation of
 * copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class OneSlotContainer extends CoreContainer {

	private EntityPlayer ep;
	private TileEntity te;
	private IInventory inv;

	public OneSlotContainer(EntityPlayer player, TileEntity tile) {
		super(player, tile);
		te = tile;
		ep = player;
		inv = (IInventory)tile;
        int posX = te.xCoord;
        int posY = te.yCoord;
        int posZ = te.zCoord;
        this.addSlotToContainer(new Slot(inv, 0, 80, 35));

        this.addPlayerInventory(player);
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
	public void updateProgressBar(int par1, int par2)
    {
        switch(par1) {

        }
    }
}
