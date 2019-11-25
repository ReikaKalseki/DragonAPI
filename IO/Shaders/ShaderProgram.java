package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderTypes;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ShaderProgram implements Comparable<ShaderProgram> {

	public final DragonAPIMod owner;
	private final Class reference;
	private final String pathPrefix;

	public final String identifier;
	public final ShaderDomain domain;

	private int vertexID;
	private int fragmentID;
	private int programID;

	private ShaderHook hook;
	private int ordering;

	private boolean isEnabled = true;

	private final HashMap<String, Object> variables = new HashMap();
	private Matrix4f modelview;
	private Matrix4f projection;
	private Vector3f focusLocation;

	ShaderProgram(DragonAPIMod mod, Class c, String p, String s, ShaderDomain dom) {
		identifier = s;
		reference = c;
		pathPrefix = p;
		owner = mod;
		domain = dom;
	}

	public void load() throws IOException {
		if (vertexID != 0)
			ARBShaderObjects.glDeleteObjectARB(vertexID);
		if (fragmentID != 0)
			ARBShaderObjects.glDeleteObjectARB(fragmentID);
		if (programID != 0) {
			ARBShaderObjects.glDeleteObjectARB(programID);
		}
		vertexID = ShaderRegistry.constructShader(owner, this.getShaderData(ShaderTypes.VERTEX), ShaderTypes.VERTEX);
		fragmentID = ShaderRegistry.constructShader(owner, this.getShaderData(ShaderTypes.FRAGMENT), ShaderTypes.FRAGMENT);
		this.register();
	}

	private InputStream getShaderData(ShaderTypes s) {
		return reference.getResourceAsStream(pathPrefix+identifier+"."+s.extension);
	}

	public ShaderProgram setHook(ShaderHook h) {
		hook = h;
		return this;
	}

	public ShaderProgram setOrdering(int o) {
		ordering = o;
		return this;
	}

	public ShaderProgram setEnabled(boolean on) {
		isEnabled = on;
		return this;
	}

	public ShaderProgram setMatricesToCurrent() {
		return this.setMatrices(ReikaRenderHelper.getModelviewMatrix(), ReikaRenderHelper.getProjectionMatrix());
	}

	public ShaderProgram setMatrices(Matrix4f model, Matrix4f proj) {
		modelview = model;
		projection = proj;
		return this;
	}

	public ShaderProgram setFocus(Coordinate e) {
		return this.setFocus(e.xCoord+0.5, e.yCoord+0.5, e.zCoord+0.5);
	}

	public ShaderProgram setFocus(TileEntity e) {
		return this.setFocus(e.xCoord+0.5, e.yCoord+0.5, e.zCoord+0.5);
	}

	public ShaderProgram setFocus(Entity e) {
		return this.setFocus(e.posX, e.posY, e.posZ);
	}

	public ShaderProgram setFocus(double x, double y, double z) {
		return this.setFocus(new Vector3f((float)x, (float)y, (float)z));
	}

	public ShaderProgram setFocus(Vector3f focus) {
		focusLocation = focus;
		return this;
	}

	public void setField(String field, Object value) {
		variables.put(field, value);
	}

	public void setIntensity(float f) {
		this.setField("intensity", f);
	}

	void run() {
		if (!isEnabled)
			return;
		if (Minecraft.getMinecraft().thePlayer == null)
			return;
		ARBShaderObjects.glUseProgramObjectARB(programID);
		if (modelview != null && projection != null)
			this.applyMatrices();
		this.applyVariables();
		this.applyField("time", Minecraft.getMinecraft().thePlayer.ticksExisted);
		this.applyField("screenWidth", Minecraft.getMinecraft().displayWidth);
		this.applyField("screenHeight", Minecraft.getMinecraft().displayHeight);

		if (focusLocation != null && modelview != null && projection != null) {
			Vector4f target = new Vector4f(focusLocation.x*0, focusLocation.y*0, focusLocation.z*0, 1.0F);
			Vector4f clipspace = Matrix4f.transform(modelview, target, null);
			clipspace = Matrix4f.transform(projection, clipspace, null);
			Vector3f ndcspace = new Vector3f(clipspace.x/clipspace.w, clipspace.y/clipspace.w, clipspace.z/clipspace.w);
			Vector2f screenXY = new Vector2f(ndcspace.x/2 + 0.5F, ndcspace.y/2 + 0.5F);
			screenXY.x *= Minecraft.getMinecraft().displayWidth;
			screenXY.y *= Minecraft.getMinecraft().displayHeight;
			this.applyField("screenX", screenXY.x);
			this.applyField("screenY", screenXY.y);
		}

		if (hook != null)
			hook.onRender(this);
	}

	private void applyVariables() {
		for (Entry<String, Object> e : variables.entrySet()) {
			this.applyField(e.getKey(), e.getValue());
		}
		//variables.clear();
	}

	private void applyMatrices() {
		FloatBuffer b1 = BufferUtils.createFloatBuffer(16);
		modelview.store(b1);
		FloatBuffer b2 = BufferUtils.createFloatBuffer(16);
		projection.store(b2);
		int loc = ARBShaderObjects.glGetUniformLocationARB(programID, "modelview");
		ARBShaderObjects.glUniformMatrix4ARB(loc, false, b1);
		loc = ARBShaderObjects.glGetUniformLocationARB(programID, "projection");
		ARBShaderObjects.glUniformMatrix4ARB(loc, false, b2);
	}

	private void applyField(String f, Object val) {
		int loc = ARBShaderObjects.glGetUniformLocationARB(programID, f);
		if (val instanceof Integer) {
			ARBShaderObjects.glUniform1iARB(loc, (int)val);
		}
		else if (val instanceof Float || val instanceof Double) {
			ARBShaderObjects.glUniform1fARB(loc, (float)val);
		}
	}

	void register() {
		programID = ARBShaderObjects.glCreateProgramObjectARB();
		if (programID == 0) {
			throw new RegistrationException(owner, "Shader program could not be assigned an ID!");
		}
		ARBShaderObjects.glAttachObjectARB(programID, vertexID);
		ARBShaderObjects.glAttachObjectARB(programID, fragmentID);
		ARBShaderObjects.glLinkProgramARB(programID);
		if (ARBShaderObjects.glGetObjectParameteriARB(programID, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RegistrationException(owner, "Shader was not linked properly: "+ShaderRegistry.parseError(programID));
		}
		ARBShaderObjects.glValidateProgramARB(programID);
		if (ARBShaderObjects.glGetObjectParameteriARB(programID, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RegistrationException(owner, "Shader failed to validate: "+ShaderRegistry.parseError(programID));
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public String toString() {
		return identifier+" #"+programID+" ["+vertexID+"/"+fragmentID+"]";
	}

	@Override
	public int compareTo(ShaderProgram o) {
		return Integer.compare(ordering, o.ordering);
	}

}
