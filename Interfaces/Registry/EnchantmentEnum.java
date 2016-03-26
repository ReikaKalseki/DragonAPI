/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import net.minecraft.enchantment.Enchantment;


public interface EnchantmentEnum extends RegistryEntry {

	public Enchantment getEnchantment();

	public int getEnchantmentID();

}
