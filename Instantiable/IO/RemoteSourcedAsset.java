package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.IO.ReikaFileReader.FileReadException;
import Reika.DragonAPI.IO.ReikaFileReader.FileWriteException;
import Reika.DragonAPI.Libraries.MathSci.ReikaDateHelper;

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

	/** Make sure you close this! */
	public InputStream getData() throws IOException {
		InputStream main = this.getPrimary();
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

	private InputStream getPrimary() {
		return reference.getResourceAsStream(path);
	}

	public void load() {
		try (InputStream main = this.getPrimary()) {
			if (main != null) {
				return;
			}
			File f = new File(this.getLocalAssetPath());
			if (!f.exists()) {
				DragonAPICore.log("Downloading dynamic asset "+path+" from remote, as its local copy does not exist.");
				this.queueDownload();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void queueDownload() {
		DynamicAssetDownloader al = new DynamicAssetDownloader(this.getRemotePath(), this.getLocalAssetPath());
		new Thread(al, "Dynamic Asset Download "+path).start();
	}

	private String getFileExt() {
		return path.substring(path.lastIndexOf('.'));
	}

	private String getFilename() {
		return path.substring(path.lastIndexOf('/')+1);
	}

	public String getRemotePath() {
		return remotePath+"/"+path;//this.getFilename();
	}

	private String getLocalAssetPath() {
		return mcDir+"/mods/"+localRemote+"/"+path;
	}

	private String getFallbackPath() {
		String ext = this.getFileExt();
		String main = path.substring(0, path.length()-ext.length());
		return main+"_fallback"+ext;
	}

	private static class DynamicAssetDownloader implements Runnable {

		private final String remotePath;
		private final String localPath;
		private final File targetFile;

		private boolean isComplete = false;

		private DynamicAssetDownloader(String rem, String loc) {
			remotePath = rem;
			localPath = loc;
			targetFile = new File(localPath);
		}

		@Override
		public void run() {
			long time = System.currentTimeMillis();
			DragonAPICore.log("Remote asset download thread starting...");
			this.tryDownload(5);
			//MinecraftForge.EVENT_BUS.post(new RemoteAssetsDownloadCompleteEvent(instance.downloadingAssets, totalSize));
		}

		private void tryDownload(int max) {
			for (int i = 0; i < max; i++) {
				try {
					this.download();
					break;
				}
				catch (FileReadException e) {
					boolean end = i == max-1;
					String text = end ? "Skipping file." : "Retrying...";
					/*dat.asset.mod.getModLogger()*/DragonAPICore.logError("Could not read remote asset '"+remotePath+"'. "+text);
					e.printStackTrace();
					targetFile.delete();
					if (end)
						break;
				}
				catch (FileWriteException e) {
					/*dat.asset.mod.getModLogger()*/DragonAPICore.logError("Could not save asset '"+localPath+"'. Skipping file.");
					e.printStackTrace();
					targetFile.delete();
					break;
				}
				catch (IOException e) {
					/*dat.asset.mod.getModLogger()*/DragonAPICore.logError("Could not download remote asset '"+remotePath+"'. Skipping file.");
					e.printStackTrace();
					targetFile.delete();
					break;
				}
			}
		}

		private void download() throws IOException {
			if (!targetFile.getAbsolutePath().replaceAll("\\\\", "/").startsWith(DragonAPICore.getMinecraftDirectoryString())) {
				StringBuilder sb = new StringBuilder();
				sb.append("Dynamic Remote Asset "+remotePath+" attempted to download to "+targetFile.getAbsolutePath()+"!");
				sb.append(" This is not in the MC directory and very likely either malicious or poorly implemented, or the remote server has been compromised!");
				String s = sb.toString();
				DragonAPICore.logError(s);
				return;
				//throw new RuntimeException(s);
			}
			targetFile.getParentFile().mkdirs();
			targetFile.delete();
			targetFile.createNewFile();
			URLConnection c = new URL(remotePath).openConnection();
			InputStream in = c.getInputStream();
			OutputStream out = new FileOutputStream(targetFile);

			long time = System.currentTimeMillis();
			ReikaFileReader.copyFile(in, out, 4096);
			long duration = System.currentTimeMillis()-time;

			String s = "Download of '"+remotePath+"' to '"+localPath+"' complete. Elapsed time: "+ReikaDateHelper.millisToHMSms(duration)+". Filesize: "+targetFile.length();
			/*dat.asset.mod.getModLogger()*/DragonAPICore.log(s);
			isComplete = true;

			in.close();
			out.close();
		}

	}

	public static final class RemoteSourcedAssetRepository {

		public final Class rootClass;
		public final String rootPath;
		public final String rootRemote;
		public final String rootLocal;

		public final DragonAPIMod owner;
		//private final RemoteAssetRepository repository;

		public RemoteSourcedAssetRepository(DragonAPIMod mod, Class c, String r, String l) {
			this(mod, c, "", r, l);
		}

		public RemoteSourcedAssetRepository(DragonAPIMod mod, Class c, String p, String r, String l) {
			rootClass = c;
			rootLocal = l;
			rootRemote = r;
			rootPath = p;

			owner = mod;
			//repository = new DynamicRemoteAssetRepository();
		}

		public RemoteSourcedAsset createAsset(String file) {
			RemoteSourcedAsset rem = new RemoteSourcedAsset(rootClass, rootPath.isEmpty() ? file : rootPath+"/"+file, rootRemote, rootLocal);
			rem.load();
			return rem;
		}

		public void addToAssetLoader() {
			//RemoteAssetLoader.instance.registerAssets(repository);
		}

	}

}
