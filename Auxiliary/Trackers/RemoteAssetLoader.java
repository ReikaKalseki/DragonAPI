/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JOptionPane;

import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.IO.ReikaFileReader.ConnectionErrorHandler;
import Reika.DragonAPI.IO.ReikaFileReader.FileReadException;
import Reika.DragonAPI.IO.ReikaFileReader.FileWriteException;
import Reika.DragonAPI.IO.ReikaFileReader.HashType;
import Reika.DragonAPI.IO.ReikaFileReader.WriteCallback;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLoginEvent;
import Reika.DragonAPI.Libraries.IO.ReikaFormatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class RemoteAssetLoader {

	public static final RemoteAssetLoader instance = new RemoteAssetLoader();

	private final ArrayList<RemoteAsset> downloadingAssets = new ArrayList();

	private AssetDownloader downloader;
	private Thread downloadThread;

	private final ArrayList<BigWarning> bigWarnings = new ArrayList();

	private RemoteAssetLoader() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void registerAssets(RemoteAssetRepository rar) {
		rar.load();
		for (RemoteAsset a : rar.getAssets()) {
			if (a.requiresDownload)
				downloadingAssets.add(a);
		}
	}

	public void checkAndStartDownloads() {
		if (!downloadingAssets.isEmpty()) {
			DragonAPICore.log("Some remote assets need to be redownloaded:");
			downloader = new AssetDownloader();
			for (RemoteAsset a : downloadingAssets) {
				a.log("Remote Asset '"+a.getDisplayName()+"' for "+a.mod.getDisplayName()+" is either missing or out of date. Redownloading...");
				downloader.totalSize += a.data.size;
			}
			DragonAPICore.log("Projected total download size: "+downloader.totalSize+" bytes in "+downloadingAssets.size()+" files.");
			downloadThread = new Thread(downloader, "Remote Asset Download");
			downloadThread.start();
		}
		else {
			MinecraftForge.EVENT_BUS.post(new RemoteAssetsDownloadCompleteEvent(downloadingAssets, 0));
		}
	}

	public float getDownloadProgress() {
		return downloader != null ? Math.min(1, downloader.getTotalCompletion()) : 1F;
	}

	public float getCurrentFileProgress() {
		return downloader != null ? Math.min(1, downloader.getCurrentFileCompletion()) : 1F;
	}

	public boolean isDownloadComplete() {
		return downloadThread == null || !downloadThread.isAlive() || downloader == null || downloader.isComplete;
	}

	@SubscribeEvent
	public void onClientReceiveWarning(ClientLoginEvent evt) {
		for (BigWarning w : bigWarnings) {
			String sg = w.message+" the file server for remote asset repository '"+w.repository.getDisplayName()+"' may be inaccessible. Check your internet settings, and please notify "+w.repository.mod.getModAuthorName()+" if the server is not accessible.";
			PopupWriter.instance.addMessage(sg);
		}
	}

	public static class AssetDownloader implements Runnable, WriteCallback {

		private long totalSize = 0;
		private long downloaded = 0;

		private RemoteAsset activeAsset;
		private long currentDownload;

		private boolean isComplete = false;

		@Override
		public void run() {
			long time = System.currentTimeMillis();
			DragonAPICore.log("Remote asset download thread starting...");
			for (RemoteAsset a : instance.downloadingAssets) {
				activeAsset = a;
				currentDownload = 0;
				this.tryDownload(a.data, 5);
			}
			long duration = System.currentTimeMillis()-time;
			DragonAPICore.log("All asset downloads complete. Elapsed time: "+ReikaFormatHelper.millisToHMSms(duration));
			isComplete = true;
			MinecraftForge.EVENT_BUS.post(new RemoteAssetsDownloadCompleteEvent(instance.downloadingAssets, totalSize));
		}

		public float getTotalCompletion() {
			return (float)downloaded/totalSize;
		}

		public float getCurrentFileCompletion() {
			return (float)currentDownload/activeAsset.data.size;
		}

		private void tryDownload(AssetData dat, int max) {
			for (int i = 0; i < max; i++) {
				try {
					this.download(dat);
					break;
				}
				catch (FileReadException e) {
					boolean end = i == max-1;
					String text = end ? "Skipping file." : "Retrying...";
					/*dat.asset.mod.getModLogger()*/DragonAPICore.logError("Could not read remote asset '"+dat.getDisplayName()+"'. "+text);
					e.printStackTrace();
					if (end) {
						dat.asset.parent.logError(e.getLocalizedMessage(), true);
						break;
					}
				}
				catch (FileWriteException e) {
					/*dat.asset.mod.getModLogger()*/DragonAPICore.logError("Could not save asset '"+dat.getDisplayName()+"'. Skipping file.");
					e.printStackTrace();
					break;
				}
				catch (IOException e) {
					/*dat.asset.mod.getModLogger()*/DragonAPICore.logError("Could not download remote asset '"+dat.getDisplayName()+"'. Skipping file.");
					e.printStackTrace();
					break;
				}
			}
		}

		private void download(AssetData dat) throws IOException {
			String local = dat.asset.getLocalPath();
			File f = new File(local);
			if (!f.getAbsolutePath().replaceAll("\\\\", "/").startsWith(DragonAPICore.getMinecraftDirectoryString())) {
				StringBuilder sb = new StringBuilder();
				sb.append("Remote Asset "+dat.asset.getDisplayName()+" attempted to download to "+f.getAbsolutePath()+"!");
				sb.append(" This is not in the MC directory and very likely either malicious or poorly implemented, or the remote server has been compromised!");
				String s = sb.toString();
				dat.asset.parent.logError(s, true);
				return;
				//throw new RuntimeException(s);
			}
			f.getParentFile().mkdirs();
			f.delete();
			f.createNewFile();
			URLConnection c = new URL(dat.path).openConnection();
			InputStream in = c.getInputStream();
			OutputStream out = new FileOutputStream(f);

			long time = System.currentTimeMillis();
			ReikaFileReader.copyFile(in, out, 4096, this);
			long duration = System.currentTimeMillis()-time;

			String s = "Download of '"+dat.getDisplayName()+"' to '"+dat.asset.getLocalPath()+"' complete. Elapsed time: "+ReikaFormatHelper.millisToHMSms(duration);
			/*dat.asset.mod.getModLogger()*/DragonAPICore.log(s);
			DragonAPICore.log("Remote asset downloads now "+String.format("%.2f", Math.min(100, this.getTotalCompletion()*100))+"% complete.");
			dat.asset.downloaded = true;

			in.close();
			out.close();
		}

		@Override
		public void onWrite(byte[] data) {
			downloaded += data.length;
			currentDownload += data.length;
		}

	}

	public static class AssetData {

		private final RemoteAsset asset;
		private final String name;
		private final String path;
		private final long size;
		private final String hash;

		public AssetData(RemoteAsset a, String p, String n, String h, long s) {
			asset = a;
			name = n;
			path = p;
			size = s;
			hash = h;
		}

		private String getLocalHash() {
			File f = new File(asset.getLocalPath());
			return f.exists() ? ReikaFileReader.getHash(f, HashType.MD5) : "";
		}

		private boolean match() {
			return this.getLocalHash().equalsIgnoreCase(hash);
		}

		public String getDisplayName() {
			return ReikaStringParser.capFirstChar(name);
		}

	}

	public static abstract class RemoteAssetRepository implements ConnectionErrorHandler {

		private boolean nonAccessible;

		private final Collection<RemoteAsset> assets = new ArrayList();

		private final DragonAPIMod mod;

		protected RemoteAssetRepository(DragonAPIMod mod) {
			this.mod = mod;
		}

		private final void load() {
			URL url = null;
			try {
				url = URI.create(this.getRepositoryURL()).toURL();
			}
			catch (MalformedURLException e) {
				this.logError("Asset Repository URL invalid", true);
				e.printStackTrace();
				return;
			}
			ArrayList<String> li = ReikaFileReader.getFileAsLines(url, 10000, true, this, null);
			if (li == null) {
				if (!nonAccessible)
					this.logError("Could not load asset repository", true);
				return;
			}
			for (String s : li) {
				RemoteAsset a = this.parseAsset(s);
				if (a != null) {
					assets.add(a);
					a.filename = a.setFilename(s);
					a.extension = a.setExtension(s);
					a.data = a.constructData(s);
					a.requiresDownload = !a.data.match();
				}
			}
			this.writeList();
			DragonAPICore.log(assets.size()+" remote assets for "+mod.getDisplayName()+" found at "+this.getDisplayName()+": "+assets);
		}

		private void writeList() {
			try {
				String file = this.getLocalPath()+"file_list.dat";
				File f = new File(file);
				f.mkdirs();
				f.delete();
				f.createNewFile();
				ArrayList<String> li = new ArrayList();
				li.add("File list for remote asset repository '"+this.getDisplayName()+"'");
				li.add("Downloaded from "+this.getRepositoryURL()+" to "+this.getLocalPath());
				int n = li.get(li.size()-1).length();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < n; i++) {
					sb.append("=");
				}
				li.add(sb.toString());
				for (RemoteAsset a : assets) {
					li.add(a.getDisplayName()+" -> "+a.getLocalPath()+" {Size="+a.data.size+" B,  Hash="+a.data.hash+"}");
				}
				ReikaFileReader.writeLinesToFile(f, li, true);
				DragonAPICore.log("Writing file list for remote asset repository '"+this.getDisplayName()+"' to disk.");
			}
			catch (IOException e) {
				DragonAPICore.logError("Remote asset repository '"+this.getDisplayName()+"' could not save its file list to disk.");
				e.printStackTrace();
			}
		}

		protected abstract RemoteAsset parseAsset(String line);

		protected final Collection<RemoteAsset> getAssets() {
			return Collections.unmodifiableCollection(assets);
		}

		public final Collection<String> getAvailableResources() {
			String file = this.getLocalPath()+"file_list.dat";
			ArrayList<String> li = ReikaFileReader.getFileAsLines(file, true);
			ArrayList<String> ret = new ArrayList();
			for (String s : li) {
				int idx = s.indexOf('>');
				int idx2 = s.indexOf('{');
				if (idx >= 0 && idx2 >= idx) {
					String p = s.substring(idx+2, idx2-1);
					File f = new File(p);
					if (f.exists())
						ret.add(p);
				}
			}
			return ret;
		}

		public abstract String getRepositoryURL();
		public abstract String getLocalPath();

		@Override
		public final void onServerRedirected() {
			this.logError("Asset Server access redirected!?", true);
		}

		@Override
		public final void onNoInternet() {
			this.logError("Is your internet disconnected?", false);
		}

		@Override
		public final void onServerNotFound() {
			this.logError("Asset Server not found!", true);
		}

		@Override
		public final void onTimedOut() {
			this.logError("Timed Out", false);
		}

		private final void logError(String msg, boolean bigWarn) {
			nonAccessible = true;
			/*mod.getModLogger()*/DragonAPICore.logError("Error accessing online asset data file: "+msg);
			if (bigWarn) {
				instance.bigWarnings.add(new BigWarning("Downloading the remote assets failed: "+msg, this));
			}
		}

		public abstract String getDisplayName();

		@Override
		public final String toString() {
			return this.getDisplayName()+": "+assets.size()+"x"+assets;
		}

	}

	public static abstract class RemoteAsset {

		private final DragonAPIMod mod;
		private final RemoteAssetRepository parent;

		private String filename;
		private String extension;
		private boolean requiresDownload;
		private AssetData data;
		private boolean downloaded;

		protected RemoteAsset(DragonAPIMod mod, RemoteAssetRepository rar) {
			this.mod = mod;
			parent = rar;
		}

		public abstract String setFilename(String line);
		public abstract String setExtension(String line);

		public abstract String getDisplayName();

		public final String getLocalPath() {
			return parent.getLocalPath()+filename+"."+extension;
		}

		@Override
		public final String toString() {
			return this.getDisplayName();
		}

		protected final void log(String s) {
			/*mod.getModLogger()*/DragonAPICore.log(s);
		}

		protected abstract AssetData constructData(String line);

		public final boolean downloadedSuccessfully() {
			return downloaded;
		}

		public final boolean available() {
			return !requiresDownload || this.downloadedSuccessfully();
		}

	}

	private static class BigWarning {

		private final String message;
		private final RemoteAssetRepository repository;

		private BigWarning(String msg, RemoteAssetRepository rar) {
			message = msg;
			repository = rar;
		}

	}

	public static class RemoteAssetsDownloadCompleteEvent extends Event {

		/** If empty, this event is fired much earlier and all files were already present. */
		public final Collection<RemoteAsset> downloadQueue;
		public final long totalSize;

		private RemoteAssetsDownloadCompleteEvent(ArrayList<RemoteAsset> li, long size) {
			downloadQueue = Collections.unmodifiableCollection(li);
			totalSize = size;
		}

	}

	public static class DownloadDisplayWindow extends JOptionPane {

		private DownloadDisplayWindow() {
			//super(message, INFORMATION_MESSAGE, DEFAULT_OPTION, null, null, null);
		}

	}

}
