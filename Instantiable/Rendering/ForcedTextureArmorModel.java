/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class ForcedTextureArmorModel extends ModelBiped {

	public final String texture;
	public final Class modClass;
	public final int armorType;

	public ForcedTextureArmorModel(Class c, String tex, int type) {
		super(type == 1 ? 1F : 0.5F, 0.0F, 64, 32);
		texture = tex;
		modClass = c;
		armorType = type;
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float  par6, float par7) {
		ReikaTextureHelper.bindTexture(modClass, texture);

		double d2 = 0;
		if (armorType == 2) {
			d2 = 1.25;
		}

		this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
		switch(armorType) {
		case 0:
			//bipedHeadwear.render(par7);
			bipedHead.render(par7);
			break;
		case 1:
			bipedBody.render(par7);
			bipedRightArm.render(par7);
			bipedLeftArm.render(par7);
			break;
		case 2:
			double d = -0.5;
			d = 0;
			GL11.glTranslated(0, d, 0);
			GL11.glScaled(1, d2, 1);
			bipedRightLeg.render(par7);
			bipedLeftLeg.render(par7);
			GL11.glScaled(1, 1D/d2, 1);
			GL11.glTranslated(0, -d, 0);
			break;
		case 3:
			bipedRightLeg.render(par7);
			bipedLeftLeg.render(par7);
			break;
		}
	}

}
