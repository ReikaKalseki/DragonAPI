package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderTypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShaderProgram {

	public final DragonAPIMod owner;
	private final Class reference;
	private final String pathPrefix;

	public final String identifier;

	public final int programID;

	int vertexID;
	int fragmentID;

	private ShaderHook hook;

	ShaderProgram(DragonAPIMod mod, Class c, String p, String s, int id) {
		identifier = s;
		reference = c;
		pathPrefix = p;
		owner = mod;
		programID = id;
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

	public void setField(String field, int value) {
		int loc = ARBShaderObjects.glGetUniformLocationARB(programID, field);
		ARBShaderObjects.glUniform1iARB(loc, value);
	}

	public void setField(String field, float value) {
		int loc = ARBShaderObjects.glGetUniformLocationARB(programID, field);
		ARBShaderObjects.glUniform1fARB(loc, value);
	}

	void run() {
		ARBShaderObjects.glUseProgramObjectARB(programID);
		this.setField("time", Minecraft.getMinecraft().thePlayer.ticksExisted);
		if (hook != null)
			hook.onRender(this);
	}

	void register() {
		ARBShaderObjects.glAttachObjectARB(programID, vertexID);
		ARBShaderObjects.glAttachObjectARB(programID, fragmentID);
		ARBShaderObjects.glLinkProgramARB(programID);
		if (ARBShaderObjects.glGetObjectParameteriARB(programID, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RegistrationException(owner, "Shader was not linked properly!");
		}
		ARBShaderObjects.glValidateProgramARB(programID);
		if (ARBShaderObjects.glGetObjectParameteriARB(programID, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RegistrationException(owner, "Shader failed to validate!");
		}
	}

	@Override
	public String toString() {
		return identifier+" #"+programID+" ["+vertexID+"/"+fragmentID+"]";
	}

}
