/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWaySet;

public final class BannedItemReader {

	public static final BannedItemReader instance = new BannedItemReader();

	private final OneWayList<ItemBanEntry> allEntries = new OneWayList();
	private final OneWaySet<Integer> allIds = new OneWaySet();

	private BannedItemReader() {

	}

	public void initWith(String path) {
		String main = System.getProperty("user.dir").replaceAll("\\\\", "/");
		String file = main+"/plugins/"+path;
		File f = new File(file);
		if (f.exists() && f.isDirectory()) {
			try {
				this.parseDirectory(f);
			}
			catch (Exception e) {}
		}
	}

	private static final class ItemBanEntry {

		public final int itemID;
		public final int itemDamage;

		private ItemBanEntry(int id, int meta) {
			itemID = id;
			itemDamage = meta;
		}

		private ItemBanEntry(int id) {
			this(id, -1);
		}

		public boolean hasMeta() {
			return itemDamage >= 0;
		}

		@Override
		public String toString() {
			return itemID+":"+itemDamage;
		}

		public boolean matches(ItemStack is) {
			return itemID == Item.getIdFromItem(is.getItem()) && (itemDamage < 0 || itemDamage == is.getItemDamage());
		}

	}

	public boolean containsID(Block id) {
		return this.containsID(Block.getIdFromBlock(id));
	}

	public boolean containsID(Item item) {
		return this.containsID(Item.getIdFromItem(item));
	}

	private boolean containsID(int id) {
		return allIds.contains(id);
	}

	public boolean containsItem(ItemStack is) {
		for (int i = 0; i < allEntries.size(); i++) {
			ItemBanEntry e = allEntries.get(i);
			if (e.matches(is))
				return true;
		}
		return false;
	}

	private void parseDirectory(File f) throws Exception {
		ArrayList<File> li = ReikaFileReader.getAllFilesInFolder(f, "yml", "txt", "dat", "cfg");
		for (int i = 0; i < li.size(); i++) {
			this.parseFile(li.get(i));
		}
	}

	private void parseFile(File f) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		String line = in.readLine();
		while (line != null) {
			String oline = line;
			while(!line.isEmpty() && !Character.isDigit(line.charAt(0))) {
				line = line.substring(1);
			}

			if (!line.isEmpty()) {
				String[] parts = line.split(":");
				if (parts != null && parts.length > 1) {
					try {
						String id = parts[0];
						String meta = parts[1];
						int intid = Integer.parseInt(id);
						int intmeta = meta.equals("*") ? -1 : Integer.parseInt(meta);
						allEntries.add(new ItemBanEntry(intid, intmeta));
					}
					catch (Exception e) {
						//e.printStackTrace();
					}
				}
			}

			line = in.readLine();
		}
		in.close();
	}

}
