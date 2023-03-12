package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderDomain;
import Reika.DragonAPI.IO.Shaders.ShaderRegistry.ShaderTypes;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Math.Vec4;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

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
	private boolean errored = false;
	private boolean errorChecked = false;

	private boolean isEnabled = true;

	private final MultiMap<ShaderTypes, ShaderLibrary> imports = new MultiMap();

	private final HashMap<String, Object> variables = new HashMap();
	private Matrix4f modelview;
	private Matrix4f projection;
	private Vector3f focusLocation;

	private ArrayList<RenderState> compoundLocation = null;

	ShaderProgram(DragonAPIMod mod, Class c, String p, String s, ShaderDomain dom) {
		identifier = s;
		reference = c;
		pathPrefix = p;
		owner = mod;
		domain = dom;
	}

	public void load() throws IOException {
		errored = false;
		errorChecked = false;
		if (vertexID != 0)
			GL20.glDeleteProgram(vertexID);
		if (fragmentID != 0)
			GL20.glDeleteProgram(fragmentID);
		if (programID != 0) {
			GL20.glDeleteProgram(programID);
		}
		try (InputStream vin = this.getShaderData(ShaderTypes.VERTEX); InputStream fin = this.getShaderData(ShaderTypes.FRAGMENT)) {
			Collection<ShaderLibrary> libs = new ArrayList();
			vertexID = ShaderRegistry.constructShader(owner, identifier, vin, ShaderTypes.VERTEX, libs);
			imports.addValues(ShaderTypes.VERTEX, libs);
			libs = new ArrayList();
			fragmentID = ShaderRegistry.constructShader(owner, identifier, fin, ShaderTypes.FRAGMENT, libs);
			imports.addValues(ShaderTypes.FRAGMENT, libs);
		}
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

	public Collection<ShaderLibrary> getLibraries() {
		return Collections.unmodifiableCollection(imports.allValues(false));
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

	public ShaderProgram clearFoci() {
		compoundLocation = null;
		return this;
	}

	public ShaderProgram addFocus(Coordinate e) {
		return this.addFocus(e.xCoord+0.5, e.yCoord+0.5, e.zCoord+0.5);
	}

	public ShaderProgram addFocus(TileEntity e) {
		return this.addFocus(e.xCoord+0.5, e.yCoord+0.5, e.zCoord+0.5);
	}

	public ShaderProgram addFocus(Entity e) {
		return this.addFocus(e.posX, e.posY, e.posZ);
	}

	public ShaderProgram addFocus(double x, double y, double z) {
		return this.addFocus(x, y, z, null, null);
	}

	public ShaderProgram addFocus(double x, double y, double z, Matrix4f model, Matrix4f proj) {
		if (compoundLocation == null)
			compoundLocation = new ArrayList();
		if (model == null)
			model = ReikaRenderHelper.getModelviewMatrix();
		if (proj == null)
			proj = ReikaRenderHelper.getProjectionMatrix();
		compoundLocation.add(new RenderState(new Vector3f((float)x, (float)y, (float)z), model, proj));
		return this;
	}

	public ShaderProgram modifyLastCompoundFocus(float intensity, HashMap<String, Object> vars) {
		RenderState rs = compoundLocation.get(compoundLocation.size()-1);
		rs.intensity = intensity;
		rs.variables = vars;
		return this;
	}

	public void clearData() {
		for (Entry<String, Object> e : variables.entrySet()) {
			e.setValue(e.getValue() instanceof Integer ? 0 : 0F);
		}
	}

	public void setFields(Map<String, Object> data) {
		variables.putAll(data);
	}

	public void setField(String field, Object value) {
		variables.put(field, value);
	}

	public void setIntensity(float f) {
		this.setField("intensity", f);
	}

	public void setTextureUnit(String field, int constant) {
		this.setField(field, constant-OpenGlHelper.defaultTexUnit);
	}

	boolean run() {
		if (!this.isEnabled() || errored)
			return false;

		if (hook != null)
			hook.onPreRender(this);

		GL20.glUseProgram(programID);
		boolean flag = false;
		if (compoundLocation != null) {
			RenderState rs = compoundLocation.remove(0);
			focusLocation = rs.position;
			//ReikaJavaLibrary.pConsole(focusLocation);
			modelview = rs.modelview;
			projection = rs.projection;
			this.setIntensity(rs.intensity);
			if (rs.variables != null) {
				for (Entry<String, Object> e : rs.variables.entrySet()) {
					this.setField(e.getKey(), e.getValue());
				}
			}
			if (compoundLocation.isEmpty())
				compoundLocation = null;
			else
				flag = true;
		}
		if (focusLocation != null) {
			this.applyFocus();
		}
		if (modelview != null && projection != null)
			this.applyMatrices();
		this.applyVariables();
		this.applyField("time", Minecraft.getMinecraft().thePlayer.ticksExisted);
		this.applyField("screenWidth", Minecraft.getMinecraft().displayWidth);
		this.applyField("screenHeight", Minecraft.getMinecraft().displayHeight);

		/*
		if (focusLocation != null && modelview != null && projection != null) {
			Vector4f target = new Vector4f(focusLocation.x*0, focusLocation.y*0, focusLocation.z*0, 1.0F);
			Vector4f clipspace = Matrix4f.transform(modelview, target, null);
			clipspace = Matrix4f.transform(projection, clipspace, null);
			Vector3f ndcspace = new Vector3f(clipspace.x/clipspace.w, clipspace.y/clipspace.w, clipspace.z/clipspace.w); //should be [-1,+1]
			Vector2f screenXY = new Vector2f(ndcspace.x/2 + 0.5F, ndcspace.y/2 + 0.5F);
			this.applyField("screenX", screenXY.x);
			this.applyField("screenY", screenXY.y);
		}
		 */

		if (hook != null)
			hook.onPostRender(this);

		return flag;
	}

	void checkForError() {
		if (!errorChecked) {
			errorChecked = true;
			int res = GL11.glGetError();
			String data = "";
			while (res != GL11.GL_NO_ERROR) {
				data = data+","+Util.translateGLErrorString(res);
				res = GL11.glGetError();
			}
			if (!data.isEmpty())
				ShaderRegistry.error(owner, identifier, "Shader "+this+" threw errors: "+data+"!", null);
		}
	}

	public boolean needsErrorChecking() {
		return !errorChecked;
	}

	void markErrored() {
		errored = true;
	}

	private void applyVariables() {
		for (Entry<String, Object> e : variables.entrySet()) {
			this.applyField(e.getKey(), e.getValue());
		}
		//variables.clear();
	}

	private void applyFocus() {
		FloatBuffer b = BufferUtils.createFloatBuffer(3);
		focusLocation.store(b);
		b.rewind();
		int loc = GL20.glGetUniformLocation(programID, "focus");
		GL20.glUniform3(loc, b);
	}

	private void applyMatrices() {
		FloatBuffer b1 = BufferUtils.createFloatBuffer(16);
		modelview.store(b1);
		b1.rewind();
		FloatBuffer b2 = BufferUtils.createFloatBuffer(16);
		projection.store(b2);
		b2.rewind();
		int loc = GL20.glGetUniformLocation(programID, "modelview");
		GL20.glUniformMatrix4(loc, false, b1);
		loc = GL20.glGetUniformLocation(programID, "projection");
		GL20.glUniformMatrix4(loc, false, b2);
	}

	private void applyField(String f, Object val) {
		int loc = GL20.glGetUniformLocation(programID, f);
		if (val instanceof Integer) {
			GL20.glUniform1i(loc, (int)val);
		}
		else if (val instanceof Float) {
			GL20.glUniform1f(loc, (float)val);
		}
		else if (val instanceof Double) {
			GL20.glUniform1f(loc, ((Double)val).floatValue());
		}
		else if (val instanceof Vec3) {
			Vec3 vec = (Vec3)val;
			GL20.glUniform3f(loc, (float)vec.xCoord, (float)vec.yCoord, (float)vec.zCoord);
		}
		else if (val instanceof Vec4) {
			Vec4 vec = (Vec4)val;
			GL20.glUniform4f(loc, vec.a, vec.b, vec.c, vec.d);
		}
		else if (val instanceof int[]) {
			int[] data = (int[])val;
			IntBuffer buf = BufferUtils.createIntBuffer(data.length);
			buf.put(data);
			buf.rewind();
			GL20.glUniform1(loc, buf);
		}
		else if (val instanceof float[]) {
			float[] data = (float[])val;
			FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
			buf.put(data);
			buf.rewind();
			GL20.glUniform1(loc, buf);
		}
		else if (val instanceof Vec3[]) {
			Vec3[] data = (Vec3[])val;
			FloatBuffer buf = BufferUtils.createFloatBuffer(data.length*3);
			for (Vec3 vec : data) {
				buf.put(vec == null ? 0 : (float)vec.xCoord);
				buf.put(vec == null ? 0 : (float)vec.yCoord);
				buf.put(vec == null ? 0 : (float)vec.zCoord);
			}
			buf.rewind();
			GL20.glUniform3(loc, buf);
		}
		else if (val instanceof Vec4[]) {
			Vec4[] data = (Vec4[])val;
			FloatBuffer buf = BufferUtils.createFloatBuffer(data.length*4);
			for (Vec4 vec : data) {
				buf.put(vec == null ? 0 : vec.a);
				buf.put(vec == null ? 0 : vec.b);
				buf.put(vec == null ? 0 : vec.c);
				buf.put(vec == null ? 0 : vec.d);
			}
			buf.rewind();
			GL20.glUniform4(loc, buf);
		}
	}

	public boolean hasOngoingFoci() {
		return compoundLocation != null;
	}

	private void register() {
		programID = GL20.glCreateProgram();
		if (programID == 0) {
			ShaderRegistry.error(owner, identifier, "Shader program could not be assigned an ID!", null);
		}
		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);
		GL20.glLinkProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			ShaderRegistry.error(owner, identifier, "Shader was not linked properly: "+ShaderRegistry.parseError(programID), null);
		}
		GL20.glValidateProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			ShaderRegistry.error(owner, identifier, "Shader failed to validate: "+ShaderRegistry.parseError(programID), null);
		}
	}

	public void updateEnabled() {
		if (hook != null)
			hook.updateEnabled(this);
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

	public static class RenderState {

		private Vector3f position;
		private Matrix4f modelview;
		private Matrix4f projection;
		private float intensity;
		private HashMap<String, Object> variables;

		private RenderState(Vector3f vec, Matrix4f m, Matrix4f p) {
			this(vec, m, p, 1, null);
		}

		private RenderState(Vector3f vec, Matrix4f m, Matrix4f p, float f, HashMap<String, Object> vars) {
			position = vec;
			modelview = m;
			projection = p;
			intensity = f;
			variables = vars;
		}

	}

}
