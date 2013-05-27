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
import net.minecraft.tileentity.TileEntity;

public abstract class ProgressContainer extends CoreContainer {

	public ProgressContainer(EntityPlayer player, TileEntity te) {
		super(player, te);
	}

	@Override
	public abstract void detectAndSendChanges();

	@Override
	public abstract void updateProgressBar(int index, int value);

}
