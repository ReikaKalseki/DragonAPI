/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import net.minecraft.item.Item;

/** This is an interface for ENUMS! */
public interface ItemEnum extends RegistrationList {

	public Item getItemInstance();

	public boolean overwritingItem();

}
