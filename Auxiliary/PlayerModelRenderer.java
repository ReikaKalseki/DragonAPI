/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Extras.ReikaModel;
import Reika.DragonAPI.Extras.SamakiModel;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerModelRenderer {

	public static final PlayerModelRenderer instance = new PlayerModelRenderer();

	private static final ReikaModel modelReika = new ReikaModel();
	private static final SamakiModel modelSamaki = new SamakiModel();

	private PlayerModelRenderer() {

	}

	@ForgeSubscribe
	public void changeReikaName(NameFormat e) {
		if ("Reika_Kalseki".equals(e.username)) {
			//e.displayname = "Reika";
		}
	}

	// Render starts centered on eye position
	@ForgeSubscribe
	public void addReikaModel(RenderPlayerEvent.Pre evt) {
		RenderPlayer render = evt.renderer;
		EntityPlayer ep = evt.entityPlayer;
		float tick = evt.partialRenderTick;
		if (ep != null) {
			if ("Reika_Kalseki".equals(ep.getEntityName())) {
				//render.setRenderPassModel(modelReika);
				ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "/Reika/DragonAPI/Resources/reika_tex.png");
				GL11.glScaled(1, -1, 1);
				GL11.glFrontFace(GL11.GL_CW);
				modelReika.renderBodyParts(ep, tick);
				GL11.glFrontFace(GL11.GL_CCW);
				GL11.glScaled(1, -1, 1);
			}
			else if ("FurryDJ".equals(ep.getEntityName())) {
				ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "/Reika/DragonAPI/Resources/samaki_tex.png");
				GL11.glScaled(1, -1, 1);
				GL11.glFrontFace(GL11.GL_CW);
				modelSamaki.renderBodyParts(ep, tick);
				GL11.glFrontFace(GL11.GL_CCW);
				GL11.glScaled(1, -1, 1);
			}
		}
	}
}
