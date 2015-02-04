package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.Collection;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Extras.ModifiedPlayerModel;
import Reika.DragonAPI.Extras.ReikaModel;
import Reika.DragonAPI.Extras.SamakiModel;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.PlayerRenderObj;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerSpecificRenderer {

	public static final PlayerSpecificRenderer instance = new PlayerSpecificRenderer();

	private final MultiMap<String, PlayerRenderObj> renders = new MultiMap().setNullEmpty();

	private final ReikaModel modelReika = new ReikaModel();
	private final SamakiModel modelSamaki = new SamakiModel();

	private PlayerSpecificRenderer() {
		this.registerRenderer("Reika_Kalseki", new PlayerModelRenderer(modelReika));
		this.registerRenderer("FurryDJ", new PlayerModelRenderer(modelSamaki));
	}

	public void registerIntercept() {
		Map<Class, Render> map = RenderManager.instance.entityRenderMap;
		map.put(EntityPlayer.class, new CustomPlayerRenderer(map.get(EntityPlayer.class)));
	}

	public void registerRenderer(String name, PlayerRenderObj r) {
		renders.addValue(name, r);

		//If anyone flips out over this and complains "OMG REIKA GIVES HIMSELF ALL THE RENDERS IN THE DEV ENVIRONMENT! DRM!",
		//You are:
		//If you cannot understand Java: making wild accusations based on your own ignorance
		//If you can understand Java: A disgrace to other programmers for harassing a developer over what you should understand is harmless
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			renders.addValue("Reika_Kalseki", r);
		}
	}

	private void renderAdditionalObjects(EntityPlayer ep, float ptick) {
		Collection<PlayerRenderObj> c = renders.get(ep.getCommandSenderName());
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
			PlayerSpecificRenderer.instance.renderAdditionalObjects((EntityPlayer)ep, partialTick);
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

}
