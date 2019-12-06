package Reika.DragonAPI.Extras;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.IO.Shaders.ShaderHook;
import Reika.DragonAPI.IO.Shaders.ShaderProgram;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ReikaShader implements ShaderHook, TickHandler {

	public static final ReikaShader instance = new ReikaShader();

	private ShaderProgram shader;
	private final ArrayList<ShaderPoint> points = new ArrayList();
	private boolean rendering;

	private ReikaShader() {

	}

	public void register() {
		shader = ShaderRegistry.createShader(DragonAPIInit.instance, "reika", DragonAPICore.class, "Resources/", ShaderDomain.GLOBALNOGUI).setEnabled(false);
		TickRegistry.instance.registerTickHandler(this);
	}

	public void updatePosition(Entity ep) {
		if (rendering)
			return;
		if (ep == Minecraft.getMinecraft().thePlayer)
			return;
		boolean flag = true;
		long time = ep.worldObj.getTotalWorldTime();
		Iterator<ShaderPoint> it = points.iterator();
		while (it.hasNext()) {
			ShaderPoint p = it.next();
			if (p.position.equals(ep.posX, ep.posY, ep.posZ)) {
				p.refresh(time);
				flag = false;
			}
			if (p.tick(time)) {
				it.remove();
			}
		}
		if (flag) {
			ShaderPoint p = new ShaderPoint(ep);
			points.add(0, p);
		}
	}

	public void render(Entity ep) {
		Minecraft mc = Minecraft.getMinecraft();
		if (ep == mc.thePlayer)
			return;
		if (points.isEmpty())
			return;
		GL11.glPushMatrix();
		double dist = ep.getDistanceSqToEntity(mc.thePlayer);
		if (mc.gameSettings.thirdPersonView > 0) {
			dist += ReikaRenderHelper.thirdPersonDistance;
		}
		float ptick = ReikaRenderHelper.getPartialTickTime();
		double px = ep.posX;//ep.lastTickPosX+(ep.posX-ep.lastTickPosX)*ptick;
		double py = ep.posY;//ep.lastTickPosY+(ep.posY-ep.lastTickPosY)*ptick;
		double pz = ep.posZ;//ep.lastTickPosZ+(ep.posZ-ep.lastTickPosZ)*ptick;

		//px += ep.posX-mc.thePlayer.posX;
		//py += ep.posY-mc.thePlayer.posY;
		//pz += ep.posZ-mc.thePlayer.posZ;

		GL11.glTranslated(RenderManager.renderPosX-ep.posX, RenderManager.renderPosY-ep.posY, RenderManager.renderPosZ-ep.posZ);
		GL11.glTranslated(0, -0.9, 0);
		GL11.glRotated(180, 0, 1, 0);
		shader.setEnabled(true);
		HashMap<String, Object> map = new HashMap();
		map.put("distance", dist);
		map.put("colorIntensity", 0.25F);
		map.put("distortionIntensity", 1F);
		rendering = true;
		for (ShaderPoint pt : points) {
			float f = pt.getIntensity();
			if (f > 0) {
				GL11.glPushMatrix();
				DecimalPosition p = pt.position;
				GL11.glTranslated(p.xCoord-px, p.yCoord-py, p.zCoord-pz);
				shader.addFocus(p.xCoord, p.yCoord, p.zCoord);
				shader.addFocus(ep);
				shader.modifyLastCompoundFocus(f, map);
				GL11.glPopMatrix();
			}
		}
		rendering = false;
		GL11.glPopMatrix();
	}

	@Override
	public void onPreRender(ShaderProgram s) {

	}

	@Override
	public void onPostRender(ShaderProgram s) {
		if (!shader.hasOngoingFoci()) {
			shader.setEnabled(false);
			shader.clearFoci();
		}
	}

	@Override
	public void updateEnabled(ShaderProgram s) {

	}

	private static class ShaderPoint {

		private static final int LIFESPAN = 30;

		private final DecimalPosition position;

		private long creation;
		private int age;

		private ShaderPoint(Entity ep) {
			position = new DecimalPosition(ep);
			creation = ep.worldObj.getTotalWorldTime();
		}

		public void refresh(long world) {
			age = 0;
			creation = world;
		}

		public boolean tick(long world) {
			age++;
			long val = Math.max(age, world-creation);
			return val >= LIFESPAN;
		}

		public float getIntensity() {
			if (age < 5) {
				return 0;
			}
			else if (age < 10) {
				return (age-5)/5F;
			}
			return 1F-(age-10)/(float)(LIFESPAN-10);
		}

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		EntityPlayer ep = (EntityPlayer)tickData[0];
		if (ep.worldObj.isRemote && ReikaPlayerAPI.isReika(ep)) {
			this.updatePosition(ep);
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "reikashader";
	}

}
