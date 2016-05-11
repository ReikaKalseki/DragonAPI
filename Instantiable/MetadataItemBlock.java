/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;


public final class MetadataItemBlock extends ItemBlockWithMetadata {

	public MetadataItemBlock(Block b) {
		super(b, b);
	}

}
