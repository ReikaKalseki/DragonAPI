package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader.RemoteAsset;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader.RemoteAssetRepository;
import Reika.DragonAPI.Base.DragonAPIMod;

public class RemoteSourcedAsset {

	private static final String mcDir = DragonAPICore.getMinecraftDirectoryString();

	public final Class reference;
	public final String path;
	public final String remotePath;
	public final String localRemote;

	private RemoteSourcedAsset(Class ref, String s, String rem, String loc) {
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
		String fall = this.getFallbackPath();
		f = new File(fall);
		if (f.exists()) {
			return new FileInputStream(f);
		}
		else {
			DragonAPICore.logError("Could not find main resource for asset "+reference+"/"+path+"!");
			InputStream in = reference.getResourceAsStream(fall);
			if (in != null)
				return in;
			DragonAPICore.logError("Could not find ANY resource for asset "+reference+"/"+path+"!");
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

	public static class RemoteSourcedAssetRepository {

		public final Class rootClass;
		public final String rootPath;
		public final String rootRemote;
		public final String rootLocal;

		public final DragonAPIMod owner;
		private final RemoteAssetRepository repository;

		public RemoteSourcedAssetRepository(DragonAPIMod mod, Class c, String r, String l) {
			this(mod, c, "", r, l);
		}

		public RemoteSourcedAssetRepository(DragonAPIMod mod, Class c, String p, String r, String l) {
			rootClass = c;
			rootLocal = l;
			rootRemote = r;
			rootPath = p;

			owner = mod;
			repository = new DynamicRemoteAssetRepository();
		}

		public RemoteSourcedAsset createAsset(String file) {
			return new RemoteSourcedAsset(rootClass, rootPath.isEmpty() ? file : rootPath+"/"+file, rootRemote+"/"+file, rootLocal);
		}

		public void addToAssetLoader() {
			RemoteAssetLoader.instance.registerAssets(repository);
		}

		private class DynamicRemoteAssetRepository extends RemoteAssetRepository {

			private DynamicRemoteAssetRepository() {
				super(owner);
			}

			@Override
			public String getRepositoryURL() {
				return rootRemote;
			}

			@Override
			protected RemoteAsset parseAsset(String line) {
				return new MusicAsset(this);
			}

			@Override
			public String getDisplayName() {

			}

			@Override
			public String getLocalPath() {
				return rootLocal;
			}

		}

	}

}
