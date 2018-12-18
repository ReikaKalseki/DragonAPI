/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWaySet;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class BannedItemReader {

	public static final BannedItemReader instance = new BannedItemReader();

	private final OneWaySet<KeyedItemStack> allEntries = new OneWaySet();

	public static final String PLUGIN_PATH = System.getProperty("user.dir").replaceAll("\\\\", "/")+"/plugins/";

	private BannedItemReader() {

	}

	public void initWith(String dir, String file) {
		if (file.equals("*")) {
			File f = new File(dir);
			if (f.exists() && f.isDirectory()) {
				try {
					this.parseDirectory(f);
				}
				catch (Exception e) {

				}
			}
		}
		else {
			File f = new File(dir, file);
			if (f.exists() && !f.isDirectory()) {
				try {
					this.parseFile(f);
				}
				catch (Exception e) {

				}
			}
		}
	}

	public boolean containsID(Block id) {
		return this.containsItem(new ItemStack(id, 1, OreDictionary.WILDCARD_VALUE));
	}

	public boolean containsID(Item item) {
		return this.containsItem(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
	}

	public boolean containsItem(ItemStack is) {
		return allEntries.contains(this.createKey(is));
	}

	private KeyedItemStack createKey(ItemStack is) {
		return new KeyedItemStack(is).setSimpleHash(true).setIgnoreNBT(true).setSized(false);
	}

	private void parseDirectory(File f) throws Exception {
		ArrayList<File> li = ReikaFileReader.getAllFilesInFolder(f, "yml", "txt", "dat", "cfg", "json");
		for (File in : li) {
			this.parseFile(in);
		}
	}

	private void parseFile(File f) throws Exception {
		if (f.getName().endsWith("json")) {
			this.parseJSONFile(f);
		}
		else {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line = in.readLine();
			while (line != null) {
				String oline = line;
				boolean flag = false;

				while(!line.isEmpty() && !Character.isDigit(line.charAt(0))) {
					line = line.substring(1);
				}

				if (!line.isEmpty()) {
					String[] parts = line.split(":");
					if (parts != null && parts.length > 1) {
						try {
							String id = parts[0];
							String meta = parts[1];
							try {
								int intid = Integer.parseInt(id);
								int intmeta = meta.equals("*") ? -1 : Integer.parseInt(meta);
								Item item = Item.getItemById(intid);
								allEntries.add(intmeta >= 0 ? this.createKey(new ItemStack(item, intmeta)) : this.createKey(new ItemStack(item)));
								flag = true;
							}
							catch (NumberFormatException e) {

							}
						}
						catch (Exception e) {
							//e.printStackTrace();
						}
					}
				}

				if (!flag && !oline.isEmpty()) {
					allEntries.add(this.createKey(ReikaItemHelper.lookupItem(oline)));
				}

				line = in.readLine();
			}
			in.close();
		}
	}

	private void parseJSONFile(File f) {
		JsonElement e = new JsonParser().parse(ReikaFileReader.getReader(f));
		if (e instanceof JsonObject) {
			JsonObject j = (JsonObject)e;
			for (Entry<String, JsonElement> entry : j.entrySet()) {
				JsonElement val = entry.getValue();
				JsonArray arr = val.getAsJsonArray();
				for (JsonElement idx : arr) {
					if (idx instanceof JsonObject) {
						JsonObject data = (JsonObject)idx;
						String item = data.get("item").getAsString();
						String dmg = data.get("damage").getAsString();
						ItemStack stack = ReikaItemHelper.lookupItem(item+":"+dmg);
						allEntries.add(this.createKey(stack));
					}
				}
			}
			/*
			if (j.getAsJsonPrimitive("enable").getAsBoolean()) {
				JsonArray dims = j.getAsJsonArray("dimensions");
				int x = j.getAsJsonPrimitive("x").getAsInt();
				int y = j.getAsJsonPrimitive("y").getAsInt();
				int z = j.getAsJsonPrimitive("z").getAsInt();
				String id = j.getAsJsonPrimitive("name").getAsString();
				try {
					WarpPoint p = new WarpPoint(id, new WorldLocation(dims.get(0).getAsInt(), x, y, z));
					map.add(p);
				}
				catch (Exception ex) {
					ChromatiCraft.logger.logError("Could not parse waypoint entry: "+e.toString());
					ex.printStackTrace();
				}
			}*/
		}
	}

}
