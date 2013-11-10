/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Resources;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ModifiedPlayerModel extends ModelBiped {

	public ModifiedPlayerModel() {
		super();
		textureWidth = 64;
		textureHeight = 32;

		this.init();
	}

	public static final float RADIAN = toRadians();

	private static float toRadians() {
		return (float)(Math.PI/180);
	}

	protected abstract void setPositions();

	protected abstract void init();

	public abstract void setPartAngles(EntityPlayer ep, float tick);

	public abstract void renderBodyParts(EntityPlayer ep, float tick);

	protected final void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
