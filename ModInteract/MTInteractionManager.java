/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import minetweaker.util.IEventHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.IO.ReikaFileReader.LineEditor;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayMap;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWaySet;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

/** Used to prevent Minetweaker from interfering with the balance of a mod; mods can register items whose recipes cannot be removed or items which
 * cannot have additional recipes added. It does not allow for the prevention of creating recipes that <i>use</i> the items, as there is no harm that
 * can result from such use.
 * <br><br>
 * <b>--Message from Reika--</b>
 * <br>
 * As I can already imagine the complaints of some 500 upset server admins and mod authors, who will object to the whole purpose of this class,
 * I pose the following question:
 * <br>
 * Given that the rules that I have in place on some of my mods, which disallows using Minetweaker or other tools to change the core recipes - with the
 * reason for that being the abuse I received day in and day out from people screwing things up and blaming me back before I put such rules in place,
 * <a href="https://sites.google.com/site/reikasminecraft/modifications">as detailed on my website</a> - and the fact that, as I have
 * recently seen firsthand, some people take rules as suggestions and make such changes anyway, what <i>exactly</i> is the problem with the fact I am
 * now enforcing the rules? If you wish to say that this implementation causes problems, that there is a better way to enforce this, then I will by
 * all means listen, but if your argument is one of moralistic proclamations that I have no right to enforce my own rules, no matter what, or that
 * I am committing some offence by having rules in the first place, then I will still listen...but for my own amusement.
 * <br><br>
 * This class is <u>NOT</u> an attack on Minetweaker or its author StanHebben (who I must say left me extremely impressed with the complexity,
 * versatility, and sophistication of MT's internal workings and the ZenScript system), nor
 * do I have any animosity towards them (and I hope they feel the same for me). I am in full agreement that MT is a very useful tool, and one I have
 * used myself on several occasions, with excellent results. This class is purely to provide me - and anyone else using DragonAPI with a similar
 * need - a means to save myself from once again dealing with hordes of people who make changes to a mod with a dim understanding at best of their
 * effects, then chew out the developer of said mod when their pack or server balance implodes due to exploits/missing dependencies/endgame shortcuts.
 */
public final class MTInteractionManager {

	public static final MTInteractionManager instance = new MTInteractionManager();

	private final OneWayMap<Prevention, OneWaySet<KeyedItemStack>> data = new OneWayMap();
	//private final OneWayMap<Prevention, OneWayMap<MTItemEntry, ScanResults>> scanResult = new OneWayMap();

	private final Method reloadMethod;
	private final Method addReloadWatcher;

	private MTInteractionManager() {
		Method rl = null;
		Method rlw = null;
		try {
			Class api = Class.forName("minetweaker.MineTweakerImplementationAPI");
			rl = getReloadMethod(api);
			rlw = api.getDeclaredMethod("onReloadEvent", IEventHandler.class);
			rlw.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		reloadMethod = rl;
		addReloadWatcher = rlw;
		try {
			addReloadWatcher.invoke(null, new ReloadHandler());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Method getReloadMethod(Class api) {
		String[] names = {
				"reload",
				"reloadScripts",
				"reloadAllScripts",
				"rebuild",
				"rebuildScripts",
				"rebuildAllScripts",
		};
		for (int i = 0; i < names.length; i++) {
			try {
				Method m = api.getDeclaredMethod(names[i]);
				m.setAccessible(true);
				return m;
			}
			catch (NoSuchMethodException e) {

			}
		}
		return null;
	}

	public void scanAndRevert() {
		this.scanAndRemove();
		this.reloadMT();
	}

	private void scanAndRemove() {
		//ReikaJavaLibrary.pConsole("DRAGONAPI MINETWEAKER: Registry: "+data);
		ArrayList<File> files = this.getFiles();
		for (File f : files) {
			this.parseFile(f);
		}
	}

	private void parseFile(File f) {
		for (Prevention p : data.keySet()) {
			MTScriptScanner scan = new MTScriptScanner(p);
			scan.performChanges(f);
		}
	}

	/*
	private void removeFlaggedFileLines() {
		for (Prevention p : data.keySet()) {
			this.removeLinesFor(p);
		}
	}

	private void removeLinesFor(Prevention p) {
		OneWaySet<KeyedItemStack> li = data.get(p);
		OneWayMap<MTItemEntry, ScanResults> scan = scanResult.get(p);
		for (KeyedItemStack ks : li) {

		}
	}
	 */
	/** Reloads the MT scripts, by invoking the same method that /mt reload does. */
	private void reloadMT() {
		if (reloadMethod == null)
			return;
		try {
			reloadMethod.invoke(null);
		}
		catch (Exception e) {

		}
	}

	private ArrayList<File> getFiles() {
		String main = System.getProperty("user.dir").replaceAll("\\\\", "/");
		File folder = new File(main+"/scripts");
		return ReikaFileReader.getAllFilesInFolder(folder, ".zs");
	}

	/*
	private void parseFile(File f) {
		ArrayList<String> li = ReikaFileReader.getFileAsLines(f, false);
		for (int line = 0; line < li.size(); line++) {
			String s = li.get(line);
			if (s.startsWith("//")) { //comment

			}
			else {
				int period = s.indexOf('.');
				if (period >= 0) {
					this.parseLine(f.getName(), s.substring(period+1), line);
				}
				else { //not a recipe line

				}
			}
		}
	}

	private void parseLine(String file, String s, int line) {
		if (s.startsWith("add")) {
			this.addEntry(file, s, Prevention.NEWRECIPE, line);
		}
		else if (s.startsWith("remove")) {
			this.addEntry(file, s, Prevention.REMOVERECIPE, line);
		}
	}

	private void addEntry(String file, String s, Prevention p, int line) {
		OneWayMap<MTItemEntry, ScanResults> type = scanResult.get(p);
		if (type == null) {
			type = new OneWayMap();
			scanResult.put(p, type);
		}
		MTItemEntry item = new MTItemEntry(s.substring(s.indexOf('<')+1, s.indexOf('>')));
		ScanResults r = type.get(item);
		if (r == null) {
			r = new ScanResults();
			type.put(item, r);
		}
		r.addLine(file, line);
	}
	 */
	public void blacklistNewRecipesFor(ItemStack is) {
		this.addEntry(Prevention.NEWRECIPE, is, false, true);
	}

	public void blacklistRecipeRemovalFor(ItemStack is) {
		this.addEntry(Prevention.REMOVERECIPE, is, false, true);
	}

	public void blacklistNewRecipesFor(Block i) {
		this.addEntry(Prevention.NEWRECIPE, new ItemStack(i), true, true);
	}

	public void blacklistRecipeRemovalFor(Block i) {
		this.addEntry(Prevention.REMOVERECIPE, new ItemStack(i), true, true);
	}

	public void blacklistNewRecipesFor(Item i) {
		this.addEntry(Prevention.NEWRECIPE, new ItemStack(i), true, true);
	}

	public void blacklistRecipeRemovalFor(Item i) {
		this.addEntry(Prevention.REMOVERECIPE, new ItemStack(i), true, true);
	}

	public void blacklistNewRecipesFor(ItemStack is, boolean ignoreMetadata, boolean ignoreNBT) {
		this.addEntry(Prevention.NEWRECIPE, is, ignoreMetadata, ignoreNBT);
	}

	public void blacklistRecipeRemovalFor(ItemStack is, boolean ignoreMetadata, boolean ignoreNBT) {
		this.addEntry(Prevention.REMOVERECIPE, is, ignoreMetadata, ignoreNBT);
	}

	private void addEntry(Prevention p, ItemStack is, boolean ignoreMetadata, boolean ignoreNBT) {
		if (is == null || is.getItem() == null) {
			throw new MisuseException("Cannot block MT scripts on null items!");
		}
		ModContainer mc = Loader.instance().activeModContainer();
		if (mc == null) {
			throw new MisuseException("Cannot block MT scripts from outside a mod!");
		}
		UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(is.getItem());
		if (id == null) {
			throw new MisuseException("Cannot block MT scripts for non-mod items!");
		}
		if (!mc.getModId().equalsIgnoreCase(id.modId)) {
			throw new MisuseException("Cannot block MT scripts on items from another mod!");
		}
		OneWaySet<KeyedItemStack> li = data.get(p);
		if (li == null) {
			li = new OneWaySet();
			data.put(p, li);
		}
		KeyedItemStack ks = new KeyedItemStack(is);
		ks.setIgnoreMetadata(ignoreMetadata);
		ks.setIgnoreNBT(ignoreNBT);
		ks.setSimpleHash(true);
		ks.lock();
		li.add(ks);
	}

	private static enum Prevention {
		NEWRECIPE(),
		REMOVERECIPE();
	}

	/*
	private static enum Modes {
		RECIPE("recipes"),
		FURNACE("smelting");

		private final String tag;

		private static final ImmutableMap<String, Modes> map = new ImmutableMap();

		private Modes(String s) {
			tag = s+".";
		}

		public static Modes getMode(String s) {
			return Modes.getMode(s.substring(0, s.indexOf('(')));
		}

		static {
			for (Modes m : values())
				map.put(m.tag, m);
		}
	}
	 */

	private static final class MTItemEntry {

		private final String id;
		private final int meta;

		public MTItemEntry(String item) { //text inside <>
			String[] parts = item.split(":");
			int m = 0;
			boolean metapar = false;
			try {
				m = Integer.parseInt(parts[parts.length-1]);
				metapar = true;
			}
			catch (NumberFormatException e) {

			}
			meta = m;
			id = metapar ? item.substring(0, item.lastIndexOf(':')) : item;
		}

		@Override
		public int hashCode() {
			return id.hashCode()^meta;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MTItemEntry) {
				MTItemEntry mt = (MTItemEntry)o;
				return id.equals(mt.id) && mt.meta == meta;
			}
			return false;
		}

		private KeyedItemStack asKeyStack() {
			Item item = (Item)Item.itemRegistry.getObject(id);
			if (item == null)
				return null;
			KeyedItemStack ks = new KeyedItemStack(new ItemStack(item, 1, meta)).setSimpleHash(true).lock();
			return ks;
		}

		@Override
		public String toString() {
			return id+"%"+meta;
		}

	}
	/*
	private static class ScanResults {

		private static OneWayMap<String, OneWaySet<Integer>> data = new OneWayMap();

		private void addLine(String file, int line) {
			OneWaySet<Integer> lines = data.get(file);
			if (lines == null) {
				lines = new OneWaySet();
				data.put(file, lines);
			}
			lines.add(line);
		}

	}*/

	private static final class MTScriptScanner extends LineEditor {

		private final OneWaySet<KeyedItemStack> set;

		private MTScriptScanner(Prevention p) {
			set = instance.data.get(p);
		}

		@Override
		protected String getReplacementLine(String s) {
			return "//"+s; //comment
		}

		@Override
		public boolean editLine(String s) {
			//ReikaJavaLibrary.pConsole("DRAGONAPI MINETWEAKER: Parsing line: "+s);
			boolean flag = this.parseLine(s);
			if (flag) {
				//ReikaJavaLibrary.pConsole("#########Line was flagged!");
				ReikaJavaLibrary.pConsole("DragonAPI: The line '"+s+"' has been commented out of the Minetweaker script, as the mod registering this "+
						"item has requested to disallow such actions. This is NOT a bug in either that mod or Minetweaker; do not bother StanHebben "+
						"with it. You may ask the developer of the registrant mod for further questions or to request a removal. Be civil.");
			}
			return flag;
		}

		private boolean parseLine(String s) {
			if (s.startsWith("//")) { //comment
				return false;
			}
			else {
				int period = s.indexOf('.');
				if (period >= 0) {
					return this.parseTruncLine(s.substring(period+1));
				}
				else { //not a recipe line
					return false;
				}
			}
		}

		private boolean parseTruncLine(String s) {
			if (s.startsWith("add")) {
				return this.parse(s, Prevention.NEWRECIPE);
			}
			else if (s.startsWith("remove")) {
				return this.parse(s, Prevention.REMOVERECIPE);
			}
			return false;
		}

		private boolean parse(String s, Prevention p) {
			MTItemEntry item = new MTItemEntry(s.substring(s.indexOf('<')+1, s.indexOf('>')));
			KeyedItemStack ks = item.asKeyStack().setSimpleHash(true).lock();
			//ReikaJavaLibrary.pConsole("Parsed as "+item+", keys to "+ks);
			return ks != null && set.contains(ks);
		}

	}

	public static boolean isMTLoaded() {
		try {
			return Class.forName("minetweaker.MineTweakerImplementationAPI") != null;
		}
		catch (ClassNotFoundException e) {
			return false;
		}
	}

	private static class ReloadHandler implements IEventHandler {

		@Override
		public void handle(Object r) {
			instance.scanAndRemove(); //Do not reload, because inf loop
		}

	}

}
