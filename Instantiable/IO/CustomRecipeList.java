package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.IO.LuaBlock.LuaBlockDatabase;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import com.google.common.base.Strings;


public class CustomRecipeList {

	private final DragonAPIMod mod;
	public final String recipeType;

	private final LuaBlockDatabase data = new LuaBlockDatabase();
	private final HashSet<LuaBlock> entries = new HashSet();

	private final HashMap<String, Class> lookups = new HashMap();

	private static final Pattern STACKSIZE_PATTERN = Pattern.compile("(.+?)(?:\\*(\\d+))?$");

	public CustomRecipeList(DragonAPIMod mod, String type) {
		this.mod = mod;
		recipeType = type;
	}

	public void addFieldLookup(String key, Class c) {
		lookups.put(key, c);
	}

	public final void load() {
		File folder = new File(this.getBaseFilepath());
		if (!folder.exists() || !folder.isDirectory())
			return;
		ArrayList<File> files = ReikaFileReader.getAllFilesInFolder(folder, this.getExtension());
		for (File f : files) {
			data.loadFromFile(f);
		}
		this.parseLuaBlocks();
	}

	private void parseLuaBlocks() {
		LuaBlock root = data.getRootBlock();
		for (LuaBlock b : root.getChildren()) {
			try {
				data.addBlock(b.getString("type"), b);
				mod.getModLogger().debug("Loaded recipe prototype:\n"+b.getString("type"));
				entries.add(b);
			}
			catch (Exception e) {
				mod.getModLogger().logError("Could not parse custom recipe section "+b.getString("type")+": ");
				e.printStackTrace();
			}
		}
		mod.getModLogger().log("All custom "+recipeType+" recipe entries parsed.");
	}

	public Collection<LuaBlock> getEntries() {
		return Collections.unmodifiableCollection(entries);
	}

	private final String getBaseFilepath() {
		return mod.getConfigFolder().getAbsolutePath()+"/"+mod.getDisplayName()+"_CustomRecipes/";
	}

	private String getExtension() {
		return ".recipes_"+recipeType;
	}

	public final Object parseObjectString(String item) {
		if (item.equals("null") || item.equals("empty") || item.equals("~") || item.equals("-"))
			return null;
		if (item.startsWith("ore:"))
			return item.substring("ore:".length());
		return this.parseItemString(item, null, true);
	}

	public final Collection<ItemStack> parseItemCollection(Collection<String> in, boolean tolerateNull) {
		Collection<ItemStack> c = new ArrayList();
		for (String s : in) {
			ItemStack is = this.parseItemString(s, null, tolerateNull);
			if (is != null)
				c.add(is);
			else if (!tolerateNull)
				throw new IllegalArgumentException("Null stack not permitted!");
		}
		return c;
	}

	public final ItemStack parseItemString(String s, LuaBlock nbt, boolean tolerateNull) {
		if (Strings.isNullOrEmpty(s)) {
			if (tolerateNull)
				return null;
			else
				throw new IllegalArgumentException("Null stack not permitted!");
		}
		String lookup = s;
		ItemStack ret = null;
		Matcher m = STACKSIZE_PATTERN.matcher(s);
		int amt = 1;
		if (m.find() && m.group(2) != null) {
			lookup = m.group(1);
			amt = Integer.parseInt(m.group(2));
			if (amt > 64) {
				throw new IllegalArgumentException("Stack size of "+amt+" is too large!");
			}
			if (amt <= 0) {
				throw new IllegalArgumentException("Stack size of "+amt+" is zero!");
			}
		}

		String key = s.substring(0, s.indexOf(':'));
		if (lookups.containsKey(key)) {
			try {
				lookup = lookup.substring(key.length()+1);
				ret = (ItemStack)lookups.get(key).getField(lookup).get(null);
			}
			catch (Exception e) {
				throw new IllegalArgumentException("No internal stack '"+lookup+"'");
			}
		}
		else {
			ret = ReikaItemHelper.lookupItem(lookup);
			if (ret == null) {
				throw new IllegalArgumentException("No such item '"+lookup+"'");
			}
		}

		ret = ReikaItemHelper.getSizedItemStack(ret, amt);

		if (ret != null && nbt != null) {
			ret.stackTagCompound = ReikaNBTHelper.constructNBT(nbt);
		}

		if (ret == null && !tolerateNull) {
			throw new IllegalArgumentException("Null stack not permitted!");
		}

		return ret;
	}

	public final IRecipe parseCraftingRecipe(LuaBlock lb, ItemStack output) {
		boolean shaped = lb.getBoolean("shaped");
		if (shaped) {
			String input1 = lb.containsKey("input_top") ? lb.getString("input_top").replaceAll(" ", "") : null;
			String input2 = lb.containsKey("input_middle") ? lb.getString("input_middle").replaceAll(" ", "") : null;
			String input3 = lb.containsKey("input_bottom") ? lb.getString("input_bottom").replaceAll(" ", "") : null;
			String[] top = input1 != null ? input1.split(",") : null;
			String[] middle = input2 != null ? input2.split(",") : null;
			String[] bottom = input3 != null ? input3.split(",") : null;
			int w = 0;
			if (top != null) {
				w = top.length;
			}
			if (middle != null) {
				if (w != 0 && w != middle.length) {
					throw new IllegalArgumentException("Rows must be of equal length!");
				}
				w = middle.length;
			}
			if (bottom != null) {
				if (w != 0 && w != bottom.length) {
					throw new IllegalArgumentException("Rows must be of equal length!");
				}
				w = bottom.length;
			}
			if (w > 3) {
				throw new IllegalArgumentException("Rows must be at most three entries long!");
			}
			ArrayList<String[]> rows = new ArrayList();
			if (top != null)
				rows.add(top);
			if (middle != null)
				rows.add(middle);
			if (bottom != null)
				rows.add(bottom);
			Object[][] array = new Object[rows.size()][w];
			for (int i = 0; i < rows.size(); i++) {
				for (int k = 0; k < w; k++) {
					String item = rows.get(i)[k];
					array[i][k] = this.parseObjectString(item);
				}
			}
			return new ShapedOreRecipe(output, ReikaRecipeHelper.decode2DArray(array));
		}
		else {
			String input = lb.getString("input").replaceAll(" ", "");
			String[] parts = input.split(",");
			if (parts.length > 9)
				throw new IllegalArgumentException("You cannot have more than nine items in recipes!");
			Object[] inputs = new Object[parts.length];
			for (int i = 0; i < parts.length; i++) {
				String s = parts[i];
				Object o = this.parseObjectString(s);
				if (o == null) {
					throw new IllegalArgumentException("You cannot have blank spaces in shapeless recipes!");
				}
				inputs[i] = o;
			}
			return new ShapelessOreRecipe(output, inputs);
		}
	}

}
