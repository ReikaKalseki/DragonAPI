/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.Side;

public abstract class PlayerTracker {

	private final ArrayList<String> players = new ArrayList();
	private final String saveFileName;

	public PlayerTracker(String name) {
		saveFileName = name;
	}

	public void onLoad() {
		this.read();
	}

	public abstract void onNewPlayer(EntityPlayer ep);

	public final boolean hasPlayer(EntityPlayer ep) {
		//ReikaJavaLibrary.pConsole(this+":"+players);
		//ReikaJavaLibrary.pConsole(players.contains(ep.getEntityName()));
		return players.contains(ep.getEntityName());
	}

	public final void addPlayer(EntityPlayer ep) {
		players.add(ep.getEntityName());
	}

	public void onUnload() {
		this.save();
	}

	public final String getSaveFileName() {
		return saveFileName+".mcpt";
	}

	public final String getSaveFilePath() {
		File save = DimensionManager.getCurrentSaveRootDirectory();
		if (save == null) {
			ReikaJavaLibrary.pConsole("DragonAPI: Cannot save player data for a null world!");
			return "";
		}
		return save.getPath().substring(2)+"/DragonAPI/PlayerTrackers/";
	}

	public final String getFullSavePath() {
		return this.getSaveFilePath()+this.getSaveFileName();
	}

	@Override
	public final String toString() {
		return this.getSaveFileName();
	}

	private final void save() {
		ReikaJavaLibrary.pConsole("DRAGONAPI: Saving player tracker "+this.toString(), Side.SERVER);
		String name = this.getSaveFileName();
		try {
			String s = this.getSaveFilePath();
			if (!s.isEmpty()) {
				File dir = new File(s);
				//ReikaJavaLibrary.pConsole(this.getSaveFilePath(), Side.SERVER);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File f = new File(this.getFullSavePath());
				if (f.exists())
					f.delete();
				f.createNewFile();
				PrintWriter p = new PrintWriter(f);
				for (int i = 0; i < players.size(); i++) {
					String line = players.get(i);
					p.append(line+"\n");
				}
				p.close();
			}
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+e.getMessage()+", and it caused the save to fail!", Side.SERVER);
			e.printStackTrace();
		}
	}

	private final void read() {
		ReikaJavaLibrary.pConsole("DRAGONAPI: Loading player tracker "+this.toString(), Side.SERVER);
		String name = this.getSaveFileName();
		try {
			BufferedReader p = new BufferedReader(new InputStreamReader(new FileInputStream(this.getFullSavePath())));
			String line = "";
			while (line != null) {
				line = p.readLine();
				if (line != null) {
					if (!players.contains(line))
						players.add(line);
				}
			}
			p.close();
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+e.getMessage()+", and it caused the read to fail!", Side.SERVER);
			e.printStackTrace();
		}
	}
}
