/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

/** This is an interface for ENUMS! */
public interface BlockEnum extends RegistrationList {

	public Block getBlockInstance();

	public Class<? extends ItemBlock> getItemBlock();

	public boolean hasItemBlock();

	public Item getItem();

}
