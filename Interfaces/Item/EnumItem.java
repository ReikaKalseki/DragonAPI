/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Item;

import Reika.DragonAPI.Interfaces.Registry.RegistrationList;
import net.minecraft.item.ItemStack;

public interface EnumItem {

	public RegistrationList getRegistry(ItemStack is);

}
