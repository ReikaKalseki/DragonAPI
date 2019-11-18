package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderTypes;

public class ShaderProgram {

	public final DragonAPIMod owner;

	public final String identifier;

	public final int programID;

	int vertexID;
	int fragmentID;

	private ShaderHook hook;

	ShaderProgram(DragonAPIMod mod, String s, int id) {
		identifier = s;
		owner = mod;
		programID = id;
	}

	public void load(InputStream fv, InputStream ff) throws IOException {
		vertexID = ShaderRegistry.constructShader(owner, fv, ShaderTypes.VERTEX);
		fragmentID = ShaderRegistry.constructShader(owner, ff, ShaderTypes.FRAGMENT);
		this.register();
	}

	public ShaderProgram setHook(ShaderHook h) {
		hook = h;
		return this;
	}

	public void setField(String field, float value) {
		int loc = ARBShaderObjects.glGetUniformLocationARB(programID, field);
		ARBShaderObjects.glUniform1fARB(loc, value);
	}

	void run() {
		this.setField("time", Minecraft.getMinecraft().thePlayer.ticksExisted);
		ARBShaderObjects.glUseProgramObjectARB(programID);
		if (hook != null)
			hook.onRender(this);
	}

	void register() {
		ARBShaderObjects.glDeleteObjectARB(vertexID);
		ARBShaderObjects.glDeleteObjectARB(fragmentID);
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
