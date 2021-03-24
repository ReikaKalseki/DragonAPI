package Reika.DragonAPI.IO.Shaders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.IO.ReikaFileReader;

public class ShaderLibrary {

	private static final HashMap<String, ShaderLibrary> libraries = new HashMap();

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
				s.load();
			}
		}
	}

	private ShaderLibrary(String s) {
		name = s;
	}

	void load() {
		try (InputStream in = DragonAPICore.class.getResourceAsStream("Resources/Shader/lib_"+name+".txt")) {
			code = ReikaFileReader.getFileAsLines(in, true, Charset.defaultCharset());
		}
		catch (IOException e) {
			ShaderRegistry.error(DragonAPIInit.instance, name, "Failed to load shader library", null, e);
		}
	}

	String getCode() {
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

}
