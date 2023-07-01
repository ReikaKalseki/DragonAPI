/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.IO.LuaBlock.LuaBlockDatabase;
import Reika.DragonAPI.Instantiable.IO.LuaBlock.NBTLuaBlock;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.BeeGene;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.ButterflyGene;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.TreeGene;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterflyGenome;


public class CustomRecipeList {

	private final DragonAPIMod mod;
	public final String recipeType;

	private LuaBlockDatabase data = new LuaBlockDatabase();
	private final HashSet<LuaBlock> entries = new HashSet();

	private final LuaBlock exampleBlock = new ExampleLuaBlock("exampleRoot", null, new LuaBlockDatabase());

	private static final HashMap<String, Class> lookups = new HashMap();
	private static final HashMap<String, DelegateLookup> delegateCalls = new HashMap();

	static {
		if (ModList.MYSTCRAFT.isLoaded())
			delegateCalls.put("myst_page", new MystPageLookup());
		if (ModList.FORESTRY.isLoaded()) {
			delegateCalls.put("forestry_bee", new BeeLookup());
			delegateCalls.put("forestry_tree", new TreeLookup());
			delegateCalls.put("forestry_butterfly", new ButterflyLookup());
		}
	}

	private static final Pattern STACKSIZE_PATTERN = Pattern.compile("(.+?)(?:\\*(\\d+))?$");

	public CustomRecipeList(DragonAPIMod mod, String type) {
		this.mod = mod;
		recipeType = type;
	}

	public static void addFieldLookup(String key, Class c) {
		lookups.put(key, c);
	}

	public final boolean load() {
		File folder = this.getBaseFilepath();
		if (!folder.exists() || !folder.isDirectory())
			return false;
		ArrayList<File> files = ReikaFileReader.getAllFilesInFolder(folder, this.getExtension());
		this.load(files);
		return true;
	}

	public final void createFolders() {
		File folder = this.getBaseFilepath();
		folder.mkdirs();
	}

	public final LuaBlock createExample(String s) {
		return new ExampleLuaBlock(s, exampleBlock, exampleBlock.tree);
	}
	/*
	public final void addToExample(LuaBlock b) {
		exampleBlock.addChild(b.name);
	}
	 */
	public final void createExampleFile() {
		try {
			File f = new File(this.getBaseFilepath(), "example"+this.getExtension());
			f.createNewFile();
			ReikaFileReader.writeLinesToFile(f, exampleBlock.writeToStrings(), true, Charsets.UTF_8);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final boolean load(File f) {
		if (!f.exists())
			return false;
		this.load(ReikaJavaLibrary.makeListFrom(f));
		return true;
	}

	public final void load(Collection<File> files) {
		this.clear();
		for (File f : files) {
			if (ReikaFileReader.getFileNameNoExtension(f, false, false).equals("example"))
				continue;
			data.loadFromFile(f);
		}
		this.parseLuaBlocks();
	}

	public final void clear() {
		data = new LuaBlockDatabase();
		entries.clear();
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
		if (!Strings.isNullOrEmpty(recipeType))
			mod.getModLogger().log("All custom "+recipeType+" recipe entries parsed.");
	}

	public final Collection<LuaBlock> getEntries() {
		return Collections.unmodifiableCollection(entries);
	}

	private final File getBaseFilepath() {
		return new File(mod.getConfigFolder(), mod.getDisplayName()+"_"+this.getFolderName());
	}

	protected String getFolderName() {
		return "CustomRecipes";
	}

	protected String getExtension() {
		return ".recipes_"+recipeType;
	}

	public static final Object parseObjectString(String item) {
		if (item.equals("null") || item.equals("empty") || item.equals("~") || item.equals("-"))
			return null;
		if (item.startsWith("ore:"))
			return item.substring("ore:".length());
		return parseItemString(item, null, true);
	}

	public static final void writeItem(LuaBlock lb, ItemStack is) {
		String base = Item.itemRegistry.getNameForObject(is.getItem());
		if (is.stackSize > 1) {
			base = base+"*"+is.stackSize;
		}
		lb.putData("item", base);
		if (is.stackTagCompound != null) {
			new NBTLuaBlock("item_nbt", lb, lb.tree, is.stackTagCompound, false);
		}
	}

	public static final Collection<ItemStack> parseItemCollection(Collection<String> in, boolean tolerateNull) {
		Collection<ItemStack> c = new ArrayList();
		for (String s : in) {
			if (s.startsWith("ore:")) {
				s = s.substring("ore:".length());
				ArrayList<ItemStack> li = OreDictionary.getOres(s);
				if (li.isEmpty() && !tolerateNull)
					throw new IllegalArgumentException("Ore dictionary tag '"+s+"' has no items!");
				else
					c.addAll(li);
			}
			else {
				ItemStack is = parseItemString(s, null, tolerateNull);
				if (is != null)
					c.add(is);
				else if (!tolerateNull)
					throw new IllegalArgumentException("Null stack not permitted!");
			}
		}
		return c;
	}

	public static final ItemStack parseItemString(String s, LuaBlock nbt, boolean tolerateNull) {
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
		if (key.equals("delegate")) {
			lookup = lookup.substring(key.length()+1);
			DelegateLookup delegate = delegateCalls.get(lookup);
			if (delegate == null)
				throw new IllegalArgumentException("No such Delegate Lookup '"+lookup+"'!");
			ItemStack is = delegate.getItem(nbt);
			if (is == null && !tolerateNull)
				throw new IllegalArgumentException("Delegate Lookup '"+lookup+"' yielded no item!");
			return is;
		}
		else if (lookups.containsKey(key)) {
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
			if (ret == null && !tolerateNull) {
				throw new IllegalArgumentException("No such item '"+lookup+"'");
			}
		}

		ret = ReikaItemHelper.getSizedItemStack(ret, amt);

		if (ret != null && nbt != null && !nbt.isEmpty()) {
			ret.stackTagCompound = nbt.asNBT();
		}

		if (ret == null && !tolerateNull) {
			throw new IllegalArgumentException("Null stack not permitted!");
		}

		return ret;
	}

	public static final IRecipe parseCraftingRecipe(LuaBlock lb, ItemStack output) {
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
					array[i][k] = parseObjectString(item);
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
				Object o = parseObjectString(s);
				if (o == null) {
					throw new IllegalArgumentException("You cannot have blank spaces in shapeless recipes!");
				}
				inputs[i] = o;
			}
			return new ShapelessOreRecipe(output, inputs);
		}
	}

	public static final String fullID(Object o) {
		if (o instanceof ItemStack)
			return fullID((ItemStack)o);
		if (o instanceof Collection)
			return ((Collection)o).stream().map(e -> fullID(e)).collect(Collectors.toList()).toString();
		if (o instanceof Item)
			return Item.itemRegistry.getNameForObject(o)+"["+ReikaItemHelper.getRegistrantMod((Item)o)+"]";
		if (o instanceof Block)
			return Block.blockRegistry.getNameForObject(o)+"["+ReikaItemHelper.getRegistrantMod((Block)o)+"]";
		return String.valueOf(o);
	}

	public static final String fullID(ItemStack is) {
		if (is == null)
			return "[null]";
		else if (is.getItem() == null)
			return "[null-item stack]";
		return is.stackSize+"x"+Item.itemRegistry.getNameForObject(is.getItem())+"@"+is.getItemDamage()+"{"+is.stackTagCompound+"}["+ReikaItemHelper.getRegistrantMod(is)+"]";
	}

	private static class ExampleLuaBlock extends LuaBlock {

		protected ExampleLuaBlock(String n, LuaBlock lb, LuaBlockDatabase db) {
			super(n, lb, db);
		}

	}

	public static interface DelegateLookup {

		public ItemStack getItem(LuaBlock data);

	}

	private static class MystPageLookup implements DelegateLookup {

		@Override
		public ItemStack getItem(LuaBlock data) {
			return ReikaMystcraftHelper.getSymbolPage(data.getString("symbol"));
		}

	}

	private static class BeeLookup implements DelegateLookup {

		@Override
		public ItemStack getItem(LuaBlock data) {
			EnumBeeType type = EnumBeeType.valueOf(data.getString("class").toUpperCase(Locale.ENGLISH));
			ItemStack ret = ReikaBeeHelper.getBeeItem(data.getString("species"), type);
			for (int i = 0; i < EnumBeeChromosome.values().length; i++) {
				EnumBeeChromosome ec = EnumBeeChromosome.values()[i];
				if (ec != EnumBeeChromosome.SPECIES) {
					String key = ec.name().toLowerCase(Locale.ENGLISH);

					if (data.containsKey(key)) {
						IAllele ia = null;

						switch(ec) {
							case CAVE_DWELLING:
							case NOCTURNAL:
							case TOLERANT_FLYER:
								ia = ReikaBeeHelper.getBooleanAllele(data.getBoolean(key));
								break;
							case EFFECT:
							case FERTILITY:
							case FLOWER_PROVIDER:
							case FLOWERING:
							case LIFESPAN:
							case SPEED:
							case TERRITORY:
								String val = data.getString(key);
								BeeGene bg = BeeAlleleRegistry.getEnum(ec, val);
								ia = bg.getAllele();
								break;
							case HUMIDITY_TOLERANCE:
							case TEMPERATURE_TOLERANCE:
								EnumTolerance et = EnumTolerance.valueOf(data.getString(key).toUpperCase(Locale.ENGLISH));
								ia = ReikaBeeHelper.getToleranceGene(et);
								break;
							case SPECIES:
							case HUMIDITY:
							default:
								break;
						}

						if (ia != null) {
							IBeeGenome ibg = (IBeeGenome)ReikaBeeHelper.getGenome(ret);
							ReikaBeeHelper.setGene(ret, ibg, ec, ia, false);
							ReikaBeeHelper.setGene(ret, ibg, ec, ia, true);
						}
					}
				}
			}
			return ret;
		}
	}

	/** Not yet implemented. */
	private static class TreeLookup implements DelegateLookup {

		@Override
		public ItemStack getItem(LuaBlock data) {
			EnumGermlingType type = EnumGermlingType.valueOf(data.getString("class").toUpperCase(Locale.ENGLISH));
			ItemStack ret = ReikaBeeHelper.getTreeItem(data.getString("species"), type);
			for (int i = 0; i < EnumTreeChromosome.values().length; i++) {
				EnumTreeChromosome ec = EnumTreeChromosome.values()[i];
				if (ec != EnumTreeChromosome.SPECIES) {
					String key = ec.name().toLowerCase(Locale.ENGLISH);

					if (data.containsKey(key)) {
						IAllele ia = null;

						switch(ec) {
							case FIREPROOF:
								ia = ReikaBeeHelper.getBooleanAllele(data.getBoolean(key));
								break;
							case EFFECT:
							case FERTILITY:
							case FRUITS:
							case GIRTH:
							case HEIGHT:
							case MATURATION:
							case PLANT:
							case SAPPINESS:
							case TERRITORY:
							case YIELD:
							case GROWTH:
								String val = data.getString(key);
								TreeGene bg = TreeAlleleRegistry.getEnum(ec, val);
								ia = bg.getAllele();
								break;
							case SPECIES:
							default:
								break;
						}

						if (ia != null) {
							ITreeGenome ibg = (ITreeGenome)ReikaBeeHelper.getGenome(ret);
							ReikaBeeHelper.setGene(ret, ibg, ec, ia, false);
							ReikaBeeHelper.setGene(ret, ibg, ec, ia, true);
						}
					}
				}
			}
			return ret;
		}
	}

	/** Not yet implemented. */
	private static class ButterflyLookup implements DelegateLookup {

		@Override
		public ItemStack getItem(LuaBlock data) {
			EnumFlutterType type = EnumFlutterType.valueOf(data.getString("class").toUpperCase(Locale.ENGLISH));
			ItemStack ret = ReikaBeeHelper.getButterflyItem(data.getString("species"), type);
			for (int i = 0; i < EnumButterflyChromosome.values().length; i++) {
				EnumButterflyChromosome ec = EnumButterflyChromosome.values()[i];
				if (ec != EnumButterflyChromosome.SPECIES) {
					String key = ec.name().toLowerCase(Locale.ENGLISH);

					if (data.containsKey(key)) {
						IAllele ia = null;

						switch(ec) {
							case NOCTURNAL:
							case TOLERANT_FLYER:
							case FIRE_RESIST:
								ia = ReikaBeeHelper.getBooleanAllele(data.getBoolean(key));
								break;
							case METABOLISM:
								ia = ReikaBeeHelper.getIntegerAllele(data.getInt(key));
								break;
							case EFFECT:
							case TERRITORY:
							case FERTILITY:
							case FLOWER_PROVIDER:
							case LIFESPAN:
							case SIZE:
							case SPEED:
								String val = data.getString(key);
								ButterflyGene bg = ButterflyAlleleRegistry.getEnum(ec, val);
								ia = bg.getAllele();
								break;
							case HUMIDITY_TOLERANCE:
							case TEMPERATURE_TOLERANCE:
								EnumTolerance et = EnumTolerance.valueOf(data.getString(key).toUpperCase(Locale.ENGLISH));
								ia = ReikaBeeHelper.getToleranceGene(et);
								break;
							case SPECIES:
							default:
								break;
						}

						if (ia != null) {
							IButterflyGenome ibg = (IButterflyGenome)ReikaBeeHelper.getGenome(ret);
							ReikaBeeHelper.setGene(ret, ibg, ec, ia, false);
							ReikaBeeHelper.setGene(ret, ibg, ec, ia, true);
						}
					}
				}
			}
			return ret;
		}
	}

}
