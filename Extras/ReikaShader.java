package Reika.DragonAPI.Extras;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.IO.Shaders.ShaderHook;
import Reika.DragonAPI.IO.Shaders.ShaderProgram;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class ReikaShader implements ShaderHook {

	private static final int LENGTH = 20;

	public static final ReikaShader instance = new ReikaShader();

	private ShaderProgram shader;
	private final ArrayList<DecimalPosition> positions = new ArrayList();
	private long lastUpdateTick;

	private ReikaShader() {

	}

	public void register() {
		shader = ShaderRegistry.createShader(DragonAPIInit.instance, "reika", DragonAPICore.class, "Resources/", ShaderDomain.GLOBALNOGUI).setEnabled(false);
	}

	public void updatePosition(EntityPlayer ep) {
		DecimalPosition pos = new DecimalPosition(ep, ReikaRenderHelper.getPartialTickTime());
		if (!positions.isEmpty() && positions.get(0).equals(pos))
			return;
		long time = ep.worldObj.getTotalWorldTime();
		if (time-lastUpdateTick > 50) {
			positions.clear();
		}
		lastUpdateTick = time;
		positions.add(0, pos);
		if (positions.size() > LENGTH) {
			positions.remove(positions.size()-1);
		}
	}

	public void render(EntityPlayer ep) {
		if (ep == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
			return;
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0.8, 0);
		this.updatePosition(ep);
		shader.setEnabled(!positions.isEmpty());
		double dist = ep.getDistanceSq(RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ);
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView > 0) {
			dist += ReikaRenderHelper.thirdPersonDistance;
		}
		HashMap<String, Object> map = new HashMap();
		map.put("distance", dist);
		map.put("colorIntensity", 0.5F);
		map.put("distortionIntensity", 1F);
		for (int i = 0; i < positions.size(); i++) {
			GL11.glPushMatrix();
			float f = 1F-i/(float)LENGTH;
			DecimalPosition p = positions.get(i);
			GL11.glTranslated(p.xCoord-ep.posX, p.yCoord-ep.posY, p.zCoord-ep.posZ);
			shader.addFocus(p.xCoord, p.yCoord, p.zCoord);
			shader.modifyLastCompoundFocus(f*0.05F, map);
			GL11.glPopMatrix();
		}
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

}
