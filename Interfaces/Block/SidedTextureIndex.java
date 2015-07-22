/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Block;

public interface SidedTextureIndex {
	
	public int getBlockTextureFromSideAndMetadata(int side, int metadata);
	public int getRenderType();
}
