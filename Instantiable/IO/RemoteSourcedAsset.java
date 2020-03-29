package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import Reika.DragonAPI.DragonAPICore;

public class RemoteSourcedAsset {

	private static final String mcDir = DragonAPICore.getMinecraftDirectoryString();

	public final Class reference;
	public final String path;
	public final String remotePath;
	public final String localRemote;

	public RemoteSourcedAsset(Class ref, String s, String rem, String loc) {
		reference = ref;
		path = s;
		remotePath = rem;
		localRemote = loc;
	}

	public InputStream getData() throws IOException {
		InputStream main = reference.getResourceAsStream(path);
		if (main != null) {
			return main;
		}
		File f = new File(this.getLocalAssetPath());
		if (f.exists()) {
			return new FileInputStream(f);
		}
		f = new File(this.getFallbackPath());
		if (f.exists()) {
			return new FileInputStream(f);
		}
		else {
			DragonAPICore.logError("Could not find any resource for asset "+reference+"/"+path+"!");
			return null;
		}
	}

	private String getFileExt() {
		return path.substring(path.lastIndexOf('.'));
	}

	private String getFilename() {
		return path.substring(path.lastIndexOf('/')+1);
	}

	public String getRemotePath() {
		return remotePath+"/"+this.getFilename();
	}

	private String getLocalAssetPath() {
		return mcDir+"/mods/"+localRemote;
	}

	private String getFallbackPath() {
		String ext = this.getFileExt();
		String main = path.substring(0, path.length()-ext.length());
		return main+"_fallback"+ext;
	}

}
