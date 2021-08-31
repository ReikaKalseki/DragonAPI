package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class ShaderLibrary {

	private static final HashMap<String, ShaderLibrary> libraries = new HashMap();
	private static final HashMap<String, Constructor<ComputedLibrary>> computed = new HashMap();

	public final String name;

	private ArrayList<String> code = new ArrayList();

	static void loadLibraries() {
		if (libraries.isEmpty()) {
			try (InputStream in = DragonAPICore.class.getResourceAsStream("Resources/Shader/liblist.txt")) {
				if (in == null)
					ShaderRegistry.error(DragonAPIInit.instance, null, "Shader library manifest not found", null);
				ArrayList<String> li = ReikaFileReader.getFileAsLines(in, true, Charset.defaultCharset());
				for (String s : li) {
					ShaderLibrary lib = new ShaderLibrary(s);
					libraries.put(s, lib);
				}
			}
			catch (IOException e) {
				ShaderRegistry.error(DragonAPIInit.instance, null, "Failed to load shader library manifest", null, e);
			}

			for (ShaderLibrary s : libraries.values()) {
				s.code = s.load();
			}
		}
	}

	private ShaderLibrary(String s) {
		name = s;
	}

	protected ArrayList<String> load() {
		try (InputStream in = DragonAPICore.class.getResourceAsStream("Resources/Shader/lib_"+name+".txt")) {
			return ReikaFileReader.getFileAsLines(in, true, Charset.defaultCharset());
		}
		catch (IOException e) {
			ShaderRegistry.error(DragonAPIInit.instance, name, "Failed to load shader library", null, e);
			return new ArrayList();
		}
	}

	public final void reload() {
		code = this.load();
	}

	final String getCode() {
		StringBuilder sb = new StringBuilder();
		for (String s : code) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	static ShaderLibrary getLibrary(String id) {
		return libraries.get(id);
	}

	static ShaderLibrary getCompute(String id, String... params) throws Exception {
		Constructor<ComputedLibrary> ctr = computed.get(id);
		if (ctr == null)
			return null;
		return ctr.newInstance(new Object[]{params});
	}

	public static void registerComputedLibrary(DragonAPIMod mod, String id, Class<? extends ComputedLibrary> c) {
		if ((c.getModifiers() & Modifier.ABSTRACT) != 0)
			throw new RegistrationException(mod, "Invalid computed library patch "+id+"/"+c.getName()+": class is abstract");
		Constructor<ComputedLibrary> ctr;
		try {
			ctr = (Constructor<ComputedLibrary>)c.getDeclaredConstructor(String[].class);
			ctr.setAccessible(true);
			computed.put(id, ctr);
		}
		catch (Exception e) {
			throw new RegistrationException(mod, "Invalid computed library patch "+id+"/"+c.getName(), e);
		}
	}

	public static abstract class ComputedLibrary extends ShaderLibrary {

		protected final Object[] params;

		protected ComputedLibrary(String s, String[] args) {
			super(s);
			params = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				params[i] = this.convertArg(args[i]);
			}
			this.reload();
		}

		protected abstract ArrayList<String> generate();

		private Object convertArg(String s) {
			try {
				return Integer.parseInt(s);
			}
			catch (Exception e) {

			}
			try {
				return Float.parseFloat(s);
			}
			catch (Exception e) {

			}
			return s;
		}

		@Override
		protected final ArrayList<String> load() {
			return this.generate();
		}

	}

	private static class Blur extends ComputedLibrary {

		private Blur(String[] args) {
			super("blur", args);
		}

		@Override
		protected ArrayList<String> generate() {
			int radius = (int)params[0];
			ArrayList<String> li = new ArrayList();
			li.add("vec4 blur"+radius+"(vec2 uv) {");

			li.add("vec4 color = vec4(0.0);");
			li.add("color.a = 1.0;");
			li.add("float sum = 0.0;");

			li.add("float f = 0.0;");
			li.add("vec2 duv = vec2(0.0);");
			li.add("vec4 get = vec4(0.0);");

			int r = radius+1;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					float dd = (float)ReikaMathLibrary.py3d(i, 0, k);
					float f = dd <= radius ? 1-(float)Math.sqrt(dd/radius) : 0;
					if (f > 0) {
						li.add("f = float("+f+");");
						li.add("sum += f;");
						li.add("duv = uv+vec2(float("+i+")/float(screenWidth), float("+k+")/float(screenHeight));");
						li.add("get = texture2D(bgl_RenderedTexture, duv);");
						li.add("color += get*f;");
					}
				}
			}
			li.add("color /= sum;");
			li.add("color = min(vec4(1.0), color);");

			li.add("return color; ");
			li.add("}");
			return li;
		}

	}

	static {
		registerComputedLibrary(DragonAPIInit.instance, "blur", Blur.class);
	}

}
