/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.registry;

import net.minecraft.item.Item;

/** This is an interface for ENUMS! */
public interface ItemEnum extends RegistrationList {

	public Item getItemInstance();

	public boolean overwritingItem();

}
