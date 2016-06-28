/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.instantiable.io.ControlledConfig;
import reika.dragonapi.io.ReikaFileReader;
import reika.dragonapi.libraries.java.ReikaStringParser;

public class PackModificationTracker {

	public static final PackModificationTracker instance = new PackModificationTracker();

	private final HashMap<DragonAPIMod, ArrayList<PackModification>> data = new HashMap();
	private final HashMap<DragonAPIMod, String> filepaths = new HashMap();

	private PackModificationTracker() {

	}

	public List<PackModification> getModifications(DragonAPIMod mod) {
		ArrayList<PackModification> li = data.get(mod);
		return li != null ? Collections.unmodifiableList(li) : null;
	}

	public boolean modificationsExist(DragonAPIMod mod) {
		return data.get(mod) != null && !data.get(mod).isEmpty();
	}

	public void addMod(DragonAPIMod mod, ControlledConfig cfg) {
		this.addMod(mod, this.getConfigFolderBasedPath(mod, cfg));
	}

	public void addMod(DragonAPIMod mod, String path) {
		if (!filepaths.containsKey(mod))
			filepaths.put(mod, path);
	}

	public void loadAll() {
		for (DragonAPIMod mod : filepaths.keySet()) {
			this.load(mod);
		}
	}

	private void load(DragonAPIMod mod) {
		mod.getModLogger().log("Loading pack modification log file.");
		try {
			File f = new File(filepaths.get(mod));
			if (!f.exists())
				this.createDataFile(f, mod);

			BufferedReader p = ReikaFileReader.getReader(f);
			String line = "";
			while (line != null) {
				line = p.readLine();
				if (line != null && !line.isEmpty() && !line.startsWith("//")) {
					PackModification entry = this.parseString(line);
					if (entry != null) {
						this.addEntry(mod, entry);
					}
					else {
						throw new IllegalArgumentException("Invalid modification entry formatting: '"+line+"'");
					}
				}
			}
			p.close();
		}
		catch (Exception e) {
			throw new RuntimeException("Invalid pack modification file: "+e.getLocalizedMessage(), e);
		}
	}

	private void addEntry(DragonAPIMod mod, PackModification entry) {
		ArrayList<PackModification> li = data.get(mod);
		if (li == null) {
			li = new ArrayList();
			data.put(mod, li);
		}
		li.add(entry);
		Collections.sort(li, modComparator);
	}

	public final String getBasicSaveFileName(DragonAPIMod mod) {
		return ReikaStringParser.stripSpaces(mod.getTechnicalName())+"_PackModifications.cfg";
	}

	private final String getConfigFolderBasedPath(DragonAPIMod mod, ControlledConfig cfg) {
		return cfg.getConfigFolder().getAbsolutePath()+"/"+this.getBasicSaveFileName(mod);
	}

	private void createDataFile(File f, DragonAPIMod mod) throws Exception {
		f.createNewFile();
		PrintWriter p = new PrintWriter(f);
		this.writeCommentLine(p, "-------------------------------");
		this.writeCommentLine(p, " "+mod.getDisplayName()+" Pack Modification Log File ");
		this.writeCommentLine(p, "-------------------------------");
		this.writeCommentLine(p, "");
		this.writeCommentLine(p, "Use this file to specify any changes you are making to "+mod.getDisplayName()+" for your modpack.");
		this.writeCommentLine(p, "Specify one per line, and format them in one of the following ways:");
		this.writeCommentLine(p, "Description OR Description:Reason");
		this.writeCommentLine(p, "");
		this.writeCommentLine(p, "Sample Lines:");
		this.writeCommentLine(p, "\tChanged compressor recipe to use GT steel");
		this.writeCommentLine(p, "\tReplaced gold ingot in ignition unit recipe with signalum:Small balance tweak");
		this.writeCommentLine(p, "");
		this.writeCommentLine(p, "Entries missing a description, or with more than one colon separator, are incorrect.");
		this.writeCommentLine(p, "Incorrectly formatted lines will throw an exception.");
		this.writeCommentLine(p, "Lines beginning with '//' are comments and will be ignored, as will empty lines.");
		this.writeCommentLine(p, "");
		this.writeCommentLine(p, "NOTE WELL: Any changes you make to the pack MUST be specified here to avoid confusing users.");
		this.writeCommentLine(p, "\tAny changes not explained here will be assumed to be intentionally hidden, and");
		this.writeCommentLine(p, "\tyou will lose permission to make the changes.");
		this.writeCommentLine(p, "====================================================================================");
		p.append("\n");
		p.close();
	}

	private static void writeCommentLine(PrintWriter p, String line) {
		p.append("// "+line+"\n");
	}

	private PackModification parseString(String s) throws Exception {
		s = s.replace("\n", "").replace("\r", "").replace("\t", "");
		String[] parts = s.split(":");
		int n = parts.length-1;
		switch(n) {
		case 0:
			return new PackModification(s);
		case 1:
			return new PackModification(parts[0], parts[1]);
		default:
			throw new IllegalArgumentException("Invalid line formatting: '"+s+"' has too many ("+n+") separators!");
		}
	}

	public static class PackModification {

		private final String desc;
		private final String reason;

		private PackModification(String s) {
			this(s, null);
		}

		private PackModification(String s, String r) {
			desc = s;
			reason = r;
		}

		@Override
		public String toString() {
			return EnumChatFormatting.RED+"# "+EnumChatFormatting.WHITE+(reason != null && !reason.isEmpty() ? desc+"; "+reason : desc);
		}

	}

	private static final Comparator modComparator = new ModComparator();

	private static class ModComparator implements Comparator<PackModification> {

		@Override
		public int compare(PackModification o1, PackModification o2) {
			return 0;
		}

	}

}
