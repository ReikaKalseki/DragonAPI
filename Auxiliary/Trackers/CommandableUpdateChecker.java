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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Extras.ModVersion;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Event.ClientLoginEvent;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CommandableUpdateChecker {

	public static final CommandableUpdateChecker instance = new CommandableUpdateChecker();

	public static final String reikaURL = "http://server.techjargaming.com/Reika/versions";

	private final HashMap<DragonAPIMod, ModVersion> latestVersions = new HashMap();
	private final Collection<UpdateChecker> checkers = new ArrayList();
	private final Collection<DragonAPIMod> oldMods = new ArrayList();
	private final HashMap<String, DragonAPIMod> modNames = new HashMap();
	private final HashMap<DragonAPIMod, Boolean> overrides = new HashMap();
	private final HashMap<DragonAPIMod, UpdateHash> hashes = new HashMap();

	private CommandableUpdateChecker() {

	}

	public void checkAll() {
		this.getOverrides();
		for (UpdateChecker c : checkers) {
			DragonAPIMod mod = c.mod;
			if (this.shouldCheck(mod)) {
				ModVersion version = c.version;
				ModVersion latest = latestVersions.get(mod);
				//if (version.isCompiled()) {
				if (version.compareTo(latest) < 0) {
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

	public boolean beAggressive(DragonAPIMod mod, EntityPlayer ep) {
		return DragonOptions.UPDATESCROLL.getState() || !this.canSkipAnnoyance(mod, ep);
	}

	private boolean canSkipAnnoyance(DragonAPIMod mod, EntityPlayer ep) {
		ModVersion latest = latestVersions.get(mod);
		ModVersion version = mod.getModVersion();
		if (latest.majorVersion-version.majorVersion > 1)
			return false;
		UpdateHash test = this.genHash(mod, ep);
		UpdateHash get = this.getOrCreateHash(mod, ep);

		return this.canHideMessage(get, test);
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

	private void markUpdate(DragonAPIMod mod, ModVersion version, ModVersion latest) {
		oldMods.add(mod);

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("modDisplayName", mod.getDisplayName());
		nbt.setString("oldVersion", version.toString());
		nbt.setString("newVersion", latest.toString());
		nbt.setString("updateUrl", mod.getDocumentationSite().toString());
		nbt.setBoolean("isDirectLink", false);
		nbt.setString("changeLog", mod.getDocumentationSite().toString());
		FMLInterModComms.sendRuntimeMessage(mod.getModContainer().getModId(), "VersionChecker", "addUpdate", nbt);
	}

	private boolean canHideMessage(UpdateHash get, UpdateHash test) {
		return !get.equals(test) && test.timestamp < get.timestamp+(1000*3600*24*14) && test.timestamp >= get.timestamp; //2 weeks, and cannot be errored
	}

	public void registerMod(DragonAPIMod mod) {
		ModVersion version = mod.getModVersion();
		if (version == ModVersion.source && false) {
			mod.getModLogger().log("Mod is in source code form. Not checking versions.");
			return;
		}
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
		this.genHashes(ep);
		if (!oldMods.isEmpty()) {
			this.sendMessages(ep);
			if (ep instanceof EntityPlayerMP)
				ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.OLDMODS.ordinal(), (EntityPlayerMP)ep);
		}
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
	public void onClientReceiveOldModsNote(EntityPlayer ep) {
		StringBuilder sb = new StringBuilder();
		for (DragonAPIMod mod : oldMods) {
			if (this.beAggressive(mod, ep)) {
				sb.append(mod.getDisplayName()+" ("+mod.getModVersion()+" -> "+latestVersions.get(mod).toString()+"); ");
			}
		}
		if (sb.length() > 0)
			MinecraftForge.EVENT_BUS.register(new ScreenWriter(sb.toString()));
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
			return true;
		}

		@Override
		public void processCommand(ICommandSender ics, String[] args) {
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			if (args.length == 2) {
				String action = args[0];
				String name = args[1];
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

	private static class UpdateChecker {

		private final ModVersion version;
		private final URL checkURL;
		private final DragonAPIMod mod;

		private UpdateChecker(DragonAPIMod mod, ModVersion version, URL url) {
			this.mod = mod;
			this.version = version;
			checkURL = url;
		}

		private ModVersion getLatestVersion() {
			try {
				ArrayList<String> lines = ReikaFileReader.getFileAsLines(checkURL, 10000, false);
				String name = ReikaStringParser.stripSpaces(mod.getDisplayName().toLowerCase());
				for (String line : lines) {
					if (line.toLowerCase().startsWith(name)) {
						String[] parts = line.split(":");
						ModVersion version = ModVersion.getFromString(parts[1]);
						return version;
					}
				}
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
				char c1 = idx >= time.length() ? '*' : time.charAt(idx);
				char c2 = idx >= id.length() ? '*' : id.charAt(idx); possibly not working
				char c3 = idx >= filepath.length() ? '*' : filepath.charAt(idx);
				long sum = c1 | (c2 << 16) | (c3 << 32);
				idx++;
				sb.append(this.getStringForInt(sum)+":");
			}
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
				char c2 = (char)((dat >> 16) & 65535); not working correctly
				char c3 = (char)((dat >> 32) & 65535);
				if (c1 != '*')
					time.append(c1);
				if (c2 != '*')
					id.append(c2);
				if (c3 != '*')
					path.append(c3);
			}
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

	@SideOnly(Side.CLIENT)
	public static class ScreenWriter {

		private final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		private final String pre = "Out of Date Mods: ";
		private final int pw = fr.getStringWidth(pre);
		private final String text;
		private final int w;

		private int tick;
		private int dx = pw;

		private ScreenWriter(String s) {
			text = s;
			w = fr.getStringWidth(s);
		}

		@SubscribeEvent
		public void drawOverlay(RenderGameOverlayEvent evt) {
			if (evt.type == ElementType.HELMET) {
				tick++;
				ReikaGuiAPI.instance.drawRect(0, 0, w, fr.FONT_HEIGHT-1, 0xff222222);
				int sw = Minecraft.getMinecraft().displayWidth/evt.resolution.getScaleFactor()-pw;
				if (w > sw && tick%(1+ReikaRenderHelper.getFPS()/20) == 0) {
					dx--;
				}
				if (dx <= -w+pw)
					dx = Minecraft.getMinecraft().displayWidth/evt.resolution.getScaleFactor();
				fr.drawString(text, dx, 0, 0xffffff);

				ReikaGuiAPI.instance.drawRect(0, 0, pw, fr.FONT_HEIGHT-1, 0xff222222);
				fr.drawString(pre, 0, 0, 0xffffff);
			}
		}

	}

}
