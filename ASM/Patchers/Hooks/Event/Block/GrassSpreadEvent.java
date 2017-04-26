/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;



public class GrassSpreadEvent extends BlockSpreadingEvent {

	public GrassSpreadEvent() {
		super("net.minecraft.block.BlockGrass", "alh");
	}

}
