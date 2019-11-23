package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShaderRegistry {

	private ShaderRegistry() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	private static final HashMap<Integer, ShaderProgram> shaders = new HashMap();
	private static final HashMap<String, ShaderProgram> shaderIDs = new HashMap();
	private static final EnumMap<ShaderDomain, ArrayList<ShaderProgram>> shaderSets = new EnumMap(ShaderDomain.class);

	public static ShaderProgram createShader(DragonAPIMod mod, String id, Class root, String pathPre, ShaderDomain dom) {
		if (!OpenGlHelper.shadersSupported)
			return null;
		if (shaderIDs.containsKey(id))
			throw new RegistrationException(mod, "Shader id "+id+" is already in use!");
		int prog = ARBShaderObjects.glCreateProgramObjectARB();
		if (prog == 0) {
			throw new RegistrationException(mod, "Shader program could not be assigned an ID!");
		}
		ShaderProgram sh = new ShaderProgram(mod, root, pathPre, id, prog, dom);
		try {
			sh.load();
		}
		catch (IOException e) {
			throw new RegistrationException(mod, "Shader program data could not be loaded!", e);
		}
		shaders.put(sh.programID, sh);
		shaderIDs.put(sh.identifier, sh);
		addShaderToSet(dom, sh);
		DragonAPICore.log("Registered "+mod.getTechnicalName()+" shader "+sh);
		return sh;
	}

	private static void addShaderToSet(ShaderDomain dom, ShaderProgram s) {
		ArrayList<ShaderProgram> li = shaderSets.get(dom);
		if (li == null) {
			li = new ArrayList();
			shaderSets.put(dom, li);
		}
		li.add(s);
		Collections.sort(li);
	}

	private static void removeShaderFromSet(ShaderDomain dom, ShaderProgram s) {
		ArrayList<ShaderProgram> li = shaderSets.get(dom);
		if (li != null) {
			li.remove(s);
		}
	}

	public static void removeShader(String id) {
		removeShader(shaderIDs.get(id));
	}

	public static void removeShader(ShaderProgram s) {
		shaderIDs.remove(s.identifier);
		shaders.remove(s.programID);
		removeShaderFromSet(s.domain, s);
	}

	public static void reloadShader(String id) throws IOException {
		shaderIDs.get(id).load();
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
		if (GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown() && Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			try {
				sh.load();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			throw new RegistrationException(mod, "Shader was not able to be constructed: "+ShaderRegistry.parseError(id));

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

	public static void runGlobalShaders(Framebuffer fb, int w, int h) {
		ArrayList<ShaderProgram> li = shaderSets.get(ShaderDomain.GLOBAL);
		if (li != null) {
			for (ShaderProgram s : li) {
				ReikaRenderHelper.renderFrameBufferToItself(fb, w, h, s);
			}
		}
	}

	public static String parseError(int programID) {
		return ARBShaderObjects.glGetInfoLogARB(programID, ARBShaderObjects.glGetObjectParameteriARB(programID, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	public static enum ShaderDomain {
		WORLD,
		TESR,
		GUI,
		GLOBAL;
	}

	public static enum ShaderTypes {
		FRAGMENT(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB, "frag"),
		VERTEX(ARBVertexShader.GL_VERTEX_SHADER_ARB, "vert");

		public final int glValue;
		public final String extension;

		private ShaderTypes(int id, String s) {
			glValue = id;
			extension = s;
		}
	}

}
