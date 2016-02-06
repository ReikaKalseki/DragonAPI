/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.PopupWriter;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Extras.ModVersion;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.IO.ReikaFileReader.ConnectionErrorHandler;
import Reika.DragonAPI.IO.ReikaFileReader.DataFetcher;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayMap;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLoginEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class CommandableUpdateChecker {

	public static final CommandableUpdateChecker instance = new CommandableUpdateChecker();

	public static final String reikaURL = "http://server.techjargaming.com/Reika/versions";

	private final HashMap<DragonAPIMod, ModVersion> latestVersions = new OneWayMap();
	private final Collection<UpdateChecker> checkers = new OneWayList();
	private final Collection<DragonAPIMod> oldMods = new OneWayList();
	private final Collection<DragonAPIMod> noURLMods = new OneWayList();

	private final HashMap<String, DragonAPIMod> modNames = new OneWayMap();
	private final HashMap<DragonAPIMod, String> modNamesReverse = new OneWayMap();

	private final HashMap<DragonAPIMod, Boolean> overrides = new OneWayMap();

	private final Collection<DragonAPIMod> dispatchedOldMods = new ArrayList();
	private final Collection<DragonAPIMod> dispatchedURLMods = new ArrayList();

	private final HashMap<DragonAPIMod, UpdateHash> hashes = new HashMap();

	private CommandableUpdateChecker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void checkAll() {
		this.getOverrides();
		for (UpdateChecker c : checkers) {
			DragonAPIMod mod = c.mod;
			if (this.shouldCheck(mod)) {
				ModVersion version = c.version;
				ModVersion latest = latestVersions.get(mod);
				//if (version.isCompiled()) {
				if (latest == ModVersion.timeout) {
					this.markUpdate(mod, version, latest);
					ReikaJavaLibrary.pConsole("-----------------------"+mod.getTechnicalName()+"-----------------------");
					ReikaJavaLibrary.pConsole("Could not connect to version server. Please check your internet settings,");
					ReikaJavaLibrary.pConsole("and if the server is unavailable please contact "+mod.getModAuthorName()+".");
					ReikaJavaLibrary.pConsole("------------------------------------------------------------------------");
				}
				else if (version.compareTo(latest) < 0) {
					this.markUpdate(mod, version, latest);
					ReikaJavaLibrary.pConsole("-----------------------"+mod.getTechnicalName()+"-----------------------");
					ReikaJavaLibrary.pConsole("This version of the mod ("+version+") is out of date.");
					ReikaJavaLibrary.pConsole("This version is likely to contain bugs, crashes, and/or exploits.");
					ReikaJavaLibrary.pConsole("No technical support whatsoever will be provided for this version.");
					ReikaJavaLibrary.pConsole("Update to "+latest+" as soon as possible; there is no good reason not to.");
					ReikaJavaLibrary.pConsole("------------------------------------------------------------------------");
					ReikaJavaLibrary.pConsole("");
				}
				//}
				//else {
				//
				//}
			}
		}
	}

	private void markUpdate(DragonAPIMod mod, ModVersion version, ModVersion latest) {
		if (latest == ModVersion.timeout) {
			noURLMods.add(mod);
		}
		else {
			oldMods.add(mod);

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("modDisplayName", mod.getDisplayName());
			nbt.setString("oldVersion", "v"+version.toString());
			nbt.setString("newVersion", "v"+latest.toString());
			nbt.setString("updateUrl", mod.getDocumentationSite().toString());
			nbt.setBoolean("isDirectLink", false);
			nbt.setString("changeLog", mod.getDocumentationSite().toString());
			FMLInterModComms.sendRuntimeMessage(mod.getModContainer().getModId(), "VersionChecker", "addUpdate", nbt);
		}
	}

	public void registerMod(DragonAPIMod mod) {
		ModVersion version = mod.getModVersion();
		if (version == ModVersion.source) {
			mod.getModLogger().log("Mod is in source code form. Not checking versions.");
			return;
		}
		if (mod.getUpdateCheckURL() == null)
			return;
		String url = mod.getUpdateCheckURL()+"_"+Loader.MC_VERSION.replaceAll("\\.", "-")+".txt";
		URL file = this.getURL(url);
		if (file == null) {
			mod.getModLogger().logError("Could not create URL to update checker. Version will not be checked.");
			return;
		}
		UpdateChecker c = new UpdateChecker(mod, version, file);
		ModVersion latest = c.getLatestVersion();
		if (latest == null) {
			mod.getModLogger().logError("Could not access online version reference. Please notify "+mod.getModAuthorName());
			return;
		}
		latestVersions.put(mod, latest);
		checkers.add(c);
		String label = ReikaStringParser.stripSpaces(mod.getDisplayName().toLowerCase());
		modNames.put(label, mod);
		modNamesReverse.put(mod, label);
	}

	private URL getURL(String url) {
		try {
			return new URL(url);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean shouldCheck(DragonAPIMod mod) {
		return overrides.containsKey(mod) ? overrides.get(mod) : true;
	}

	private void getOverrides() {
		File f = this.getFile();
		if (f.exists()) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
			for (int i = 0; i < li.size(); i++) {
				String line = li.get(i);
				String[] parts = line.split(":");
				DragonAPIMod mod = modNames.get(parts[0]);
				boolean b = Boolean.parseBoolean(parts[1]);
				ModVersion version = ModVersion.getFromString(parts[2]);
				if (version.equals(latestVersions.get(mod)))
					overrides.put(mod, b);
			}
		}
	}

	private void setChecker(DragonAPIMod mod, boolean enable) {
		File f = this.getFile();
		String name = ReikaStringParser.stripSpaces(mod.getDisplayName().toLowerCase());
		ModVersion latest = latestVersions.get(mod);
		if (f.exists()) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
			Iterator<String> it = li.iterator();
			while (it.hasNext()) {
				String line = it.next();
				if (line.startsWith(name)) {
					it.remove();
				}
			}
			li.add(name+":"+enable+":"+latest);
			try {
				PrintWriter p = new PrintWriter(f);
				for (int i = 0; i < li.size(); i++)
					p.append(li.get(i)+"\n");
				p.close();
			}
			catch (IOException e) {

			}
		}
		else {
			try {
				f.createNewFile();
				PrintWriter p = new PrintWriter(f);
				p.append(name+":"+enable+":"+latest);
				p.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File getFile() {
		String base = DragonAPICore.getMinecraftDirectory().getAbsolutePath();
		File parent = new File(base+"/saves/DragonAPI");
		if (!parent.exists())
			parent.mkdirs();
		String path = parent+"/ucheck.dat";
		File f = new File(path);
		return f;
	}

	public void notifyPlayer(EntityPlayer ep) {
		if (!oldMods.isEmpty() || !noURLMods.isEmpty()) {
			this.sendMessages(ep);
		}

		if (ep instanceof EntityPlayerMP) {
			PacketTarget pt = new PacketTarget.PlayerTarget((EntityPlayerMP)ep);
			for (DragonAPIMod mod : oldMods) {
				if (this.beAggressive(mod, (EntityPlayerMP)ep)) {
					ReikaPacketHelper.sendStringPacket(DragonAPIInit.packetChannel, PacketIDs.OLDMODS.ordinal(), modNamesReverse.get(mod), pt);
				}
			}
			for (DragonAPIMod mod : noURLMods) {
				ReikaPacketHelper.sendStringPacket(DragonAPIInit.packetChannel, PacketIDs.OLDMODS.ordinal(), "URL_"+modNamesReverse.get(mod), pt);
			}
		}
	}

	private boolean beAggressive(DragonAPIMod mod, EntityPlayerMP ep) {
		boolean abandonedPack = latestVersions.get(mod).majorVersion-mod.getModVersion().majorVersion > 1;
		if (!abandonedPack && DragonOptions.PACKONLYUPDATE.getState()) {
			return this.isPackMaker(mod, ep);
		}
		else if (DragonOptions.OPONLYUPDATE.getState()) {
			return DragonAPICore.isSinglePlayer() || ReikaPlayerAPI.isAdmin(ep);
		}
		return true;
	}

	private boolean isPackMaker(DragonAPIMod mod, EntityPlayerMP ep) {
		UpdateHash test = this.genHash(mod, ep);
		UpdateHash get = this.getOrCreateHash(mod, ep);
		return !!get.equals(test);
	}

	private UpdateHash getOrCreateHash(DragonAPIMod mod, EntityPlayer ep) {
		UpdateHash uh = hashes.get(mod);
		if (uh == null) {
			uh = this.readHash(mod);
			if (uh == null) {
				uh = this.genHash(mod, ep);
				this.writeHash(mod, uh);
			}
			hashes.put(mod, uh);
		}
		return uh;
	}

	private UpdateHash readHash(DragonAPIMod mod) {
		File f = this.getHashFile();
		ArrayList<String> data = ReikaFileReader.getFileAsLines(f, true);
		for (String s : data) {
			String tag = mod.getDisplayName()+"=";
			if (s.startsWith(tag)) {
				return UpdateHash.decode(s.substring(tag.length()));
			}
		}
		return null;
	}

	private void writeHash(DragonAPIMod mod, UpdateHash uh) {
		File f = this.getHashFile();
		ArrayList<String> data = ReikaFileReader.getFileAsLines(f, true);
		String tag = mod.getDisplayName()+"=";
		data.add(tag+uh.toString());
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			String sep = System.getProperty("line.separator");
			String line = r.readLine();
			StringBuilder out = new StringBuilder();
			for (String l : data) {
				out.append(l+sep);
			}
			r.close();
			FileOutputStream os = new FileOutputStream(f);
			os.write(out.toString().getBytes());
			os.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getHashFile() {
		String base = DragonAPICore.getMinecraftDirectoryString();
		File parent = new File(base+"/config/Reika");
		if (!parent.exists())
			parent.mkdirs();
		String path = parent+"/versions.dat";
		File f = new File(path);
		try {
			if (!f.exists())
				f.createNewFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	private UpdateHash genHash(DragonAPIMod mod, EntityPlayer ep) {
		return new UpdateHash(ep.getUniqueID(), mod.getModContainer().getSource().getAbsolutePath(), System.currentTimeMillis());
	}

	@SideOnly(Side.CLIENT)
	public void onClientLogin(ClientLoginEvent evt) {
		this.genHashes(evt.player);
	}

	private void genHashes(EntityPlayer ep) {
		for (DragonAPIMod mod : latestVersions.keySet()) {
			this.getOrCreateHash(mod, ep);
		}
	}

	@SideOnly(Side.CLIENT)
	public void onClientReceiveOldModID(String s) {
		Collection<DragonAPIMod> c = s.startsWith("URL_") ? dispatchedURLMods : dispatchedOldMods;
		if (s.startsWith("URL_"))
			s = s.substring(4);
		DragonAPIMod mod = modNames.get(s);
		if (!c.contains(mod))
			c.add(mod);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientReceiveOldModsNote(ClientLoginEvent evt) {
		ArrayList<String> li = new ArrayList();
		for (DragonAPIMod mod : dispatchedOldMods) {
			StringBuilder sb = new StringBuilder();
			sb.append(mod.getDisplayName());
			sb.append(" ");
			sb.append(mod.getModVersion());
			sb.append(" is out of date. Update to ");
			sb.append(latestVersions.get(mod).toString());
			sb.append(" as soon as possible.");
			li.add(sb.toString());
		}
		for (DragonAPIMod mod : dispatchedURLMods) {
			StringBuilder sb = new StringBuilder();
			sb.append(mod.getDisplayName());
			sb.append(" could not verify its version; the version server may be inaccessible. Check your internet settings, and please notify ");
			sb.append(mod.getModAuthorName());
			sb.append(" if the server is not accessible.");
			li.add(sb.toString());
		}
		for (String s : li) {
			PopupWriter.instance.addMessage(s);
		}
	}

	private void sendMessages(EntityPlayer ep) {
		String sg = EnumChatFormatting.YELLOW.toString()+"DragonAPI Notification:";
		ReikaChatHelper.sendChatToPlayer(ep, sg);
		for (DragonAPIMod mod : oldMods) {
			String s = this.getChatMessage(mod);
			ReikaChatHelper.sendChatToPlayer(ep, s);
		}
		String g = EnumChatFormatting.YELLOW.toString();
		String sg2 = g+"To disable this notifcation for any mod, type \"/"+CheckerDisableCommand.tag+" disable [modname]\".";
		ReikaChatHelper.sendChatToPlayer(ep, sg2);
		sg2 = g+"Changes take effect upon server or client restart.";
		ReikaChatHelper.sendChatToPlayer(ep, sg2);
	}

	private String getChatMessage(DragonAPIMod mod) {
		ModVersion latest = latestVersions.get(mod);
		String g = EnumChatFormatting.LIGHT_PURPLE.toString();
		String r = EnumChatFormatting.RESET.toString();
		return g+mod.getDisplayName()+r+" is out of date, likely has errors, and is no longer supported. Update to "+latest+".";
	}

	public static class CheckerDisableCommand extends DragonCommandBase {

		public static final String tag = "checker";

		@Override
		public String getCommandString() {
			return tag;
		}

		@Override
		protected boolean isAdminOnly() {
			return false;
		}

		@Override
		public void processCommand(ICommandSender ics, String[] args) {
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			if (args.length == 2) {
				String action = args[0];
				String name = args[1].toLowerCase();
				DragonAPIMod mod = instance.modNames.get(name);
				if (mod != null) {
					if (action.equals("disable")) {
						instance.setChecker(mod, false);
						String sg = EnumChatFormatting.BLUE.toString()+"Update checker for "+mod.getDisplayName()+" disabled.";
						ReikaChatHelper.sendChatToPlayer(ep, sg);
					}
					else if (action.equals("enable")) {
						instance.setChecker(mod, true);
						String sg = EnumChatFormatting.BLUE.toString()+"Update checker for "+mod.getDisplayName()+" enabled.";
						ReikaChatHelper.sendChatToPlayer(ep, sg);
					}
					else {
						String sg = EnumChatFormatting.RED.toString()+"Invalid argument '"+action+"'.";
						ReikaChatHelper.sendChatToPlayer(ep, sg);
					}
				}
				else {
					String sg = EnumChatFormatting.RED.toString()+"Mod '"+name+"' not found.";
					ReikaChatHelper.sendChatToPlayer(ep, sg);
				}
			}
			else {
				String sg = EnumChatFormatting.RED.toString()+"Invalid arguments.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
			}
		}
	}

	private static class UpdateChecker implements ConnectionErrorHandler, DataFetcher {

		private final ModVersion version;
		private final URL checkURL;
		private final DragonAPIMod mod;
		private Date modified;

		private UpdateChecker(DragonAPIMod mod, ModVersion version, URL url) {
			this.mod = mod;
			this.version = version;
			checkURL = url;
		}

		private ModVersion getLatestVersion() {
			try {
				ArrayList<String> lines = ReikaFileReader.getFileAsLines(checkURL, 10000, false, this, this);
				String name = ReikaStringParser.stripSpaces(mod.getDisplayName().toLowerCase());
				for (String line : lines) {
					if (line.toLowerCase().startsWith(name)) {
						String[] parts = line.split(":");
						ModVersion version = ModVersion.getFromString(parts[1]);
						return version;
					}
				}
			}
			catch (VersionNotLoadableException e) {
				return ModVersion.timeout;
			}
			catch (Exception e) {
				this.logError(e);
			}
			return null;
		}

		private void logError(Exception e) {
			if (e instanceof IOException) {
				mod.getModLogger().logError("IO Error accessing online file:");
				mod.getModLogger().log(e.getClass().getCanonicalName()+": "+e.getLocalizedMessage());
				mod.getModLogger().log(e.getStackTrace()[0].toString());
			}
			else {
				mod.getModLogger().logError("Error accessing online file:");
				e.printStackTrace();
			}
		}

		@Override
		public void onServerRedirected() {
			throw new VersionNotLoadableException("Version server not found!");
		}

		@Override
		public void onNoInternet() {
			mod.getModLogger().logError("Error accessing online file: Is your internet disconnected?");
		}

		@Override
		public void onServerNotFound() {
			throw new VersionNotLoadableException("Version server not found!");
		}

		@Override
		public void onTimedOut() {
			mod.getModLogger().logError("Error accessing online file: Timed Out");
		}

		@Override
		public void fetchData(URLConnection c) throws Exception {
			String lastModified = c.getHeaderField("Last-Modified");
			modified = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(lastModified);
		}
	}

	private static class UpdateHash {

		private final long timestamp;
		private final String filepath;
		private final UUID player;

		private UpdateHash(UUID id, String file, long time) {
			player = id;
			filepath = file;
			timestamp = time;

		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof UpdateHash) {
				UpdateHash uh = (UpdateHash)o;
				return uh.player == player && uh.filepath.equals(player);
			}
			return false;
		}

		@Override
		public String toString() {
			String time = String.valueOf(timestamp);
			String id = player.toString();
			StringBuffer sb = new StringBuffer();
			int idx = 0;
			while (idx < time.length() || idx < id.length() || idx < filepath.length()) {
				long c1 = idx >= time.length() ? '*' : time.charAt(idx);
				long c2 = idx >= id.length() ? '*' : id.charAt(idx);
				long c3 = idx >= filepath.length() ? '*' : filepath.charAt(idx);
				long sum = c1 | (c2 << 16) | (c3 << 32);
				idx++;
				//ReikaJavaLibrary.pConsole(c1+" & "+c2+" & "+c3+" > "+sum+" $ "+this.getStringForInt(sum));
				sb.append(this.getStringForInt(sum)+":");
			}
			//ReikaJavaLibrary.pConsole("Final: "+time+" & "+id+" & "+filepath+" > "+sb.toString());
			return sb.toString();
		}

		public static UpdateHash decode(String s) {
			StringBuilder path = new StringBuilder();
			StringBuilder id = new StringBuilder();
			StringBuilder time = new StringBuilder();
			String[] parts = s.split(":");
			for (int i = 0; i < parts.length; i++) {
				String p = parts[i];
				long dat = getIntForString(p);
				char c1 = (char)(dat & 65535);
				char c2 = (char)((dat >> 16) & 65535);
				char c3 = (char)((dat >> 32) & 65535);
				//ReikaJavaLibrary.pConsole(c1+" & "+c2+" & "+c3+" < "+dat+" $ "+p);
				if (c1 != '*')
					time.append(c1);
				if (c2 != '*')
					id.append(c2);
				if (c3 != '*')
					path.append(c3);
			}
			//ReikaJavaLibrary.pConsole("Final: "+time+" & "+id+" & "+path+" < "+s);
			return new UpdateHash(UUID.fromString(id.toString()), path.toString(), Long.parseLong(time.toString()));
		}

		private static String getStringForInt(long l) {
			return Long.toString(l, 36);
		}

		private static long getIntForString(String s) {
			return Long.parseLong(s, 36);
		}

		private static final char[] chars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', '~', '`', '+', '-', '=',
			'!', '@', '#', '$', '%', '^', '&', '*', '(', ')',
			'[', ']', '{', '}', ';', ':', '<', '>', ',', '.',
		};

	}

	private static class VersionNotLoadableException extends RuntimeException {

		public VersionNotLoadableException(String s) {
			super(s);
		}

	}

}
