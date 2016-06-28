/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.DragonOptions;
import reika.dragonapi.extras.ModifiedPlayerModel;
import reika.dragonapi.extras.ReikaModel;
import reika.dragonapi.extras.SamakiModel;
import reika.dragonapi.instantiable.data.maps.MultiMap;
import reika.dragonapi.interfaces.PlayerRenderObj;
import reika.dragonapi.io.ReikaFileReader;
import reika.dragonapi.libraries.io.ReikaRenderHelper;
import reika.dragonapi.libraries.io.ReikaTextureHelper;
import reika.dragonapi.libraries.java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PlayerSpecificRenderer {

	public static final PlayerSpecificRenderer instance = new PlayerSpecificRenderer();

	private final MultiMap<UUID, PlayerRenderObj> renders = new MultiMap().setNullEmpty().setOrdered(new RenderComparator());
	private final HashMap<UUID, String> glows = new HashMap();
	private final HashMap<UUID, String> customGlows = new HashMap();

	private final ReikaModel modelReika = new ReikaModel();
	private final SamakiModel modelSamaki = new SamakiModel();

	private PlayerSpecificRenderer() {
		this.registerRenderer(DragonAPICore.Reika_UUID, new PlayerModelRenderer(modelReika));
		this.registerRenderer(UUID.fromString("bca741d8-d934-4785-9c26-f6a4141be124"), new PlayerModelRenderer(modelSamaki));

		this.registerGlow(DragonAPICore.Reika_UUID, "reika_glow");
		this.registerGlow(UUID.fromString("bca741d8-d934-4785-9c26-f6a4141be124"), "samaki_glow");
		this.registerGlow(UUID.fromString("d859c5ea-37e9-43d7-b3b9-523e448bfda0"), "frey_glow");
	}

	public void registerIntercept() {
		Map<Class, Render> map = RenderManager.instance.entityRenderMap;
		map.put(EntityPlayer.class, new CustomPlayerRenderer(map.get(EntityPlayer.class)));
	}

	public void registerRenderer(UUID uuid, PlayerRenderObj r) {
		renders.addValue(uuid, r);

		//If anyone flips out over this and complains "OMG REIKA GIVES HIMSELF ALL THE RENDERS IN THE DEV ENVIRONMENT! DRM!",
		//You are:
		//If you cannot understand Java: making wild accusations based on your own ignorance
		//If you can understand Java: A disgrace to other programmers for harassing a developer over what you should understand is harmless
		//Update: Someone did it. Congratulations. Now go eat the contents of your toilet.
		//if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
		//	renders.addValue(DragonAPICore.Reika_UUID, r);
		//}
	}

	private void registerGlow(UUID uuid, String s) {
		glows.put(uuid, "/Reika/DragonAPI/Resources/"+s+".png");
	}

	public void loadGlowFiles() {
		File f = new File(DragonAPICore.getMinecraftDirectory(), "config/Reika/glowrenders.dat");
		if (f.exists()) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
			for (String s : li) {
				try {
					String[] parts = s.split("=");
					UUID uid = UUID.fromString(parts[0]);
					File img = new File(DragonAPICore.getMinecraftDirectory(), "config/Reika/glowrenders/"+parts[1]);
					if (!img.exists())
						throw new FileNotFoundException();
					BufferedImage im = ImageIO.read(img);
					if (im != null) {
						customGlows.put(uid, "*"+img.getAbsolutePath());
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					DragonAPICore.logError("Could not load glow render entry "+s);
				}
			}
		}
	}

	private void renderAdditionalObjects(EntityPlayer ep, float ptick) {
		if (ep == Minecraft.getMinecraft().thePlayer && !DragonOptions.CUSTOMRENDER.getState())
			return;
		Collection<PlayerRenderObj> c = renders.get(ep.getUniqueID());
		if (c != null) {
			for (PlayerRenderObj r : c) {
				r.render(ep, ptick, new PlayerRotationData(ep, ptick));
			}
		}
	}

	private class PlayerModelRenderer implements PlayerRenderObj {

		private final ModifiedPlayerModel model;

		private PlayerModelRenderer(ModifiedPlayerModel m) {
			model = m;
		}

		public void render(EntityPlayer ep, float tick, PlayerRotationData dat) {
			if (ep != null) {
				GL11.glPushMatrix();
				//render.setRenderPassModel(modelReika);
				model.bindTexture();
				GL11.glTranslated(0, 1.6, 0);
				GL11.glScaled(1, -1, 1);
				if (ep.isSneaking()) {
					GL11.glRotated(22.5, 1, 0, 0);
					GL11.glTranslated(-0.02, 0.1, -0.05);
				}
				//GL11.glFrontFace(GL11.GL_CW);
				model.renderBodyParts(ep, tick);
				//GL11.glFrontFace(GL11.GL_CCW);
				GL11.glPopMatrix();
			}
		}

		@Override
		public int getRenderPriority() {
			return Integer.MIN_VALUE;
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
			if (ep.isPotionActive(Potion.invisibility))
				return;
			if (MinecraftForgeClient.getRenderPass() == 1 || MinecraftForgeClient.getRenderPass() == -1)
				PlayerSpecificRenderer.instance.renderAdditionalObjects((EntityPlayer)ep, partialTick);
		}

		@Override
		protected void renderModel(EntityLivingBase ep, float f1, float f2, float f3, float f4, float f5, float f6) {
			if (MinecraftForgeClient.getRenderPass() == 0 || MinecraftForgeClient.getRenderPass() == -1)
				super.renderModel(ep, f1, f2, f3, f4, f5, f6);
			if (ep.isPotionActive(Potion.invisibility))
				return;
			String glow = PlayerSpecificRenderer.instance.getGlow(ep.getUniqueID());
			if (glow != null) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.DEFAULT.apply();
				if (glow.charAt(0) == '*') {
					ReikaTextureHelper.bindRawTexture(glow.substring(1));
				}
				else {
					ReikaTextureHelper.bindTexture(DragonAPICore.class, glow);
				}
				this.renderWithoutTextureBind(ep, f1, f2, f3, f4, f5, f6);
				GL11.glPopAttrib();
			}
		}

		@Override
		public void renderFirstPersonArm(EntityPlayer ep)
		{
			super.renderFirstPersonArm(ep);
			String glow = PlayerSpecificRenderer.instance.getGlow(ep.getUniqueID());
			if (glow != null) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.DEFAULT.apply();
				if (glow.charAt(0) == '*') {
					ReikaTextureHelper.bindRawTexture(glow.substring(1));
				}
				else {
					ReikaTextureHelper.bindTexture(DragonAPICore.class, glow);
				}
				super.renderFirstPersonArm(ep);
				GL11.glPopAttrib();
			}
		}

		private void renderWithoutTextureBind(EntityLivingBase ep, float f1, float f2, float f3, float f4, float f5, float f6) {
			if (!ep.isInvisible()) {
				mainModel.render(ep, f1, f2, f3, f4, f5, f6);
			}
			else if (!ep.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
				GL11.glPushMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
				GL11.glDepthMask(false);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
				mainModel.render(ep, f1, f2, f3, f4, f5, f6);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				GL11.glPopMatrix();
				GL11.glDepthMask(true);
			}
			else {
				mainModel.setRotationAngles(f1, f2, f3, f4, f5, f6, ep);
			}
		}

	}

	public static class PlayerRotationData {

		public final float rotationYaw;
		public final float rotationYawHead;
		public final float rotationPitch;

		private final float prevRotationYaw;
		private final float prevRotationYawHead;
		private final float prevRotationPitch;

		private final float partialTick;

		private float renderYaw;
		private float renderYawHead;
		private float renderPitch;

		public final float interpYaw;
		public final float interpYawHead;
		public final float interpPitch;

		private PlayerRotationData(EntityPlayer ep, float ptick) {
			rotationPitch = ep.rotationPitch;
			rotationYaw = ep.rotationYaw;
			rotationYawHead = ep.rotationYawHead;
			partialTick = ptick;
			prevRotationPitch = ep.prevRotationPitch;
			prevRotationYaw = ep.prevRotationYaw;
			prevRotationYawHead = ep.prevRotationYawHead;

			renderPitch = -ep.rotationPitch;
			renderYawHead = -ep.rotationYaw%360-partialTick*(ep.rotationYaw-ep.prevRotationYaw);
			renderYaw = -ep.renderYawOffset%360-partialTick*(ep.renderYawOffset-ep.prevRenderYawOffset)+180;

			interpYaw = prevRotationYaw+(rotationYaw-prevRotationYaw)*partialTick;
			interpYawHead = prevRotationYawHead+(rotationYawHead-prevRotationYawHead)*partialTick;
			interpPitch = prevRotationPitch+(rotationPitch-prevRotationPitch)*partialTick;

			this.compensateAngles();
		}

		/** Compensates for in-inventory rendering */
		private void compensateAngles() {
			//ReikaJavaLibrary.pConsole(yc/RADIAN);
			if (partialTick == 1.0F) {

				int ySize = 136;
				int xSize = 195;

				Minecraft mc = Minecraft.getMinecraft();
				ScaledResolution scr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

				int width = scr.getScaledWidth();
				int height = scr.getScaledHeight();

				int guiLeft = (width - xSize) / 2;
				int guiTop = (height - ySize) / 2;

				float par1 = Mouse.getX() * width / mc.displayWidth;
				float par2 = height - Mouse.getY() * height / mc.displayHeight - 1;

				float par3 = guiLeft + 43 - par1;
				float par4 = guiTop + 45 - 30 - par2;

				renderYaw = -(float)Math.atan(par3 / 40.0F) * 20.0F;
				renderYawHead = -(float)Math.atan(par3 / 40.0F) * 40.0F;
				renderPitch = -((float)Math.atan(par4 / 40.0F)) * 20.0F;

				renderYawHead += 180;
				renderYaw += 180;
				//renderPitch = -90;
			}
		}

		public float getRenderYaw() {
			return renderYaw;
		}

		public float getRenderYawHead() {
			return renderYawHead;
		}

		public float getRenderPitch() {
			return renderPitch;
		}

	}

	private static class RenderComparator implements Comparator<PlayerRenderObj> {

		@Override
		public int compare(PlayerRenderObj o1, PlayerRenderObj o2) {
			int p1 = o1.getRenderPriority();
			int p2 = o2.getRenderPriority();
			return p1 < p2 ? -1 : p1 > p2 ? 1 : 0;
		}

	}

	public String getGlow(UUID uid) {
		String s = glows.get(uid);
		if (s == null)
			s = customGlows.get(uid);
		return s;
	}

}
