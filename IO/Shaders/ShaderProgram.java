package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderTypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShaderProgram implements Comparable<ShaderProgram> {

	public final DragonAPIMod owner;
	private final Class reference;
	private final String pathPrefix;

	public final String identifier;
	public final ShaderDomain domain;
	public final int programID;

	int vertexID;
	int fragmentID;

	private ShaderHook hook;
	private int ordering;

	private boolean isEnabled = true;

	private final HashMap<String, Object> variables = new HashMap();

	ShaderProgram(DragonAPIMod mod, Class c, String p, String s, int id, ShaderDomain dom) {
		identifier = s;
		reference = c;
		pathPrefix = p;
		owner = mod;
		programID = id;
		domain = dom;
	}

	public void load() throws IOException {
		if (vertexID != 0)
			ARBShaderObjects.glDeleteObjectARB(vertexID);
		if (fragmentID != 0)
			ARBShaderObjects.glDeleteObjectARB(fragmentID);
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

	public void setField(String field, Object value) {
		variables.put(field, value);
	}

	void run() {
		if (!isEnabled)
			return;
		if (Minecraft.getMinecraft().thePlayer == null)
			return;
		ARBShaderObjects.glUseProgramObjectARB(programID);
		this.applyVariables();
		this.setField("time", Minecraft.getMinecraft().thePlayer.ticksExisted);
		if (hook != null)
			hook.onRender(this);
	}

	private void applyVariables() {
		for (Entry<String, Object> e : variables.entrySet()) {
			int loc = ARBShaderObjects.glGetUniformLocationARB(programID, e.getKey());
			Object val = e.getValue();
			if (val instanceof Integer) {
				ARBShaderObjects.glUniform1iARB(loc, (int)val);
			}
			else if (val instanceof Float || val instanceof Double) {
				ARBShaderObjects.glUniform1fARB(loc, (float)val);
			}
		}
		//variables.clear();
	}

	void register() {
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
