/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Extras.ModifiedPlayerModel;
import Reika.DragonAPI.Extras.ReikaModel;
import Reika.DragonAPI.Extras.SamakiModel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerModelRenderer {

	public static final PlayerModelRenderer instance = new PlayerModelRenderer();

	private final ReikaModel modelReika = new ReikaModel();
	private final SamakiModel modelSamaki = new SamakiModel();
	private final HashMap<String, ModifiedPlayerModel> models = new HashMap();

	private PlayerModelRenderer() {
		models.put("Reika_Kalseki", modelReika);
		models.put("FurryDJ", modelSamaki);
	}

	public void register() {
		Map<Class, Render> map = RenderManager.instance.entityRenderMap;
		map.put(EntityPlayer.class, new CustomPlayerRenderer(map.get(EntityPlayer.class)));
	}

	// Render starts centered on eye position
	//@SubscribeEvent
	private void addCustomModel(EntityPlayer ep, float tick) {
		if (ep != null) {
			ModifiedPlayerModel model = models.get(ep.getCommandSenderName());
			if (model != null) {
				GL11.glPushMatrix();
				//render.setRenderPassModel(modelReika);
				model.bindTexture();
				GL11.glTranslated(0, 1.6, 0);
				GL11.glScaled(1, -1, 1);
				if (ep.isSneaking()) {
					GL11.glRotated(22.5, 1, 0, 0);
					GL11.glTranslated(-0.02, 0.1, -0.05);
				}
				GL11.glFrontFace(GL11.GL_CW);
				model.renderBodyParts(ep, tick);
				GL11.glFrontFace(GL11.GL_CCW);
				GL11.glPopMatrix();
			}
		}
	}

	private static final class CustomPlayerRenderer extends RenderPlayer {

		private CustomPlayerRenderer(Render original) {
			super();
			renderManager = RenderManager.instance;
		}

		@Override
		protected void rotateCorpse(EntityLivingBase ep, float par2, float par3, float partialTick)
		{
			super.rotateCorpse(ep, par2, par3, partialTick);
			PlayerModelRenderer.instance.addCustomModel((EntityPlayer)ep, partialTick);
		}

	}
}
