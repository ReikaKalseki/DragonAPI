/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

public interface MultiPageInventory extends ConditionBreakDropsInventory {

	public int getNumberPages();

	public int getSlotsOnPage(int page);

	public int getCurrentPage();

}
