package Reika.DragonAPI.IO.Shaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Throwables;

import net.minecraft.client.renderer.OpenGlHelper;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;

public class ShaderRegistry {

	private ShaderRegistry() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	private static final HashMap<Integer, ShaderProgram> shaders = new HashMap();
	private static final HashMap<String, ShaderProgram> shaderIDs = new HashMap();

	public static ShaderProgram createShader(DragonAPIMod mod, String id, Class root, String pv, String pf) {
		return createShader(mod, id, root.getResourceAsStream(pv), root.getResourceAsStream(pf));
	}

	public static ShaderProgram createShader(DragonAPIMod mod, String id, File fv, File ff) throws FileNotFoundException {
		return createShader(mod, id, new FileInputStream(fv), new FileInputStream(ff));
	}

	public static ShaderProgram createShader(DragonAPIMod mod, String id, InputStream fv, InputStream ff) {
		if (!OpenGlHelper.shadersSupported)
			return null;
		if (shaderIDs.containsKey(id))
			throw new RegistrationException(mod, "Shader id "+id+" is already in use!");
		int vert = 0;
		int frag = 0;
		try {
			vert = constructShader(mod, fv, ShaderTypes.VERTEX);
			frag = constructShader(mod, ff, ShaderTypes.FRAGMENT);
			int prog = ARBShaderObjects.glCreateProgramObjectARB();
			if (prog == 0) {
				throw new RegistrationException(mod, "Shader program could not be assigned an ID!");
			}
			ShaderProgram sh = new ShaderProgram(mod, id, prog);
			sh.load(fv, ff);
			shaders.put(sh.programID, sh);
			shaderIDs.put(sh.identifier, sh);
			DragonAPICore.log("Registered "+mod.getTechnicalName()+" shader "+sh);
			return sh;
		}
		catch (Exception e) {
			ARBShaderObjects.glDeleteObjectARB(vert);
			ARBShaderObjects.glDeleteObjectARB(frag);
			throw Throwables.propagate(e);
		}
	}

	public void removeShader(String id) {
		this.removeShader(shaderIDs.get(id));
	}

	public void removeShader(ShaderProgram s) {
		shaderIDs.remove(s.identifier);
		shaders.remove(s.programID);
	}

	public static void reloadShader(String id, InputStream fv, InputStream ff) throws IOException {
		shaderIDs.get(id).load(fv, ff);
	}

	public static void runShader(int id) {
		runShader(shaders.get(id));
	}

	public static void runShader(String id) {
		runShader(shaderIDs.get(id));
	}

	public static void runShader(ShaderProgram sh) {
		if (!OpenGlHelper.shadersSupported || sh == null)
			return;
		sh.run();
	}

	public static void completeShader() {
		if (!OpenGlHelper.shadersSupported)
			return;
		ARBShaderObjects.glUseProgramObjectARB(0);
	}

	static int constructShader(DragonAPIMod mod, InputStream data, ShaderTypes type) throws IOException {
		if (data == null)
			throw new RegistrationException(mod, "Shader has null program data!");
		int id = ARBShaderObjects.glCreateShaderObjectARB(type.glValue);

		if (id == 0)
			throw new RegistrationException(mod, "Shader was not able to be assigned an ID!");

		ARBShaderObjects.glShaderSourceARB(id, readData(data));
		ARBShaderObjects.glCompileShaderARB(id);

		if (ARBShaderObjects.glGetObjectParameteriARB(id, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
			throw new RegistrationException(mod, "Shader was not able to be constructed!");

		return id;
	}

	private static String readData(InputStream data) {
		StringBuilder sb = new StringBuilder();
		ArrayList<String> li = ReikaFileReader.getFileAsLines(data, true);
		for (String s : li) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	public static enum ShaderTypes {
		FRAGMENT(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB),
		VERTEX(ARBVertexShader.GL_VERTEX_SHADER_ARB);

		public final int glValue;

		private ShaderTypes(int id) {
			glValue = id;
		}
	}

}
