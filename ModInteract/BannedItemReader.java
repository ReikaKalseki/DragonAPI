package Reika.DragonAPI.ModInteract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.Data.ImmutableList;

public class BannedItemReader {

	public static final BannedItemReader instance = new BannedItemReader();

	private final ImmutableList<ItemBanEntry> allEntries = new ImmutableList();

	private BannedItemReader() {

	}

	public void initWith(String path) {
		String main = System.getProperty("user.dir").replaceAll("\\\\", "/");
		String file = main+"/"+path;
		File f = new File(file);
		if (f.exists()) {
			try {
				this.parseFile(f);
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
			itemID = id;
			itemDamage = -1;
		}

		public boolean hasMeta() {
			return itemDamage >= 0;
		}

		@Override
		public String toString() {
			return itemID+":"+itemDamage;
		}

		public boolean matches(ItemStack is) {
			return itemID == is.itemID && (itemDamage < 0 || itemDamage == is.getItemDamage());
		}

	}

	public boolean containsID(int id) {
		for (int i = 0; i < allEntries.size(); i++) {
			ItemBanEntry e = allEntries.get(i);
			if (e.itemID == id)
				return true;
		}
		return false;
	}

	public boolean containsItem(ItemStack is) {
		for (int i = 0; i < allEntries.size(); i++) {
			ItemBanEntry e = allEntries.get(i);
			if (e.matches(is))
				return true;
		}
		return false;
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
						e.printStackTrace();
					}
				}
			}

			line = in.readLine();
		}
		in.close();
	}

}
