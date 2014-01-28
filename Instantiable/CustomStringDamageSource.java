/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;

public class CustomStringDamageSource extends DamageSource {

	private final String message;

	/** Takes one arg - the rest of the message after the player's name.
	 * For example, supplying "was sucked into a jet engine" turns into
	 * "[Player] was sucked into a jet engine". */
	public CustomStringDamageSource(String msg) {
		super("custom");
		message = msg;
	}

	@Override
	public ChatMessageComponent getDeathMessage(EntityLivingBase e)
	{
		ChatMessageComponent ch = new ChatMessageComponent();
		ch.addText(e.getEntityName()+" "+message);
		return ch;
	}

	@Override
	public DamageSource setDamageBypassesArmor() {
		return super.setDamageBypassesArmor();
	}

	@Override
	public DamageSource setDamageAllowedInCreativeMode() {
		return super.setDamageAllowedInCreativeMode();
	}

}
