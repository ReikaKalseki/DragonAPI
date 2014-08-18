/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;

public class ModifiableAttributeMap extends ServersideAttributeMap {

	@Override
	public IAttributeInstance registerAttribute(IAttribute par1Attribute)
	{
		if (attributesByName.containsKey(par1Attribute.getAttributeUnlocalizedName()))
		{
			attributesByName.remove(par1Attribute.getAttributeUnlocalizedName());
		}
		return super.registerAttribute(par1Attribute);
	}
}
